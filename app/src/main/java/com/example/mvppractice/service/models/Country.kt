package com.example.mvppractice.service.models

import com.google.gson.annotations.SerializedName

data class Country(
    @SerializedName("name")
    val countryName: String? = "",
    @SerializedName("country-code")
    val countryCode: String? = "",
    @SerializedName("alpha-2")
    val alpha2: String? = ""
)
