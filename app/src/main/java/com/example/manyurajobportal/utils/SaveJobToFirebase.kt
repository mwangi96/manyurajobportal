package com.example.manyurajobportal.utils

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

// Job model kept INSIDE this file (no separate data model file)
data class JobData(
    val jobId: String = "",
    val adminId: String = "",
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
    val skills: List<String> = emptyList(),
    val applicantCount: Int = 0,
    val timestamp: Long = 0L
)

class SaveJobToFirebase {

    fun saveJobToFirebase(
        jobTitle: String,
        companyName: String,
        workplaceType: String,
        employmentType: String,
        currency: String,
        salaryType: String,
        minSalary: String,
        maxSalary: String,
        country: String,
        city: String,
        skills: List<String>,
        jobDescription: String,
        adminId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()

        // Auto-generate jobId
        val jobId = UUID.randomUUID().toString()

        // Create JobData object in THIS file
        val jobData = JobData(
            jobId = jobId,
            adminId = adminId,
            jobTitle = jobTitle,
            companyName = companyName,
            workplace = workplaceType,
            employmentType = employmentType,
            currency = currency,
            salaryType = salaryType,
            minSalary = minSalary,
            maxSalary = maxSalary,
            country = country,
            city = city,
            jobDescription = jobDescription,
            skills = skills,
            timestamp = System.currentTimeMillis()
        )

        // Save to Firestore
        db.collection("jobs")
            .document(jobId)
            .set(jobData)
            .addOnSuccessListener {
                Log.d(TAG, "Job posted successfully: $jobId")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to post job", e)
                onFailure(e)
            }
    }

    companion object {
        private const val TAG = "SaveJobToFirebase"
    }
}
