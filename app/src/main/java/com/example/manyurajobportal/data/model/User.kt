package com.example.manyurajobportal.data.model

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "" // "admin" or "alumni"
)
