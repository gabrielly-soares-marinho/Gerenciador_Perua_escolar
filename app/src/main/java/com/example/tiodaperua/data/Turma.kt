package com.example.tiodaperua.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "turmas")
data class Turma(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String,
    val escola: String,
    val periodo: String,
    val cep: String?,
    val logradouro: String?,
    val numero: String?,
    val complemento: String?,
    val bairro: String?,
    val cidade: String?,
    val estado: String?
)
