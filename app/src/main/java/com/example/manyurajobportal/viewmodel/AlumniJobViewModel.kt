// AlumniJobViewModel.kt
package com.example.manyurajobportal.viewmodel.alumni

import androidx.lifecycle.ViewModel
import com.example.manyurajobportal.data.model.Job
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

class AlumniJobViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _jobs = MutableStateFlow<List<Job>>(emptyList())
    val jobs: StateFlow<List<Job>> = _jobs

    private val _search = MutableStateFlow("")
    val search: StateFlow<String> = _search

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _job = MutableStateFlow<Job?>(null)
    val job: StateFlow<Job?> = _job

    private val _hasApplied = MutableStateFlow<Boolean?>(null)
    val hasApplied: StateFlow<Boolean?> = _hasApplied

    fun loadJobs() {
        _loading.value = true
        firestore.collection("jobs").get()
            .addOnSuccessListener { snap ->
                _jobs.value = snap.documents.mapNotNull { it.toObject(Job::class.java) }
                _loading.value = false
            }
            .addOnFailureListener {
                _jobs.value = emptyList()
                _loading.value = false
            }
    }

    fun onSearchChange(query: String) {
        _search.value = query
        val filtered = _jobs.value.filter { it.jobTitle.contains(query, true) }
        _jobs.value = filtered
    }

    fun loadJob(jobId: String) {
        _loading.value = true
        firestore.collection("jobs").document(jobId).get()
            .addOnSuccessListener { doc ->
                _job.value = doc.toObject(Job::class.java)
                _loading.value = false
            }
    }

    fun checkIfApplied(jobId: String) {
        val uid = FirebaseAuth.getInstance().uid ?: return

        _loading.value = true
        firestore.collection("applications")
            .document(jobId)
            .collection("applicants")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                _hasApplied.value = doc.exists()
                _loading.value = false
            }
    }

    suspend fun hasUserApplied(jobId: String): Boolean {
        val uid = FirebaseAuth.getInstance().uid ?: return false

        return try {
            val doc = firestore.collection("applications")
                .document(jobId)
                .collection("applicants")
                .document(uid)
                .get()
                .await()

            doc.exists()
        } catch (e: Exception) {
            false
        }
    }

}