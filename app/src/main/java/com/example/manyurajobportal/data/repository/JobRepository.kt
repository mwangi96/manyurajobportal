package com.example.manyurajobportal.data.repository

import com.example.manyurajobportal.data.model.Job
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class JobRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val jobsCollection = firestore.collection("jobs")
    private val applicationsCollection = firestore.collection("applications")
    private val uid = FirebaseAuth.getInstance().currentUser?.uid


    // ---------------------------------------------------------
    // 🔵 GET ALL JOBS (For AlumniJobViewModel & PostedJobScreen)
    // ---------------------------------------------------------
    suspend fun getAllJobs(): List<Job> {
        return try {
            val snapshot = jobsCollection
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            val list = snapshot.toObjects(Job::class.java)

            // 🔹 Add applicant count to each job
            list.map { job ->
                val count = getApplicantCount(job.jobId)
                job.copy(applicantCount = count)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun getJobById(jobId: String): Flow<Job?> = callbackFlow {
        val listener = jobsCollection.document(jobId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    val job = snapshot.toObject(Job::class.java)

                    trySend(job)  // send real-time job updates
                } else {
                    trySend(null)
                }
            }

        awaitClose { listener.remove() }
    }



    // ---------------------------------------------------------
    // 🔵 Get applicant count for a job
    // ---------------------------------------------------------
    private suspend fun getApplicantCount(jobId: String): Int {
        return try {
            val snapshot = applicationsCollection
                .whereEqualTo("jobId", jobId)
                .get()
                .await()
            snapshot.size()
        } catch (e: Exception) {
            0
        }
    }


    // ---------------------------------------------------------
    // 🔵 GET SINGLE JOB BY ID
    // ---------------------------------------------------------
    suspend fun getJob(jobId: String): Job? {
        return try {
            val snapshot = jobsCollection.document(jobId)
                .get()
                .await()

            val job = snapshot.toObject(Job::class.java)

            // include applicant count
            job?.copy(applicantCount = getApplicantCount(jobId))

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    // ---------------------------------------------------------
    // 🔵 CHECK IF USER APPLIED FOR JOB
    // ---------------------------------------------------------
    suspend fun hasApplied(jobId: String): Boolean {
        if (uid == null) return false

        return try {
            val snapshot = applicationsCollection
                .whereEqualTo("jobId", jobId)
                .whereEqualTo("userId", uid)
                .get()
                .await()

            !snapshot.isEmpty

        } catch (e: Exception) {
            false
        }
    }


    // ---------------------------------------------------------
    // 🔵 POST JOB
    // ---------------------------------------------------------
    suspend fun postjob(job: Job): Pair<Boolean, String> {
        val jobId = firestore.collection("jobs").document().id
        val adminId = uid ?: ""

        return try {
            val jobData = job.copy(
                jobId = jobId,
                adminId = adminId,
                timestamp = System.currentTimeMillis()
            )

            jobsCollection.document(jobId)
                .set(jobData)
                .await()

            true to "Job posted successfully!"
        } catch (e: Exception) {
            e.printStackTrace()
            false to "Failed: ${e.message}"
        }
    }


    // ---------------------------------------------------------
    // 🔵 GET ADMIN JOBS
    // ---------------------------------------------------------
    suspend fun getAdminJobs(searchQuery: String = ""): List<Job> {
        val adminId = uid ?: return emptyList()

        return try {
            val snapshot = jobsCollection
                .whereEqualTo("adminId", adminId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            val jobs = snapshot.toObjects(Job::class.java)

            if (searchQuery.isBlank()) return jobs

            val q = searchQuery.lowercase()

            jobs.filter { job ->
                job.jobTitle.lowercase().contains(q) ||
                        job.companyName.lowercase().contains(q) ||
                        job.city.lowercase().contains(q) ||
                        job.country.lowercase().contains(q) ||
                        job.skills.any { it.lowercase().contains(q) }
            }

        } catch (e: Exception) {
            emptyList()
        }
    }


    // ---------------------------------------------------------
    // 🔵 UPDATE JOB
    // ---------------------------------------------------------
    suspend fun updateJob(job: Job) {
        jobsCollection.document(job.jobId)
            .set(job)
            .await()
    }


    // ---------------------------------------------------------
    // 🔵 DELETE JOB
    // ---------------------------------------------------------
    suspend fun deleteJob(jobId: String) {
        jobsCollection.document(jobId)
            .delete()
            .await()
    }


    // ---------------------------------------------------------
    // 🔵 GET APPLICANTS FOR A JOB
    // ---------------------------------------------------------
    suspend fun getApplicants(jobId: String): List<Map<String, Any?>> {
        return try {
            val apps = applicationsCollection
                .whereEqualTo("jobId", jobId)
                .get()
                .await()

            apps.mapNotNull { application ->
                val userId = application.getString("userId") ?: return@mapNotNull null

                val userSnapshot = firestore.collection("users")
                    .document(userId)
                    .get()
                    .await()

                val userData = userSnapshot.data ?: return@mapNotNull null

                mapOf(
                    "fullName" to (userData["fullName"] ?: ""),
                    "email" to (application.getString("email") ?: userData["email"]),
                    "phone" to application.getString("phone"),
                    "location" to application.getString("location"),
                    "timestamp" to application.getLong("timestamp"),
                    "userEmail" to userData["email"],
                    "userId" to userId
                )
            }

        } catch (e: Exception) {
            emptyList()
        }
    }
}
