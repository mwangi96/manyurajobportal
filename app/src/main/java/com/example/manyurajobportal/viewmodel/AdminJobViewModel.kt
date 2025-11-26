package com.example.manyurajobportal.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.manyurajobportal.data.model.Job
import com.example.manyurajobportal.data.repository.JobRepository
import com.example.manyurajobportal.data.repository.ApplicationRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

class AdminJobsViewModel(
    private val repository: JobRepository = JobRepository(FirebaseFirestore.getInstance()),
    private val applicationRepository: ApplicationRepository = ApplicationRepository()
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

    // --------------------------------------------------------------------
    // 🔍 LOAD ALL JOBS INCLUDING APPLICANT COUNTS
    // --------------------------------------------------------------------
    fun loadJobs() {
        viewModelScope.launch {
            try {
                _loading.value = true

                // 1️⃣ Fetch all admin jobs
                val jobList = repository.getAdminJobs(_search.value)

                // 2️⃣ Fetch applicant count for each job in parallel (faster)
                val updatedJobs = jobList.map { job ->
                    async {
                        val count = applicationRepository.getApplicantCount(job.jobId)
                        job.copy(applicantCount = count)
                    }
                }.awaitAll()

                // 3️⃣ Update UI list
                _jobs.value = updatedJobs

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

    // --------------------------------------------------------------------
    // 🔵 GET A SINGLE JOB BY ID
    // --------------------------------------------------------------------
    fun getJobById(jobId: String): Flow<Job?> {
        return repository.getJobById(jobId)
    }

    // --------------------------------------------------------------------
    // ❌ DELETE JOB
    // --------------------------------------------------------------------
    fun deleteJob(jobId: String) {
        viewModelScope.launch {
            repository.deleteJob(jobId)
        }
    }

    // --------------------------------------------------------------------
    // 🟩 UPDATE JOB
    // --------------------------------------------------------------------
    fun updateJob(job: Job) {
        viewModelScope.launch {
            repository.updateJob(job)
        }
    }
}
