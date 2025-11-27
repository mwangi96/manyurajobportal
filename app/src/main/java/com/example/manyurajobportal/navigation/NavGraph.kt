package com.example.manyurajobportal.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.manyurajobportal.ui.screens.admin.*
import com.example.manyurajobportal.ui.screens.auth.LoginScreen
import com.example.manyurajobportal.ui.screens.auth.SignUpScreen
import com.example.manyurajobportal.ui.screens.dashboard.AdminDashboardScreen
import com.example.manyurajobportal.ui.screens.dashboard.AlumniDashboardScreen
import com.example.manyurajobportal.ui.screens.intro.IntroScreen
import com.example.manyurajobportal.ui.screens.alumni.*
import com.example.manyurajobportal.ui.screens.intro.LoadScreen
import com.example.manyurajobportal.utils.SharedViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    sharedViewModel: SharedViewModel
) {

    NavHost(
        navController = navController,
        startDestination = Routes.LoadScreen.route   // ⭐ START HERE
    ) {

        // -------------------------------
        // 🔹 LOAD SCREEN
        // -------------------------------
        composable(Routes.LoadScreen.route) {
            LoadScreen(navController)
        }

        // -------------------------------
        // 🔹 AUTH ROUTES
        // -------------------------------
        composable(Routes.IntroScreen.route) {
            IntroScreen(navController)
        }

        composable(Routes.LoginScreen.route) {
            LoginScreen(navController, sharedViewModel)
        }

        composable(Routes.SignUpScreen.route) {
            SignUpScreen(navController, sharedViewModel)
        }

        // -------------------------------
        // 🔹 ADMIN ROUTES
        // -------------------------------
        composable(Routes.AdminDashboard.route) {
            AdminDashboardScreen(navController, sharedViewModel)
        }

        composable(Routes.PostedJobScreen.route) {
            PostedJobScreen(navController, sharedViewModel)
        }


        composable(Routes.PostJobScreen.route) {
            PostJobScreen(navController, sharedViewModel)
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

        composable(
            route = Routes.ApplicationScreen.route,
            arguments = listOf(navArgument("jobId") { type = NavType.StringType })
        ) {
            ApplicationScreen(navController, sharedViewModel)
        }




    }
}