package com.example.tiodaperua.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "turmas")
data class Turma(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String,
    val periodo: String,
    val escola: String
)
