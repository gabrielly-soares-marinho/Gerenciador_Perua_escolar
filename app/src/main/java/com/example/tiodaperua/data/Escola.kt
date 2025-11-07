package com.example.tiodaperua.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "escolas")
data class Escola(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String,
    val endereco: String,
    val telefone: String
)
