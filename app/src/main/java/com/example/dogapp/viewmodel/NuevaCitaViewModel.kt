package com.example.dogapp.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.dogapp.data.remote.model.RazaMascota
import com.example.dogapp.data.repository.CitaRepository
import kotlinx.coroutines.launch
import com.example.dogapp.data.local.entity.CitaEntity
import kotlin.math.sin

class NuevaCitaViewModel (
    private val repository: CitaRepository
): ViewModel(){
    // Lista completa de razas obtenidas de Retrofit
    private var listaTodasLasRazas: List <RazaMascota> = emptyList()

    // LiveData que expone la lista de nombres de razas filtradas para la Vista
    private val _razasFiltradas = MutableLiveData<List<String>>()
    val razasFiltradas: LiveData<List<String>> = _razasFiltradas

    // Estado para saber cuándo se guardó la cita exitosamente

    private val _guardadoExitoso = MutableLiveData<Boolean>()
    val guardadoExitoso: LiveData<Boolean> = _guardadoExitoso

    // Para manejar errores si la API o el guardado fallan
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        cargarRazas()

    }
    // Llama a la API de Dog CEO mediante Retrofit
    private fun cargarRazas(){
        viewModelScope.launch {
            try {
                listaTodasLasRazas = repository.obtenerRazas()
            } catch (e: Exception ) {
                _error.value = "Error al cargar las razas desde la API"
            }
        }
    }

    //Filtro por prefijo y umbral >= 2 letras
    fun filtrarRazas(texto: String) {
        if (texto.length >= 2) {
            val filtradas = listaTodasLasRazas
                .map {it.nombre }
                .filter {nombre -> nombre.startsWith(texto, ignoreCase = true) }

            _razasFiltradas.value = filtradas

        } else {
            _razasFiltradas.value = emptyList()
        }
    }

    // Función para obtener el objeto RazaMascota por nombre seleccionado
    fun obtenerRazaPorNombre(nombre:String): RazaMascota? {
        return listaTodasLasRazas.find { it.nombre.equals(nombre, ignoreCase = true) }

    }
    // Inserta la cita en la base de datos (Room)
    fun guardarCita(nombreMascota: String, raza: String, propietario: String, telefono: String, sintoma: String) {
        viewModelScope.launch {
            try {
                val razaObj = obtenerRazaPorNombre(raza)
                var urlImagen = ""

                if (razaObj != null) {
                    urlImagen = repository.obtenerImagenRaza(razaObj.rutaApi)
                }

                val nuevaCita = CitaEntity(
                    nombreMascota = nombreMascota,
                    raza = raza,
                    nombrePropietario = propietario,
                    telefono = telefono,
                    sintoma = sintoma,
                    imagenUrl = urlImagen
                )
                // Guardar en la BD local mediante el DAO
                repository.insertarCita(nuevaCita)

                _guardadoExitoso.value = true
            } catch (e: Exception) {
                _error.value = "Error al guardar la cita"
            }

        }
    }
    companion object {
        fun fabrica(repository: CitaRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { NuevaCitaViewModel(repository) }

        }
    }
}