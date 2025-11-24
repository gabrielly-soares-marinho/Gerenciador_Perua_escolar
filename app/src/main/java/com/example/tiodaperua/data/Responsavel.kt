package com.example.tiodaperua.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "responsaveis")
data class Responsavel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String,
    val telefone: String,
    val email: String,
    val cep: String?,
    val logradouro: String?,
    val numero: String?,
    val complemento: String?,
    val bairro: String?,
    val cidade: String?,
    val estado: String?
)
