package com.example.manyurajobportal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.manyurajobportal.data.model.Job
import com.example.manyurajobportal.data.repository.JobRepository
import kotlinx.coroutines.launch

class JobViewModel(
    private val repository: JobRepository
) : ViewModel() {

    fun postjob(
        job: Job,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val (success, message) = repository.postjob(job)
            onResult(success, message)
        }
    }
}
