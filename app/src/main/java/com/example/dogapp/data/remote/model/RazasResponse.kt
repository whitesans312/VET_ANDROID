package com.example.dogapp.data.remote.model

data class RazasResponse(
    val message: Map<String, List<String>>,
    val status: String
)

data class ImagenRazaResponse(
    val message: String,
    val status: String
)
