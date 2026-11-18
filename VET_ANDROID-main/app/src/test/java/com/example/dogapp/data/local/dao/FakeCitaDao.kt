package com.example.dogapp.data.local.dao

import com.example.dogapp.data.local.entity.CitaEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

/**
 * Doble de prueba del [CitaDao]: guarda las citas en memoria y notifica a los
 * observadores igual que Room, para probar el Home sin emulador ni base de datos.
 */
class FakeCitaDao(citas: List<CitaEntity> = emptyList()) : CitaDao {

    private val datos = MutableStateFlow(citas)
    private var siguienteId = (citas.maxOfOrNull { it.id } ?: 0L) + 1

    override suspend fun insertar(cita: CitaEntity): Long {
        val nueva = cita.copy(id = siguienteId++)
        datos.value = datos.value + nueva
        return nueva.id
    }

    override fun observarTodas(): Flow<List<CitaEntity>> = datos.asStateFlow()

    override fun observarPorId(id: Long): Flow<CitaEntity?> =
        datos.map { lista -> lista.firstOrNull { it.id == id } }

    override suspend fun obtenerPorId(id: Long): CitaEntity? =
        datos.value.firstOrNull { it.id == id }

    override suspend fun actualizar(cita: CitaEntity): Int {
        val lista = datos.value
        if (lista.none { it.id == cita.id }) return 0
        datos.value = lista.map { if (it.id == cita.id) cita else it }
        return 1
    }

    override suspend fun eliminarPorId(id: Long): Int {
        val lista = datos.value
        if (lista.none { it.id == id }) return 0
        datos.value = lista.filterNot { it.id == id }
        return 1
    }

    override suspend fun contar(): Int = datos.value.size
}
