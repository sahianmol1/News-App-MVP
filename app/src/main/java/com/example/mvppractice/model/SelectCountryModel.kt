package com.example.mvppractice.model

import com.example.mvppractice.contracts.SelectCountryContract
import com.example.mvppractice.service.models.Country
import com.google.gson.Gson
import java.io.InputStream
import java.io.InputStreamReader

class SelectCountryModel (
    private val countriesJsonStream: InputStream
) : SelectCountryContract.Model {
    override suspend fun fetchCountriesList(
        onFinishListener: SelectCountryContract.Model.OnFinishListener
    ) {
        onFinishListener.onLoading()

        try {
            val reader = InputStreamReader(countriesJsonStream)
            val gson = Gson()
            onFinishListener.onSuccess(gson.fromJson(reader, Array<Country>::class.java).toList())
        } catch (e: Exception) {
            onFinishListener.onError(e.message.toString())
        }

    }
}