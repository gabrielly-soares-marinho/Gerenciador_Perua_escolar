package com.example.tiodaperua.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "escolas")
data class Escola(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String,
    val cep: String?,
    val logradouro: String?,
    val numero: String?,
    val complemento: String?,
    val bairro: String?,
    val cidade: String?,
    val estado: String?,
    val telefone: String
)
