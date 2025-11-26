package com.example.manyurajobportal.data.repository

import com.example.manyurajobportal.data.model.ApplicationDisplay
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ApplicationRepository {

    private val firestore = FirebaseFirestore.getInstance()

    // SUBMIT APPLICATION
    suspend fun submitApplication(
        jobId: String,
        userId: String,
        jobTitle: String,
        fullName: String,
        email: String,
        phone: String,
        location: String
    ) {
        val data = hashMapOf(
            "jobId" to jobId, // 🔥 REQUIRED
            "userId" to userId,
            "jobTitle" to jobTitle,
            "fullName" to fullName,
            "email" to email,
            "phone" to phone,
            "location" to location,
            "status" to "Pending",
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("applications")
            .document(jobId)
            .collection("applicants")
            .document(userId)
            .set(data)
            .await()
    }


    // LOAD APPLICATIONS
    suspend fun getUserApplications(userId: String): List<ApplicationDisplay> {
        val snap = firestore.collectionGroup("applicants")
            .whereEqualTo("userId", userId)
            .get()
            .await()

        return snap.documents.map { doc ->
            ApplicationDisplay(
                jobId = doc.getString("jobId") ?: "",  // 🔥 ADD THIS
                jobTitle = doc.getString("jobTitle") ?: "",
                timestamp = doc.getLong("timestamp") ?: 0L,
                status = doc.getString("status") ?: "Pending"
            )

        }
    }

    // 🔵 GET NUMBER OF APPLICANTS FOR A JOB
    suspend fun getApplicantCount(jobId: String): Int {
        val snap = firestore.collection("applications")
            .document(jobId)
            .collection("applicants")
            .get()
            .await()

        return snap.size()
    }



}
