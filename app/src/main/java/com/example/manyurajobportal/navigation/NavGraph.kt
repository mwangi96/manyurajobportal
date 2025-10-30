package com.example.manyurajobportal.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.manyurajobportal.ui.screens.auth.LoginScreen
import com.example.manyurajobportal.ui.screens.auth.SignUpScreen
import com.example.manyurajobportal.ui.screens.dashboard.AdminDashboardScreen
import com.example.manyurajobportal.ui.screens.dashboard.AlumniDashboardScreen
import com.example.manyurajobportal.ui.screens.intro.IntroScreen
import com.example.manyurajobportal.viewmodel.AuthViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel // ✅ receive shared ViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Routes.IntroScreen.route
    ) {
        composable(Routes.IntroScreen.route) {
            IntroScreen(navController)
        }

        composable(Routes.LoginScreen.route) {
            // ✅ Pass the same ViewModel instance to LoginScreen
            LoginScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable(Routes.SignUpScreen.route) {
            // ✅ Pass the same ViewModel instance to SignupScreen
            SignUpScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable("admin_dashboard") {
            AdminDashboardScreen(navController, authViewModel)
        }
        composable("alumni_dashboard") {
            AlumniDashboardScreen(navController, authViewModel)
        }

        // 🔜 Add your other screens here (Dashboard, ForgotPassword, etc.)
        // Example:
        // composable(Routes.DashboardScreen.route) {
        //     DashboardScreen(navController, authViewModel)
        // }
    }
}
