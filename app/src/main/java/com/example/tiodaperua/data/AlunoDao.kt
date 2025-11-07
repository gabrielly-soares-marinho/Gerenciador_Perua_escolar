package com.example.tiodaperua.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AlunoDao {
    @Insert
    suspend fun insert(aluno: Aluno)

    @Update
    suspend fun update(aluno: Aluno)

    @Delete
    suspend fun delete(aluno: Aluno)

    @Query("SELECT * FROM alunos ORDER BY nome ASC")
    fun getAllAlunos(): Flow<List<Aluno>>
}