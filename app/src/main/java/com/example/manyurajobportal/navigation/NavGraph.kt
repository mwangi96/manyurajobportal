package com.example.manyurajobportal.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.manyurajobportal.ui.screens.admin.PostJobScreen
import com.example.manyurajobportal.ui.screens.auth.LoginScreen
import com.example.manyurajobportal.ui.screens.auth.SignUpScreen
import com.example.manyurajobportal.ui.screens.dashboard.AdminDashboardScreen
import com.example.manyurajobportal.ui.screens.dashboard.AlumniDashboardScreen
import com.example.manyurajobportal.ui.screens.intro.IntroScreen
import com.example.manyurajobportal.viewmodel.AuthViewModel
import com.example.manyurajobportal.viewmodel.SharedViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    // ✅ Create a single instance of SharedViewModel for the entire navigation graph
    val sharedViewModel: SharedViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.IntroScreen.route
    ) {
        // 🏁 Intro Screen
        composable(Routes.IntroScreen.route) {
            IntroScreen(navController)
        }

        // 🔐 Login Screen
        composable(Routes.LoginScreen.route) {
            LoginScreen(
                navController = navController,
                authViewModel = authViewModel,
                sharedViewModel = sharedViewModel // ✅ pass here
            )
        }

        // 🧾 Sign Up Screen
        composable(Routes.SignUpScreen.route) {
            SignUpScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        // 🧑‍💼 Admin Dashboard
        composable(Routes.AdminDashboard.route) {
            AdminDashboardScreen(
                navController = navController,
                sharedViewModel = sharedViewModel // ✅ now you can access user info here
            )
        }

        // 🎓 Alumni Dashboard
        composable(Routes.AlumniDashboard.route) {
            AlumniDashboardScreen(
                navController = navController,
                sharedViewModel = sharedViewModel // ✅ same for alumni
            )
        }

        // 🔜 Add more screens (Job Details, Apply, Chat, etc.) below using sharedViewModel when needed

        // 📝 Post Job Screen
        composable(Routes.PostJobScreen.route) {
            PostJobScreen(
                navController = navController,
                sharedViewModel = sharedViewModel
            )
        }
    }
}
