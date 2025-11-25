package com.example.manyurajobportal.data.model

data class Job(
    val jobId: String = "",
    val adminId: String = "",          // NEW: Used to filter admin-specific posted jobs
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
    val skills: List<String> = emptyList(),   // NEW: for required job skills
    val timestamp: Long = System.currentTimeMillis(),
    val applicantCount: Int = 0
)
