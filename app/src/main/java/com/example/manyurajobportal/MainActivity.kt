package com.example.manyurajobportal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.manyurajobportal.navigation.NavGraph
import com.example.manyurajobportal.ui.screens.theme.ManyuraJobPortalTheme
import com.example.manyurajobportal.utils.FirebaseAuthRepository
import com.example.manyurajobportal.utils.SharedViewModel
import com.example.manyurajobportal.utils.SharedViewModelFactory
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {

    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Create Repository and ViewModel
        val firebaseRepo = FirebaseAuthRepository()
        sharedViewModel = ViewModelProvider(
            this,
            SharedViewModelFactory(firebaseRepo)
        )[SharedViewModel::class.java]

        enableEdgeToEdge()

        setContent {
            ManyuraJobPortalTheme {
                val navController = rememberNavController()

                NavGraph(
                    navController = navController,
                    sharedViewModel = sharedViewModel
                )
            }
        }
    }
}
