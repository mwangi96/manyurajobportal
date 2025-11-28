package com.example.manyurajobportal.utils

import com.example.manyurajobportal.data.model.EmailRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface EmailApiService {

    @Headers(
        "Content-Type: application/json",
        "api-key: YOUR_BREVO_API_KEY"   // Replace this with your real API key
    )
    @POST("v3/smtp/email")
    suspend fun sendEmail(
        @Body emailData: EmailRequest
    ): Response<Unit>
}
