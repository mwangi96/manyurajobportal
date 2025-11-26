package com.example.manyurajobportal.navigation

sealed class Routes(val route: String) {

    // -------------------------
    // 🔐 AUTH ROUTES
    // -------------------------
    object IntroScreen : Routes("intro")
    object LoginScreen : Routes("login")
    object SignUpScreen : Routes("signup")

    // -------------------------
    // 🧑‍💼 ADMIN ROUTES
    // -------------------------
    object AdminDashboard : Routes("admin_dashboard")
    object AdminJobDetails : Routes("admin_job_details/{jobId}")
    object AdminEditJob : Routes("admin_edit_job/{jobId}")
    object Applicants : Routes("applicants/{jobId}")
    object PostJobScreen : Routes("post_job")
    object PostedJobScreen : Routes("posted_jobs")

    // -------------------------
    // 🎓 ALUMNI ROUTES
    // -------------------------
    object AlumniDashboard : Routes("alumni_dashboard?")

    object AlumniJobDetails : Routes("alumni_job_details/{jobId}")
    object ApplyScreen : Routes("apply/{jobId}")
    object ApplicationsScreen : Routes("applications")

    // -------------------------
    // 👤 PROFILE ROUTES
    // -------------------------
    object CreateProfile : Routes("create_profile")
}
