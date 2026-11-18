package com.example.dogapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.dogapp.data.local.dao.CitaDao
import com.example.dogapp.data.local.entity.CitaEntity

@Database(
    entities = [CitaEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun citaDao(): CitaDao
}
