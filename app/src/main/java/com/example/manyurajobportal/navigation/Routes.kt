sealed class Routes(val route: String) {

    object LoadScreen : Routes("load")        // ⭐ NEW
    object IntroScreen : Routes("intro")
    object LoginScreen : Routes("login")
    object SignUpScreen : Routes("signup")

    object AdminDashboard : Routes("admin_dashboard")
    object PostJobScreen : Routes("post_job")
    object PostedJobScreen : Routes("posted_jobs")

    object AlumniDashboard : Routes("alumni_dashboard?tab={tab}")

    object ApplyScreen : Routes("apply/{jobId}") {
        fun passId(id: String) = "apply/$id"
    }

    object ApplicationScreen : Routes("application_screen")

    object JobDetailsScreen : Routes("jobDetails/{jobId}") {
        fun passId(id: String) = "jobDetails/$id"
    }

    object ApplicantsScreen : Routes("applicants/{jobId}") {
        fun passId(id: String) = "applicants/$id"
    }


}
