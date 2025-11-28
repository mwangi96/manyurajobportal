package com.example.manyurajobportal.utils

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    val api: EmailApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.brevo.com/")   // Brevo API URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EmailApiService::class.java)
    }
}
