package com.example.dogapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * HU 4.0 - Respaldo del detalle sin emulador: la cita llega por citaId y eliminar borra
 * solo esa cita y avisa para volver al Home.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DetalleViewModelTest {

    @get:Rule
    val reglaDeHiloPrincipal = InstantTaskExecutorRule()

    private val observadorVacio = Observer<CitaEntity?> { }

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `entrega la cita que corresponde al citaId`() = runTest {
        val viewModel = DetalleViewModel(FakeCitaDao(listOf(cita(1, "Luna"), cita(2, "Rocky"))), citaId = 2)
        viewModel.cita.observeForever(observadorVacio)

        assertEquals("Rocky", viewModel.cita.value?.nombreMascota)

        viewModel.cita.removeObserver(observadorVacio)
    }

    @Test
    fun `entrega null cuando la cita no existe`() = runTest {
        val viewModel = DetalleViewModel(FakeCitaDao(listOf(cita(1, "Luna"))), citaId = 99)
        viewModel.cita.observeForever(observadorVacio)

        assertNull(viewModel.cita.value)
        assertFalse(viewModel.eliminando)

        viewModel.cita.removeObserver(observadorVacio)
    }

    @Test
    fun `eliminar borra solo esa cita y avisa para volver al Home`() = runTest {
        val dao = FakeCitaDao(listOf(cita(1, "Luna"), cita(2, "Rocky")))
        val viewModel = DetalleViewModel(dao, citaId = 1)
        viewModel.cita.observeForever(observadorVacio)

        viewModel.eliminar()

        assertTrue(viewModel.eliminando)
        assertEquals(true, viewModel.citaEliminada.value)
        assertNull(dao.obtenerPorId(1))
        assertEquals("Rocky", dao.obtenerPorId(2)?.nombreMascota)
        assertNull(viewModel.cita.value)

        viewModel.cita.removeObserver(observadorVacio)
    }

    private fun cita(id: Long, nombre: String) = CitaEntity(
        id = id,
        nombreMascota = nombre,
        raza = "Criollo",
        nombrePropietario = "Propietario",
        telefono = "3000000000",
        sintoma = "Sintoma"
    )
}
