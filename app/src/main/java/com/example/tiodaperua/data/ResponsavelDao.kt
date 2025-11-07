package com.example.tiodaperua.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ResponsavelDao {
    @Insert
    suspend fun insert(responsavel: Responsavel)

    @Update
    suspend fun update(responsavel: Responsavel)

    @Delete
    suspend fun delete(responsavel: Responsavel)

    @Query("SELECT * FROM responsaveis ORDER BY nome ASC")
    fun getAllResponsaveis(): Flow<List<Responsavel>>
}