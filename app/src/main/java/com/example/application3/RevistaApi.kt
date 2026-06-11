package com.example.application3

import retrofit2.Call
import retrofit2.http.GET

interface RevistaApi {
    @GET("https://apiws.uteq.edu.ec/h6RPoSoRaah0Y4Bah28eew/functions/information/entity/5")
    fun getRevistas(): Call<List<Revista>>
}
