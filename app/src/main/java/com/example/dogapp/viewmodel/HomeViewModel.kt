package com.example.dogapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.dogapp.data.local.dao.CitaDao
import com.example.dogapp.data.local.entity.CitaEntity

/**
 * HU 2.0 - Subtarea "HomeViewModel observando LiveData<List<Cita>> del DAO" (Medina Hernan).
 *
 * El DAO expone un Flow (patron que ya traia el proyecto), asi que aqui se convierte
 * una sola vez con asLiveData(): el Home queda observando LiveData y se refresca solo
 * cuando Room avisa de un insert, update o delete.
 *
 * Se inyecta el DAO por constructor para poder usar un doble de prueba en los tests.
 */
class HomeViewModel(dao: CitaDao) : ViewModel() {

    val citas: LiveData<List<CitaEntity>> = dao.observarTodas().asLiveData()

    companion object {
        fun fabrica(dao: CitaDao): ViewModelProvider.Factory = viewModelFactory {
            initializer { HomeViewModel(dao) }
        }
    }
}
