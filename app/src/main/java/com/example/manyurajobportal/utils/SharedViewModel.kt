package com.example.manyurajobportal.utils

import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.example.manyurajobportal.ui.screens.admin.ApplicantData
import com.example.manyurajobportal.screens.ApplicationData



class SharedViewModel(
    private val firebaseRepo: FirebaseAuthRepository
) : ViewModel() {

    /* ---------------------------------------------------------
     * GLOBAL UI STATE
     * --------------------------------------------------------- */
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage


    /* ---------------------------------------------------------
     * USER SESSION
     * --------------------------------------------------------- */
    private val _currentUserEmail = MutableStateFlow<String?>(null)
    val currentUserEmail: String?
        get() = _currentUserEmail.value

    private val _currentUserRole = MutableStateFlow("")
    val currentUserRole: StateFlow<String> = _currentUserRole

    private val _currentUserName = MutableStateFlow<String?>(null)
    val currentUserName: String?
        get() = _currentUserName.value

    fun setUserInfo(name: String, email: String, role: String) {
        _currentUserName.value = name
        _currentUserEmail.value = email
        _currentUserRole.value = role
    }

    fun clearUserSession() {
        _currentUserEmail.value = null
        _currentUserName.value = null
        _currentUserRole.value = ""

        // ❗ Must clear Firestore + applications to avoid recomposition errors
        _firestoreData.value = null
        _applications.value = emptyList()
        appliedJobs.clear()
    }


    /* ---------------------------------------------------------
     * FIRESTORE USER DATA
     * --------------------------------------------------------- */
    private val _firestoreData = MutableStateFlow<Map<String, Any>?>(null)
    val firestoreData: StateFlow<Map<String, Any>?> = _firestoreData

    fun getUserFirestoreData(uid: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val doc = firestore.collection("users").document(uid).get().await()
                if (doc.exists()) {
                    _firestoreData.value = doc.data
                } else {
                    _errorMessage.value = "User data not found"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load user data"
            }
            _loading.value = false
        }
    }


    /* ---------------------------------------------------------
     * AUTH: Current user UID
     * --------------------------------------------------------- */
    fun currentUser(): String? = firebaseRepo.currentUser()?.uid

    fun logout() {
        firebaseRepo.logout()     // Firebase sign out
        clearUserSession()        // Clear everything cached
    }


    /* ---------------------------------------------------------
     * FIREBASE INSTANCES
     * --------------------------------------------------------- */
    private val firestore = FirebaseFirestore.getInstance()
    private val realtimeDb = FirebaseDatabase.getInstance().reference


    /* ---------------------------------------------------------
     * AUTH
     * --------------------------------------------------------- */
    fun signUp(name: String, email: String, password: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            val success = firebaseRepo.signup(name, email, password)
            if (!success) _errorMessage.value = "Signup failed"
            _loading.value = false
            callback(success)
        }
    }

    fun login(email: String, password: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            val success = firebaseRepo.login(email, password)
            if (!success) _errorMessage.value = "Invalid credentials"
            _loading.value = false
            callback(success)
        }
    }

    fun resetPassword(email: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            val success = firebaseRepo.resetPassword(email)
            if (!success) _errorMessage.value = "Reset failed"
            _loading.value = false
            callback(success)
        }
    }


    /* ---------------------------------------------------------
     * APPLICATION CACHE SYSTEM
     * --------------------------------------------------------- */
    val appliedJobs = mutableStateMapOf<String, Boolean>()

    fun checkIfUserApplied(jobId: String, email: String) {
        if (appliedJobs.containsKey(jobId)) return

        viewModelScope.launch {
            appliedJobs[jobId] = hasUserApplied(jobId, email)
        }
    }

    private suspend fun hasUserApplied(jobId: String, email: String): Boolean {
        return try {
            val docs = firestore.collection("applications")
                .whereEqualTo("jobId", jobId)
                .whereEqualTo("userEmail", email)
                .get()
                .await()

            !docs.isEmpty
        } catch (e: Exception) {
            Log.e("SharedViewModel", "hasUserApplied error:", e)
            false
        }
    }

    fun hasApplied(jobId: String): Boolean = appliedJobs[jobId] == true


    /* ---------------------------------------------------------
     * APPLY FOR JOB
     * --------------------------------------------------------- */
    fun applyForJob(
        jobId: String,
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String,
        location: String
    ) {
        viewModelScope.launch {
            _loading.value = true

            if (hasUserApplied(jobId, email)) {
                _errorMessage.value = "You have already applied for this job."
                _loading.value = false
                return@launch
            }

            val application = mapOf(
                "jobId" to jobId,
                "firstName" to firstName,
                "lastName" to lastName,
                "userEmail" to email,
                "phoneNumber" to phoneNumber,
                "location" to location,
                "status" to "Application Sent"
            )

            try {
                firestore.collection("applications").add(application).await()
                appliedJobs[jobId] = true
            } catch (e: Exception) {
                Log.e("SharedViewModel", "applyForJob error:", e)
                _errorMessage.value = "Failed to submit application"
            }

            _loading.value = false
        }
    }

    val applicantsForJob = mutableStateOf<List<ApplicantData>>(emptyList())


    fun fetchApplicantsForJob(jobId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val docs = firestore.collection("applications")
                    .whereEqualTo("jobId", jobId)
                    .get()
                    .await()

                applicantsForJob.value = docs.map { doc ->
                    ApplicantData(
                        id = doc.id,
                        firstName = doc.getString("firstName") ?: "",
                        lastName = doc.getString("lastName") ?: "",
                        userEmail = doc.getString("userEmail") ?: "",
                        phoneNumber = doc.getString("phoneNumber") ?: "",
                        location = doc.getString("location") ?: "",
                        status = doc.getString("status") ?: "Application Sent"
                    )
                }

            } catch (e: Exception) {
                _errorMessage.value = "Failed to load applicants"
            }
            _loading.value = false
        }
    }


    fun updateApplicationStatus(applicationId: String, newStatus: String) {
        viewModelScope.launch {
            try {
                firestore.collection("applications")
                    .document(applicationId)
                    .update("status", newStatus)
                    .await()

                applicantsForJob.value = applicantsForJob.value.map {
                    if (it.id == applicationId) it.copy(status = newStatus) else it
                }

            } catch (e: Exception) {
                _errorMessage.value = "Failed to update status"
            }
        }
    }



    /* ---------------------------------------------------------
     * MY APPLICATIONS
     * --------------------------------------------------------- */
    private val _applications = MutableStateFlow<List<ApplicationData>>(emptyList())
    val applications: StateFlow<List<ApplicationData>> = _applications

    fun fetchApplications(email: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val docs = firestore.collection("applications")
                    .whereEqualTo("userEmail", email)
                    .get()
                    .await()

                val list = docs.map { doc ->

                    val jobId = doc.getString("jobId") ?: ""
                    val status = doc.getString("status") ?: "Application Sent"

                    // 🔥 Fetch job details from Firestore (NOT Realtime DB)
                    val jobDoc = firestore.collection("jobs")
                        .document(jobId)
                        .get()
                        .await()

                    ApplicationData(
                        jobId = jobId,
                        jobTitle = jobDoc.getString("jobTitle") ?: "",
                        companyName = jobDoc.getString("companyName") ?: "",
                        status = status
                    )
                }

                _applications.value = list

            } catch (e: Exception) {
                Log.e("SharedViewModel", "fetchApplications error:", e)
                _errorMessage.value = "Failed to load applications"
            }

            _loading.value = false
        }
    }
}

