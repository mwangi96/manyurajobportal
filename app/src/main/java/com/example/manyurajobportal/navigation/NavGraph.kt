package com.example.manyurajobportal.navigation

import android.text.Layout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
//import androidx.compose.ui.Alignment
import com.example.manyurajobportal.ui.screens.admin.*
import com.example.manyurajobportal.ui.screens.auth.LoginScreen
import com.example.manyurajobportal.ui.screens.auth.SignUpScreen
import com.example.manyurajobportal.ui.screens.dashboard.AdminDashboardScreen
import com.example.manyurajobportal.ui.screens.dashboard.AlumniDashboardScreen
import com.example.manyurajobportal.ui.screens.intro.IntroScreen
import com.example.manyurajobportal.ui.screens.alumni.*
import com.example.manyurajobportal.viewmodel.AuthViewModel
import com.example.manyurajobportal.viewmodel.SharedViewModel
import com.example.manyurajobportal.viewmodel.alumni.AlumniJobViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val sharedViewModel: SharedViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.IntroScreen.route
    ) {

        // -------------------------------
        // 🔹 AUTH ROUTES
        // -------------------------------
        composable(Routes.IntroScreen.route) { IntroScreen(navController) }

        composable(Routes.LoginScreen.route) {
            LoginScreen(navController, authViewModel, sharedViewModel)
        }

        composable(Routes.SignUpScreen.route) {
            SignUpScreen(navController, authViewModel)
        }

        // -------------------------------
        // 🔹 ADMIN ROUTES
        // -------------------------------
        composable(Routes.AdminDashboard.route) {
            AdminDashboardScreen(navController, sharedViewModel)
        }

        composable(Routes.AdminJobDetails.route) { backStack ->
            val jobId = backStack.arguments?.getString("jobId")!!
            AdminJobDetailsScreen(jobId, navController)
        }

        composable(Routes.AdminEditJob.route) { backStack ->
            val jobId = backStack.arguments?.getString("jobId")!!
            AdminEditJobScreen(jobId, navController)
        }

        composable(Routes.Applicants.route) { backStack ->
            val jobId = backStack.arguments?.getString("jobId")!!
            ApplicantsScreen(jobId, navController)
        }

        composable(Routes.PostJobScreen.route) {
            PostedJobScreen(navController, sharedViewModel)
        }

        // -------------------------------
        // 🔹 ALUMNI ROUTES
        // -------------------------------
        composable(
            route = Routes.AlumniDashboard.route,
            arguments = listOf(navArgument("tab") {
                type = NavType.StringType
                defaultValue = "Home"
            })
        ) {
            AlumniDashboardScreen(navController, sharedViewModel)
        }

        composable(Routes.CreateProfile.route) {
            AlumniProfileScreen(navController, sharedViewModel)
        }

        composable(Routes.AlumniJobDetails.route) { backStack ->
            val jobId = backStack.arguments?.getString("jobId")!!
            AlumniJobDetailsScreen(jobId = jobId, navController = navController)
        }

        composable(Routes.ApplyScreen.route) { backStack ->
            val jobId = backStack.arguments?.getString("jobId")!!
            val vm: AlumniJobViewModel = viewModel()

            LaunchedEffect(jobId) {
                vm.loadJob(jobId)
            }

            val jobState by vm.job.collectAsState()

            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

            if (jobState == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                ApplyScreen(
                    navController = navController,
                    job = jobState!!,
                    userId = userId
                )
            }

        }


        composable(Routes.ApplicationsScreen.route) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            ApplicationsScreen(
                navController = navController,
                sharedViewModel = sharedViewModel,
                userId = userId
            )
        }

    }
}
