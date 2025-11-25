package com.example.manyurajobportal.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.manyurajobportal.data.model.Job
import com.example.manyurajobportal.data.repository.JobRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AdminJobsViewModel(
    private val repository: JobRepository = JobRepository(FirebaseFirestore.getInstance())
) : ViewModel() {

    private val _jobs = MutableStateFlow<List<Job>>(emptyList())
    val jobs: StateFlow<List<Job>> = _jobs

    private val _search = MutableStateFlow("")
    val search: StateFlow<String> = _search

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    init {
        loadJobs()
    }

    // ---------------------------------------------------------
    // 🔍 LOAD ALL JOBS POSTED BY THIS ADMIN
    // ---------------------------------------------------------
    fun loadJobs() {
        viewModelScope.launch {
            try {
                _loading.value = true
                _jobs.value = repository.getAdminJobs(_search.value)
            } catch (e: Exception) {
                e.printStackTrace()
                _jobs.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }

    fun onSearchChange(value: String) {
        _search.value = value
        loadJobs()
    }

    // ---------------------------------------------------------
    // 🟦 GET A SINGLE JOB BY ID (USED IN AdminJobDetailsScreen)
    // ---------------------------------------------------------
    fun getJobById(jobId: String): Flow<Job?> {
        return repository.getJobById(jobId)
    }

    // ---------------------------------------------------------
    // ❌ DELETE A JOB (USED IN AdminJobDetailsScreen)
    // ---------------------------------------------------------
    fun deleteJob(jobId: String) {
        viewModelScope.launch {
            repository.deleteJob(jobId)
        }
    }



    // ---------------------------------------------------------
// 🟩 UPDATE JOB
// ---------------------------------------------------------
    fun updateJob(job: Job) {
        viewModelScope.launch {
            repository.updateJob(job)
        }
    }

}
