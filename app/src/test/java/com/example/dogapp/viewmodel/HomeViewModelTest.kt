package com.example.dogapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.dogapp.data.local.dao.FakeCitaDao
import com.example.dogapp.data.local.entity.CitaEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * HU 2.0 - Respaldo del QA de la subtarea de Medina Hernan.
 *
 * Comprueba que el [HomeViewModel] entrega las citas que vienen del DAO y que el
 * LiveData se refresca cuando el DAO emite un cambio (crear, editar o borrar),
 * es decir el criterio "refresco automatico" sin emulador ni Room real.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val reglaDeHiloPrincipal = InstantTaskExecutorRule()

    private val despachadorDePrueba = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(despachadorDePrueba)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `entrega las citas que vienen del DAO`() = runTest {
        val viewModel = HomeViewModel(
            FakeCitaDao(
                listOf(
                    cita(id = 1, nombre = "Luna", sintoma = "Cojera"),
                    cita(id = 2, nombre = "Rocky", sintoma = "Vacunacion")
                )
            )
        )

        val sonda = viewModel.citas.sondar()

        assertEquals(listOf("Luna", "Rocky"), sonda.ultimo().map { it.nombreMascota })
    }

    @Test
    fun `arranca en lista vacia cuando no hay citas`() = runTest {
        val viewModel = HomeViewModel(FakeCitaDao())

        val sonda = viewModel.citas.sondar()

        assertEquals(emptyList<CitaEntity>(), sonda.ultimo())
    }

    @Test
    fun `se refresca solo cuando el DAO emite un cambio`() = runTest {
        val dao = FakeCitaDao(listOf(cita(id = 1, nombre = "Luna", sintoma = "Cojera")))
        val viewModel = HomeViewModel(dao)
        val sonda = viewModel.citas.sondar()

        assertEquals(1, sonda.ultimo().size)

        dao.insertar(cita(id = 0, nombre = "Rocky", sintoma = "Control"))

        assertEquals(2, sonda.ultimo().size)
        assertEquals(listOf("Luna", "Rocky"), sonda.ultimo().map { it.nombreMascota })
    }

    @Test
    fun `refleja la edicion y el borrado de una cita`() = runTest {
        val original = cita(id = 1, nombre = "Luna", sintoma = "Cojera")
        val dao = FakeCitaDao(listOf(original))
        val viewModel = HomeViewModel(dao)
        val sonda = viewModel.citas.sondar()

        dao.actualizar(original.copy(sintoma = "Cojera leve"))
        assertEquals("Cojera leve", sonda.ultimo().single().sintoma)

        dao.eliminarPorId(1)
        assertEquals(emptyList<CitaEntity>(), sonda.ultimo())
    }

    private fun cita(id: Long, nombre: String, sintoma: String = "Sintoma") = CitaEntity(
        id = id,
        nombreMascota = nombre,
        raza = "Criollo",
        nombrePropietario = "Propietario",
        telefono = "3000000000",
        sintoma = sintoma
    )
}

/**
 * Observa el LiveData sin ciclo de vida y guarda cada valor emitido, para poder
 * afirmar sobre el ultimo estado del listado.
 */
private class SondaDescartable<T>(private val liveData: LiveData<T>) : Observer<T> {

    private val valores = mutableListOf<T>()

    override fun onChanged(value: T) {
        valores += value
    }

    fun ultimo(): T = valores.lastOrNull()
        ?: error("El LiveData no emitio ningun valor")

    fun descartar() = liveData.removeObserver(this)
}

private fun <T> LiveData<T>.sondar(): SondaDescartable<T> =
    SondaDescartable(this).also { observeForever(it) }
