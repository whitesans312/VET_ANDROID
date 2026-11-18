package com.example.dogapp.data.local.database

import android.content.Context
import androidx.room.Room

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
            ).build().also { database ->
                instance = database
            }
        }
}
