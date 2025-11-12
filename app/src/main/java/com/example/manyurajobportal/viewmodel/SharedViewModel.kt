package com.example.manyurajobportal.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

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
