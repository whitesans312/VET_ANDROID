package com.example.dogapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "citas")
data class CitaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "nombre_mascota")
    val nombreMascota: String,

    val raza: String,

    @ColumnInfo(name = "nombre_propietario")
    val nombrePropietario: String,

    val telefono: String,
    val sintoma: String,

    @ColumnInfo(name = "imagen_url")
    val imagenUrl: String? = null
)
