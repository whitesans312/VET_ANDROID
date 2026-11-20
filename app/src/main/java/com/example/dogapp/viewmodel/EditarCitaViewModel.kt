package com.example.dogapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.dogapp.data.local.entity.CitaEntity
import com.example.dogapp.data.remote.model.RazaMascota
import com.example.dogapp.data.repository.CitaRepository
import kotlinx.coroutines.launch

class EditarCitaViewModel(
    private val repository: CitaRepository,
    citaId: Long
) : ViewModel() {

    val cita: LiveData<CitaEntity?> =
        repository.observarCita(citaId).asLiveData()

    private val _razas = MutableLiveData<List<RazaMascota>>()
    val razas: LiveData<List<RazaMascota>> = _razas

    private val _imagenUrl = MutableLiveData<String?>()
    val imagenUrl: LiveData<String?> = _imagenUrl

    private val _guardado = MutableLiveData<Boolean?>()
    val guardado: LiveData<Boolean?> = _guardado

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        cargarRazas()
    }

    private fun cargarRazas() {
        viewModelScope.launch {
            try {
                _razas.value = repository.obtenerRazas()
            } catch (e: Exception) {
                _error.value = "No fue posible cargar las razas"
            }
        }
    }

    fun buscarImagenRaza(raza: RazaMascota) {
        viewModelScope.launch {
            try {
                _imagenUrl.value =
                    repository.obtenerImagenRaza(raza.rutaApi)
            } catch (e: Exception) {
                _error.value = "No fue posible cargar la imagen de la raza"
            }
        }
    }

    fun actualizarCita(cita: CitaEntity) {
        viewModelScope.launch {
            try {
                val filasActualizadas =
                    repository.actualizarCita(cita)

                _guardado.value = filasActualizadas > 0

                if (filasActualizadas == 0) {
                    _error.value = "No se encontró la cita para actualizar"
                }
            } catch (e: Exception) {
                _guardado.value = false
                _error.value = "Ocurrió un error al actualizar la cita"
            }
        }
    }

    fun limpiarResultado() {
        _guardado.value = null
        _error.value = null
    }

    companion object {

        fun fabrica(
            repository: CitaRepository,
            citaId: Long
        ): ViewModelProvider.Factory = viewModelFactory {

            initializer {
                EditarCitaViewModel(
                    repository,
                    citaId
                )
            }
        }
    }
}