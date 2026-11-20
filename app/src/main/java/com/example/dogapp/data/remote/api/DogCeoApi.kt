package com.example.dogapp.data.remote.api

import com.example.dogapp.data.remote.model.ImagenRazaResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface DogCeoApi {

    @GET("api/breeds/list/all")
    suspend fun obtenerRazas(): Response<com.example.dogapp.data.remote.model.RazasResponse>


    @GET("api/breed/{rutaRaza}/images/random")
    suspend fun obtenerImagenRaza(
        @Path(value = "rutaRaza", encoded = true)
        rutaRaza: String
    ): Response<ImagenRazaResponse>
}