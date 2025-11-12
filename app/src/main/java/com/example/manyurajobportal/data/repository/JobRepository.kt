package com.example.manyurajobportal.data.repository

import android.net.Uri
import com.example.manyurajobportal.data.model.Job
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.*

class JobRepository(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {

    /**
     * Posts a job to Firestore with optional image upload to Firebase Storage.
     *
     * @param job The job data to post.
     * @param imageUri The image Uri from gallery (optional).
     * @return true if successful, false otherwise.
     */
    suspend fun postJob(job: Job, imageUri: Uri?): Boolean {
        return try {
            val jobId = UUID.randomUUID().toString()
            var imageUrl = ""

            // Upload image to Firebase Storage if provided
            if (imageUri != null) {
                val ref = storage.reference.child("jobs/$jobId.jpg")
                ref.putFile(imageUri).await()
                imageUrl = ref.downloadUrl.await().toString()
            }

            // Copy job data with ID and imageUrl
            val jobData = job.copy(jobId = jobId, imageUrl = imageUrl, timestamp = System.currentTimeMillis())

            // Save job to Firestore
            firestore.collection("jobs")
                .document(jobId)
                .set(jobData)
                .await()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
