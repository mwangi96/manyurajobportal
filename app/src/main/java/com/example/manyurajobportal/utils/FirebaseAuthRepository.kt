package com.example.manyurajobportal.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    /* ---------------------------------------------------------
     * REGISTER USER + SAVE TO FIRESTORE
     * --------------------------------------------------------- */
    suspend fun signup(name: String, email: String, password: String): Boolean {
        return try {
            // Create user
            auth.createUserWithEmailAndPassword(email, password).await()
            val user = auth.currentUser ?: return false

            // Update displayName
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()

            user.updateProfile(profileUpdates).await()

            // Save to Firestore
            val userMap = mapOf(
                "uid" to user.uid,
                "name" to name,
                "email" to email,
                "role" to "alumni",
                "createdAt" to System.currentTimeMillis()
            )

            firestore.collection("users")
                .document(user.uid)
                .set(userMap)
                .await()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /* ---------------------------------------------------------
     * LOGIN USER
     * --------------------------------------------------------- */
    suspend fun login(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /* ---------------------------------------------------------
     * LOGOUT USER (Corrected)
     * --------------------------------------------------------- */
    fun logout() {
        auth.signOut()   // ❗ Correct logout
    }

    /* ---------------------------------------------------------
     * CHECK IF USER IS LOGGED IN
     * --------------------------------------------------------- */
    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    /* ---------------------------------------------------------
     * CURRENT USER OBJECT
     * --------------------------------------------------------- */
    fun currentUser(): FirebaseUser? {
        return auth.currentUser
    }

    /* ---------------------------------------------------------
     * RESET PASSWORD
     * --------------------------------------------------------- */
    suspend fun resetPassword(email: String): Boolean {
        return try {
            auth.sendPasswordResetEmail(email).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /* ---------------------------------------------------------
     * GET USER FIRESTORE DOCUMENT
     * --------------------------------------------------------- */
    suspend fun getUserDocument(userId: String): Map<String, Any>? {
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            snapshot.data
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /* ---------------------------------------------------------
     * UPDATE USER DOCUMENT
     * --------------------------------------------------------- */
    suspend fun updateUserDocument(userId: String, updates: Map<String, Any>): Boolean {
        return try {
            firestore.collection("users")
                .document(userId)
                .update(updates)
                .await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
