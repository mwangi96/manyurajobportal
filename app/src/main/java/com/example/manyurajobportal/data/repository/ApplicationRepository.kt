package com.example.manyurajobportal.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ApplicationRepository {

    private val applicationsRef = FirebaseFirestore.getInstance()
        .collection("applications")

    // Check if user has already applied
    suspend fun hasUserApplied(jobId: String, userId: String): Boolean {
        val doc = applicationsRef
            .document(jobId)
            .collection("applicants")
            .document(userId)
            .get()
            .await()
        return doc.exists()
    }

    // Submit a new application
    suspend fun submitApplication(
        jobId: String,
        userId: String,
        fullName: String,
        email: String,
        phone: String,
        location: String
    ) {
        val applicationData = mapOf(
            "userId" to userId,
            "fullName" to fullName,
            "email" to email,
            "phone" to phone,
            "location" to location,
            "status" to "Pending", // default status
            "timestamp" to System.currentTimeMillis()
        )

        applicationsRef
            .document(jobId)
            .collection("applicants")
            .document(userId)
            .set(applicationData)
            .await()
    }
}
