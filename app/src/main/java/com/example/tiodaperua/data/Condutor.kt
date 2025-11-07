package com.example.tiodaperua.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "condutores")
data class Condutor(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String,
    val telefone: String,
    val placaVeiculo: String
)
