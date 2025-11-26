package com.example.manyurajobportal.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.manyurajobportal.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    val repository: AuthRepository,
    private val app: Application
) : AndroidViewModel(app) {

    // 🔹 Observable auth state (for UI updates)
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        data class Success(val role: String) : AuthState()
        data class Error(val message: String) : AuthState()
    }

    // =============================================================
    // ✅ SIGN UP — Automatically assigns "alumni" role
    // =============================================================
    fun signUpUser(
        name: String,
        email: String,
        password: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.signUpWithEmail(name, email, password)
            result.onSuccess {
                Toast.makeText(app, "Sign up successful!", Toast.LENGTH_SHORT).show()
                _authState.value = AuthState.Success("alumni") // default role
                onResult(true)
            }.onFailure {
                val message = it.message ?: "Sign-up failed"
                Toast.makeText(app, message, Toast.LENGTH_SHORT).show()
                _authState.value = AuthState.Error(message)
                onResult(false)
            }
        }
    }

    // =============================================================
    // ✅ LOGIN — Fetch role from Firestore for navigation
    // =============================================================
    fun loginUser(
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.loginWithEmail(email, password)
            result.onSuccess {
                val uid = repository.currentUser()?.uid
                val role = uid?.let { repository.getUserRole(it) } ?: "alumni"
                Toast.makeText(app, "Login successful!", Toast.LENGTH_SHORT).show()
                _authState.value = AuthState.Success(role)
                onResult(true, role)
            }.onFailure {
                val message = it.message ?: "Invalid email or password"
                Toast.makeText(app, message, Toast.LENGTH_SHORT).show()
                _authState.value = AuthState.Error(message)
                onResult(false, null)
            }
        }
    }

    // =============================================================
    // 🔹 Password Reset
    // =============================================================
    fun resetPassword(email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.resetPassword(email)
            result.onSuccess {
                Toast.makeText(app, "Password reset email sent!", Toast.LENGTH_SHORT).show()
                _authState.value = AuthState.Success("PasswordReset")
            }.onFailure {
                val message = it.message ?: "Password reset failed"
                Toast.makeText(app, message, Toast.LENGTH_SHORT).show()
                _authState.value = AuthState.Error(message)
            }
        }
    }

    // =============================================================
    // 🔹 Logout
    // =============================================================
    fun logout() {
        repository.logout()
        Toast.makeText(app, "Logged out successfully", Toast.LENGTH_SHORT).show()
        _authState.value = AuthState.Idle
    }
}