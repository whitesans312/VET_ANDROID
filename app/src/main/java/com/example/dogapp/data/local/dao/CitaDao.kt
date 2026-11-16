package com.example.dogapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.dogapp.data.local.entity.CitaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CitaDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(cita: CitaEntity): Long

    @Query("SELECT * FROM citas ORDER BY id ASC")
    fun observarTodas(): Flow<List<CitaEntity>>

    @Query("SELECT * FROM citas WHERE id = :id LIMIT 1")
    fun observarPorId(id: Long): Flow<CitaEntity?>

    @Query("SELECT * FROM citas WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Long): CitaEntity?

    /** Devuelve el número de filas modificadas; cero significa que no se encontró la cita. */
    @Update
    suspend fun actualizar(cita: CitaEntity): Int

    /** Devuelve el número de filas eliminadas; cero significa que no se encontró la cita. */
    @Query("DELETE FROM citas WHERE id = :id")
    suspend fun eliminarPorId(id: Long): Int

    @Query("SELECT COUNT(*) FROM citas")
    suspend fun contar(): Int
}
