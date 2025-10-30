package com.example.manyurajobportal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.manyurajobportal.data.repository.AuthRepository
import com.example.manyurajobportal.navigation.NavGraph
import com.example.manyurajobportal.ui.screens.theme.ManyuraJobPortalTheme
import com.example.manyurajobportal.viewmodel.AuthViewModel
import com.example.manyurajobportal.viewmodel.AuthViewModelFactory
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {

    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Initialize Firebase FIRST — before any FirebaseAuth or Firestore call
        FirebaseApp.initializeApp(this)

        // ✅ Then safely create AuthRepository and ViewModel
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        val repository = AuthRepository(auth, firestore)
        val factory = AuthViewModelFactory(repository, application)
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        enableEdgeToEdge()

        setContent {
            ManyuraJobPortalTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // ✅ Pass the shared AuthViewModel to your NavGraph
                    NavGraph(
                        navController = navController,
                        authViewModel = authViewModel
                    )
                }
            }
        }
    }
}
