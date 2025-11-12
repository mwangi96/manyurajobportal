package com.example.manyurajobportal.data.remote


import retrofit2.http.GET
import retrofit2.http.Query

data class CountryResponseItem(val name: Name)
data class Name(val common: String)

data class CityResponse(val data: List<String>)

interface ApiService {
    @GET("v3.1/all")
    suspend fun getCountries(): List<CountryResponseItem>

    @GET("cities")
    suspend fun getCities(@Query("country") country: String): CityResponse
}
