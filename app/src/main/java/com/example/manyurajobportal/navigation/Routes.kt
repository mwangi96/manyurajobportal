package com.example.manyurajobportal.navigation

sealed class Routes(val route: String) {
    object IntroScreen : Routes("intro")
    object LoginScreen : Routes("login")
    object SignUpScreen : Routes("signup")

    // Add these two routes for role-based navigation
    object AdminDashboard : Routes("admin_dashboard")
    object AlumniDashboard : Routes("alumni_dashboard")
}
