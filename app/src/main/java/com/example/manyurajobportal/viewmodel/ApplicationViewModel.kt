package com.example.manyurajobportal.viewmodel.alumni

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.manyurajobportal.data.model.ApplicationDisplay
import com.example.manyurajobportal.data.repository.ApplicationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ApplicationViewModel(
    private val repository: ApplicationRepository = ApplicationRepository()
) : ViewModel() {

    private val _applications = MutableStateFlow<List<ApplicationDisplay>>(emptyList())
    val applications: StateFlow<List<ApplicationDisplay>> = _applications

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _applicationSuccess = MutableStateFlow(false)
    val applicationSuccess: StateFlow<Boolean> = _applicationSuccess


    // 🔵 LOAD USER'S APPLICATIONS FOR APPLICATIONS SCREEN
    fun loadUserApplications(userId: String) {
        viewModelScope.launch {
            _loading.value = true

            try {
                _applications.value = repository.getUserApplications(userId)
            } catch (_: Exception) {}

            _loading.value = false
        }
    }


    // 🔵 APPLY FOR A JOB (USED IN ApplyScreen)
    fun applyForJob(
        jobId: String,
        userId: String,
        jobTitle: String,
        fullName: String,
        email: String,
        phone: String,
        location: String
    ) {
        viewModelScope.launch {
            _loading.value = true
            _applicationSuccess.value = false

            try {
                repository.submitApplication(
                    jobId = jobId,
                    userId = userId,
                    jobTitle = jobTitle,
                    fullName = fullName,
                    email = email,
                    phone = phone,
                    location = location
                )
                _applicationSuccess.value = true
            } catch (e: Exception) {
                _applicationSuccess.value = false
            }

            _loading.value = false
        }
    }
}
