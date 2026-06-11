package com.example.application3

import com.google.gson.annotations.SerializedName

data class Revista(
    @SerializedName("anio") val anio: Int?,
    @SerializedName("mes") val mes: Int?,
    @SerializedName("urlportada") val urlportada: String?,
    @SerializedName("urlpw") val pdf: String?
)

data class RevistaResponse(
    val data: List<Revista>?
)