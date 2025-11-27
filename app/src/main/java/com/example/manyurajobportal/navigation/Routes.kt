sealed class Routes(val route: String) {

    object LoadScreen : Routes("load")        // ⭐ NEW
    object IntroScreen : Routes("intro")
    object LoginScreen : Routes("login")
    object SignUpScreen : Routes("signup")

    object AdminDashboard : Routes("admin_dashboard")
    object PostJobScreen : Routes("post_job")
    object PostedJobScreen : Routes("posted_jobs")

    object AlumniDashboard : Routes("alumni_dashboard?tab={tab}")

    object ApplicationScreen : Routes("application_screen/{jobId}") {
        fun createRoute(jobId: String) = "application_screen/$jobId"
    }

}
