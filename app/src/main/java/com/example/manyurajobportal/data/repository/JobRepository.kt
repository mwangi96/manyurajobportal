package com.example.manyurajobportal.data.repository

import com.example.manyurajobportal.data.model.Job
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.tasks.await

class JobRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val jobsCollection = firestore.collection("jobs")
    private val applicationsCollection = firestore.collection("applications")

    // Always fetch latest UID, not once at class init
    private val auth get() = FirebaseAuth.getInstance()
    private val uid get() = auth.currentUser?.uid


    /*---------------------------------------------------------
     🔵 POST JOB (Admin)
     ---------------------------------------------------------*/
    suspend fun postJob(job: Job): Pair<Boolean, String> {
        val jobId = jobsCollection.document().id
        val adminId = uid ?: return false to "Admin not authenticated!"

        return try {
            val jobData = job.copy(
                jobId = jobId,
                adminId = adminId,
                timestamp = System.currentTimeMillis()
            )

            jobsCollection.document(jobId).set(jobData).await()
            true to "Job posted successfully!"

        } catch (e: Exception) {
            false to "Failed: ${e.message}"
        }
    }


    /*---------------------------------------------------------
     🔵 FETCH JOBS POSTED BY CURRENT ADMIN
     ---------------------------------------------------------*/
    suspend fun getAdminJobs(search: String = ""): List<Job> {
        val adminId = uid ?: return emptyList()

        return try {
            val snapshot = jobsCollection
                .whereEqualTo("adminId", adminId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            val jobs = snapshot.toObjects(Job::class.java)

            if (search.isBlank()) return jobs

            val query = search.lowercase()
            jobs.filter { job ->
                job.jobTitle.lowercase().contains(query) ||
                        job.companyName.lowercase().contains(query) ||
                        job.city.lowercase().contains(query) ||
                        job.country.lowercase().contains(query) ||
                        job.skills.any { it.lowercase().contains(query) }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }


    /*---------------------------------------------------------
     🔵 GET SINGLE JOB
     ---------------------------------------------------------*/
    suspend fun getJob(jobId: String): Job? {
        return try {
            val snap = jobsCollection.document(jobId).get().await()
            snap.toObject(Job::class.java)
        } catch (_: Exception) {
            null
        }
    }

    /*---------------------------------------------------------
 🔵 GET SINGLE JOB AS FLOW (Real-time updates)
 ---------------------------------------------------------*/
    fun getJobById(jobId: String): kotlinx.coroutines.flow.Flow<Job?> = kotlinx.coroutines.flow.callbackFlow {
        val listener = jobsCollection.document(jobId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObject(Job::class.java))
            }

        awaitClose { listener.remove() }
    }



    /*---------------------------------------------------------
     🔵 DELETE JOB
     ---------------------------------------------------------*/
    suspend fun deleteJob(jobId: String) {
        jobsCollection.document(jobId).delete().await()
    }


    /*---------------------------------------------------------
     🔵 UPDATE JOB
     ---------------------------------------------------------*/
    suspend fun updateJob(job: Job) {
        jobsCollection.document(job.jobId).set(job).await()
    }


    /*---------------------------------------------------------
     🔵 GET APPLICANT COUNT FOR A JOB
     ---------------------------------------------------------*/
    suspend fun getApplicantCount(jobId: String): Int {
        return try {
            applicationsCollection
                .whereEqualTo("jobId", jobId)
                .get()
                .await()
                .size()
        } catch (_: Exception) {
            0
        }
    }

}
