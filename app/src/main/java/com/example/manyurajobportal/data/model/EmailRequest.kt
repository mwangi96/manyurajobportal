package com.example.manyurajobportal.data.model

data class EmailRequest(
    val sender: Sender = Sender("Manyura Job Portal", "gideonmwangi718@gmail.com"),
    val to: List<Recipient>,
    val subject: String,
    val htmlContent: String
)

data class Sender(
    val name: String,
    val email: String
)

data class Recipient(
    val email: String
)
