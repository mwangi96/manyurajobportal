package com.example.manyurajobportal.data.model

data class ApplicationDisplay(
    val jobId: String = "",
    val jobTitle: String = "",
    val timestamp: Long = 0L,
    val status: String = "Pending"
)
