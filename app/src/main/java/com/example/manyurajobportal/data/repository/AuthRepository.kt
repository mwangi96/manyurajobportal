package com.example.manyurajobportal.data.repository

import com.example.manyurajobportal.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    // ✅ Sign Up + Automatically assign "alumni" role
    suspend fun signUpWithEmail(
        name: String,
        email: String,
        password: String
    ): Result<Unit> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("User UID is null")

            // Automatically assign alumni role
            val user = User(
                uid = uid,
                name = name,
                email = email,
                role = "alumni"
            )

            firestore.collection("users").document(uid).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ✅ Login
    suspend fun loginWithEmail(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ✅ Get user role (for navigation)
    suspend fun getUserRole(uid: String): String? {
        val snapshot = firestore.collection("users").document(uid).get().await()
        return snapshot.getString("role")
    }

    // ✅ Logout
    fun logout() = auth.signOut()

    // ✅ Get current user
    fun currentUser() = auth.currentUser

    // ✅ Password reset
    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
