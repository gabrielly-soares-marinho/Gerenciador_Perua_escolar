package com.example.tiodaperua.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TurmaDao {
    @Insert
    suspend fun insert(turma: Turma)

    @Update
    suspend fun update(turma: Turma)

    @Delete
    suspend fun delete(turma: Turma)

    @Query("SELECT * FROM turmas ORDER BY nome ASC")
    fun getAllTurmas(): Flow<List<Turma>>
}