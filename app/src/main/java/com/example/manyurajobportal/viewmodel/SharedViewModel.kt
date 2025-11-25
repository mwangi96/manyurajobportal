package com.example.manyurajobportal.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow

class SharedViewModel : ViewModel() {

    val userId = MutableStateFlow<String?>(FirebaseAuth.getInstance().currentUser?.uid)


    // 🔹 Persist user info across all screens
    var userName = mutableStateOf("")
    var userEmail = mutableStateOf("")
    var userRole = mutableStateOf("")

    fun setUserInfo(name: String, email: String, role: String) {
        userName.value = name
        userEmail.value = email
        userRole.value = role
    }

    fun clearUserInfo() {
        userName.value = ""
        userEmail.value = ""
        userRole.value = ""
    }
}
