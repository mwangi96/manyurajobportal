package com.example.manyurajobportal.utils

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.manyurajobportal.screens.ApplicationData
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SharedViewModel(
    private val firebaseRepo: FirebaseAuthRepository
) : ViewModel() {

    /* ---------------------------------------------------------
     * LOADING + ERROR STATES
     * --------------------------------------------------------- */
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage


    /* ---------------------------------------------------------
     * USER INFO STATE
     * --------------------------------------------------------- */
    val userEmail = MutableStateFlow<String?>(null)
    val userRole = MutableStateFlow<String?>(null)
    val userName = MutableStateFlow<String?>(null)

    fun setUserInfo(name: String, email: String, role: String) {
        userName.value = name
        userEmail.value = email
        userRole.value = role
    }


    /* ---------------------------------------------------------
     * FIREBASE INSTANCES
     * --------------------------------------------------------- */
    private val firestore = FirebaseFirestore.getInstance()
    private val realtimeDb = FirebaseDatabase.getInstance().reference


    /* ---------------------------------------------------------
     * AUTH OPERATIONS
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
            if (!success) _errorMessage.value = "Reset link failed"
            _loading.value = false
            callback(success)
        }
    }

    fun clearUserSession() {
        userEmail.value = null
        userName.value = null
        userRole.value = null
    }

    fun currentUser(): String? = firebaseRepo.currentUser()


    /* ---------------------------------------------------------
     * USER FIRESTORE DOCUMENT
     * --------------------------------------------------------- */
    private val _firestoreData = MutableStateFlow<Map<String, Any>?>(null)
    val firestoreData: StateFlow<Map<String, Any>?> = _firestoreData

    fun getUserFirestoreData(userId: String) {
        viewModelScope.launch {
            _loading.value = true
            val data = firebaseRepo.getUserDocument(userId)
            if (data == null) _errorMessage.value = "Failed to fetch user data"
            _firestoreData.value = data
            _loading.value = false
        }
    }

    fun updateUserFirestoreData(userId: String, updates: Map<String, Any>, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            val success = firebaseRepo.updateUserDocument(userId, updates)
            if (!success) _errorMessage.value = "Profile update failed"
            _loading.value = false
            callback(success)
        }
    }


    /* ---------------------------------------------------------
     * JOB APPLICATION SYSTEM
     * --------------------------------------------------------- */
    private val _applications = MutableStateFlow<List<ApplicationData>>(emptyList())
    val applications: StateFlow<List<ApplicationData>> = _applications


    suspend fun hasUserApplied(jobId: String, email: String): Boolean {
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
                _errorMessage.value = null
            } catch (e: Exception) {
                Log.e("SharedViewModel", "applyForJob error:", e)
                _errorMessage.value = "Failed to submit application"
            }

            _loading.value = false
        }
    }


    /* ---------------------------------------------------------
     * FETCH USER APPLICATIONS + MATCH REALTIME DB JOBS
     * --------------------------------------------------------- */
    fun fetchApplications(email: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val apps = firestore.collection("applications")
                    .whereEqualTo("userEmail", email)
                    .get()
                    .await()

                val list = apps.map { doc ->
                    val jobId = doc.getString("jobId") ?: ""
                    val status = doc.getString("status") ?: "Application Sent"

                    val jobSnapshot = realtimeDb.child("jobs").child(jobId).get().await()

                    ApplicationData(
                        jobId = jobId,
                        jobTitle = jobSnapshot.child("jobTitle").getValue(String::class.java) ?: "",
                        companyName = jobSnapshot.child("companyName").getValue(String::class.java) ?: "",
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
