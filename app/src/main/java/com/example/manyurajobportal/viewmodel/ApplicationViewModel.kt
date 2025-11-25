package com.example.manyurajobportal.viewmodel.alumni

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.manyurajobportal.data.repository.ApplicationRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ApplicationDisplay(
    val jobTitle: String,
    val timestamp: Long,
    val status: String
)

class ApplicationViewModel(
    private val repository: ApplicationRepository = ApplicationRepository()
) : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _applications = MutableStateFlow<List<ApplicationDisplay>>(emptyList())
    val applications: StateFlow<List<ApplicationDisplay>> = _applications

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    // 🔹 NEW: success state for ApplyScreen redirect
    private val _applicationSuccess = MutableStateFlow(false)
    val applicationSuccess: StateFlow<Boolean> = _applicationSuccess

    // Load all applications belonging to user
    fun loadUserApplications(userId: String) {
        _loading.value = true

        firestore.collection("applications")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { snap ->
                _applications.value = snap.documents.map {
                    ApplicationDisplay(
                        jobTitle = it.getString("jobTitle") ?: "",
                        timestamp = it.getLong("timestamp") ?: 0L,
                        status = it.getString("status") ?: "Pending"
                    )
                }
                _loading.value = false
            }
    }

    // 🔹 NEW: Submit job application
    fun applyForJob(
        jobId: String,
        userId: String,
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
                    fullName = fullName,
                    email = email,
                    phone = phone,
                    location = location
                )

                _applicationSuccess.value = true
            } catch (e: Exception) {
                _applicationSuccess.value = false
            } finally {
                _loading.value = false
            }
        }
    }
}
