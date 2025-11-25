package com.example.manyurajobportal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.manyurajobportal.data.model.Profile
import com.example.manyurajobportal.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel(
    private val repo: ProfileRepository = ProfileRepository()
) : ViewModel() {

    private val _fullName = MutableStateFlow("")
    val fullName: StateFlow<String> = _fullName

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _profile = MutableStateFlow<Profile?>(null)
    val profile: StateFlow<Profile?> = _profile


    // ---------------------------------------------------------
    // 1. Load user info for CreateProfileScreen
    // ---------------------------------------------------------
    fun loadUserInfo() {
        viewModelScope.launch {
            val data = repo.getUserInfo()

            _fullName.value = data["fullName"] ?: ""
            _email.value = data["email"] ?: ""
        }
    }


    // ---------------------------------------------------------
    // 2. Load full profile for ApplyScreen or anywhere
    // ---------------------------------------------------------
    fun fetchUserProfile(userId: String) {
        viewModelScope.launch {
            val db = repo.db
            val doc = db.collection("profiles").document(userId).get().await()

            if (doc.exists()) {
                _profile.value = Profile(
                    fullName = doc.getString("fullName") ?: "",
                    email = doc.getString("email") ?: "",
                    phone = doc.getString("phone") ?: "",
                    location = doc.getString("location") ?: ""
                )
            }
        }
    }


    // ---------------------------------------------------------
    // 3. Create Profile using repository
    // ---------------------------------------------------------
    fun createProfile(phone: String, location: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            _loading.value = true

            val success = repo.createProfile(phone, location)

            _loading.value = false
            onComplete(success)
        }
    }

    fun updateProfile(phone: String, location: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            val success = repo.updateProfile(phone, location)
            _loading.value = false
            onComplete(success)
        }
    }


    // ---------------------------------------------------------
    // 4. Check if user already has a profile
    // ---------------------------------------------------------
    fun checkProfile(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            onResult(repo.hasProfile())
        }
    }
}
