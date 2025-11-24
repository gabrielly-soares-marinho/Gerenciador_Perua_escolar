package com.example.tiodaperua.network

import com.example.tiodaperua.model.CepResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface CepApiService {
    @GET("{cep}/json/")
    fun getAddress(@Path("cep") cep: String): Call<CepResponse>
}