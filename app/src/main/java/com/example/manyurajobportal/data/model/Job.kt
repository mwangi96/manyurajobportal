package com.example.manyurajobportal.data.model

data class Job(
    val  jobId: String = "",
    val jobTitle: String = "",
    val companyName: String = "",
    val workplace: String = "",
    val employmentType: String = "",
    val currency: String = "",
    val salaryType: String = "",
    val minSalary: String = "",
    val maxSalary: String = "",
    val country: String = "",
    val city: String = "",
    val jobDescription: String = "",
    val imageUrl: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
