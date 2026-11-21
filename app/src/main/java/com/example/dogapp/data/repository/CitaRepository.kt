package com.example.dogapp.data.repository

import com.example.dogapp.data.local.dao.CitaDao
import com.example.dogapp.data.local.entity.CitaEntity
import com.example.dogapp.data.remote.api.DogCeoApi
import com.example.dogapp.data.remote.model.RazaMascota
import kotlinx.coroutines.flow.Flow

class CitaRepository(
    private val citaDao: CitaDao,
    private val dogCeoApi: DogCeoApi
) {

    fun observarCita(id: Long): Flow<CitaEntity?> {
        return citaDao.observarPorId(id)
    }

    suspend fun actualizarCita(cita: CitaEntity): Int {
        return citaDao.actualizar(cita)
    }

    suspend fun obtenerRazas(): List<RazaMascota> {

        val respuesta = dogCeoApi.obtenerRazas()

        if (!respuesta.isSuccessful) {
            throw Exception("No se pudieron consultar las razas")
        }

        val mapaRazas = respuesta.body()?.message
            ?: throw Exception("La API no devolvió información")

        return mapaRazas.flatMap { (raza, subrazas) ->

            if (subrazas.isEmpty()) {
                listOf(
                    RazaMascota(
                        nombre = raza.replaceFirstChar { it.uppercase() },
                        rutaApi = raza
                    )
                )
            } else {
                subrazas.map { subraza ->

                    RazaMascota(
                        nombre = "${raza.replaceFirstChar { it.uppercase() }} ${
                            subraza.replaceFirstChar { it.uppercase() }
                        }",
                        rutaApi = "$raza/$subraza"
                    )
                }
            }
        }.sortedBy { it.nombre }
    }

    suspend fun obtenerImagenRaza(rutaApi: String): String {

        val respuesta = dogCeoApi.obtenerImagenRaza(rutaApi)

        if (!respuesta.isSuccessful) {
            throw Exception("No se pudo obtener la imagen de la raza")
        }

        return respuesta.body()?.message
            ?: throw Exception("La API no devolvió una imagen")
    }
    suspend fun insertarCita(cita: CitaEntity): Long {
        return citaDao.insertar(cita)
    }
}