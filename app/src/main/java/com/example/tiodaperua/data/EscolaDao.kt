package com.example.tiodaperua.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EscolaDao {
    @Insert
    suspend fun insert(escola: Escola)

    @Update
    suspend fun update(escola: Escola)

    @Delete
    suspend fun delete(escola: Escola)

    @Query("SELECT * FROM escolas ORDER BY nome ASC")
    fun getAllEscolas(): Flow<List<Escola>>
}