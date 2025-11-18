package com.example.manyurajobportal.data.repository

import com.example.manyurajobportal.data.model.Job
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

class JobRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    /**
     * 🔹 POST JOB
     * Adds a new job to Firestore with a unique jobId and timestamp.
     * Returns (success, message)
     */
    suspend fun postjob(job: Job): Pair<Boolean, String> {
        val jobId = UUID.randomUUID().toString()
        val adminId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        return try {
            val jobData = job.copy(
                jobId = jobId,
                adminId = adminId,   // 🔥 VERY IMPORTANT
                timestamp = System.currentTimeMillis()
            )

            firestore.collection("jobs")
                .document(jobId)
                .set(jobData)
                .await()

            Pair(true, "Job posted successfully!")
        } catch (e: Exception) {
            e.printStackTrace()
            Pair(false, "Failed to post job: ${e.message}")
        }
    }


    /**
     * 🔹 GET JOBS POSTED BY CURRENT ADMIN
     * Filters by: title, company, city, country, skills
     */
    suspend fun getAdminJobs(searchQuery: String = ""): List<Job> {
        val adminId = FirebaseAuth.getInstance().currentUser?.uid ?: return emptyList()

        return try {
            val snapshot = firestore.collection("jobs")
                .whereEqualTo("adminId", adminId)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            val jobs = snapshot.toObjects(Job::class.java)

            if (searchQuery.isBlank()) {
                return jobs
            }

            val q = searchQuery.lowercase()

            jobs.filter { job ->
                job.jobTitle.lowercase().contains(q) ||
                        job.companyName.lowercase().contains(q) ||
                        job.city.lowercase().contains(q) ||
                        job.country.lowercase().contains(q) ||
                        job.skills.any { it.lowercase().contains(q) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
