package com.example.dogapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.dogapp.data.local.dao.CitaDao
import com.example.dogapp.data.local.entity.CitaEntity
import kotlinx.coroutines.launch

/**
 * HU 4.0 - Subtarea "DetalleViewModel con LiveData<Cita?> por citaId".
 *
 * Sigue el mismo patron del HomeViewModel: el DAO se inyecta por constructor y el Flow de Room
 * se convierte una sola vez con asLiveData(). Asi el detalle tambien se refresca solo si la cita
 * cambia en la base de datos.
 *
 * La pantalla solo recibe el ID de la cita (no el objeto completo) y aqui se recupera de Room.
 */
class DetalleViewModel(
    private val dao: CitaDao,
    private val citaId: Long
) : ViewModel() {

    /** La cita pedida; llega null cuando no existe (o ya fue eliminada). */
    val cita: LiveData<CitaEntity?> = dao.observarPorId(citaId).asLiveData()

    private val _citaEliminada = MutableLiveData(false)

    /** Pasa a true cuando Room termino de borrar la cita: es la senal para volver al Home. */
    val citaEliminada: LiveData<Boolean> = _citaEliminada

    /**
     * Es true desde que se pide el borrado. Al borrar, Room emite null en [cita] antes de que
     * termine eliminar(); la pantalla usa este flag para no confundir ese null con
     * "cita no encontrada".
     */
    var eliminando = false
        private set

    /** Elimina la cita de Room (sin dialogo de confirmacion, segun el criterio 4 de la HU). */
    fun eliminar() {
        if (eliminando) return
        eliminando = true
        viewModelScope.launch {
            dao.eliminarPorId(citaId)
            _citaEliminada.value = true
        }
    }

    companion object {
        fun fabrica(dao: CitaDao, citaId: Long): ViewModelProvider.Factory = viewModelFactory {
            initializer { DetalleViewModel(dao, citaId) }
        }
    }
}
