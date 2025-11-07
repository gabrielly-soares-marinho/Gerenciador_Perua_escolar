package com.example.tiodaperua.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alunos")
data class Aluno(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String,
    val escola: String,
    val turma: String,
    val endereco: String
)