package com.example.manyurajobportal.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProfileRepository(
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun hasProfile(): Boolean {
        val uid = FirebaseAuth.getInstance().uid ?: return false
        val doc = db.collection("profiles").document(uid).get().await()
        return doc.exists()
    }

    suspend fun getUserInfo(): Map<String, String> {
        val uid = FirebaseAuth.getInstance().uid ?: return emptyMap()
        val doc = db.collection("users").document(uid).get().await()

        return mapOf(
            "fullName" to (doc.getString("fullName") ?: ""),
            "email" to (doc.getString("email") ?: "")
        )
    }

    suspend fun createProfile(phone: String, location: String): Boolean {
        val uid = FirebaseAuth.getInstance().uid ?: return false

        val info = getUserInfo()

        val data = mapOf(
            "fullName" to info["fullName"],
            "email" to info["email"],
            "phone" to phone,
            "location" to location
        )

        return try {
            db.collection("profiles").document(uid).set(data).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateProfile(phone: String, location: String): Boolean {
        val uid = FirebaseAuth.getInstance().uid ?: return false

        return try {
            db.collection("profiles")
                .document(uid)
                .update(
                    mapOf(
                        "phone" to phone,
                        "location" to location
                    )
                ).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
