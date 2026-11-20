package com.example.dogapp.data.local.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.dogapp.data.local.entity.CitaEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DatabaseProvider {
    private const val DATABASE_NAME = "dogapp_database"

    @Volatile
    private var instance: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase =
        instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            ).addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    instance?.let { database ->
                        CoroutineScope(Dispatchers.IO).launch {
                            sembrarCitasIniciales(database)
                        }
                    }
                }
            }).build().also { database ->
                instance = database
                CoroutineScope(Dispatchers.IO).launch {
                    if (database.citaDao().contar() == 0) {
                        sembrarCitasIniciales(database)
                    }
                }
            }
        }

    private suspend fun sembrarCitasIniciales(database: AppDatabase) {
        val citasDeEjemplo = listOf(
            CitaEntity(
                nombreMascota = "Luna",
                raza = "Labrador",
                nombrePropietario = "Ana Gómez",
                telefono = "3001234567",
                sintoma = "Cojera de la pata trasera derecha"
            ),
            CitaEntity(
                nombreMascota = "Rocky",
                raza = "Criollo",
                nombrePropietario = "Carlos Ruiz",
                telefono = "3109876543",
                sintoma = "Vacunación anual y desparasitación"
            ),
            CitaEntity(
                nombreMascota = "Michi",
                raza = "Siamés",
                nombrePropietario = "Laura Díaz",
                telefono = "3205551234",
                sintoma = "Control postoperatorio"
            )
        )
        citasDeEjemplo.forEach { database.citaDao().insertar(it) }
    }
}

