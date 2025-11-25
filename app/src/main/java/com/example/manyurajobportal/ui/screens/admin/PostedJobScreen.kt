package com.example.manyurajobportal.ui.screens.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.manyurajobportal.data.model.Job
import com.example.manyurajobportal.ui.screens.components.JobCard
import com.example.manyurajobportal.viewmodel.SharedViewModel
import com.example.manyurajobportal.viewmodel.admin.AdminJobsViewModel
import com.example.manyurajobportal.viewmodel.alumni.AlumniJobViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostedJobScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    val isAdmin = sharedViewModel.userRole.value == "admin"

    // 👇 Correct ViewModel based on role
    val adminVM: AdminJobsViewModel? = if (isAdmin) viewModel() else null
    val alumniVM: AlumniJobViewModel? = if (!isAdmin) viewModel() else null

    // 👇 Unified state
    val jobs by (adminVM?.jobs ?: alumniVM?.jobs)!!.collectAsState()
    val search by (adminVM?.search ?: alumniVM?.search)!!.collectAsState()
    val isLoading by (adminVM?.loading ?: alumniVM?.loading)!!.collectAsState()

    // 👇 Load jobs when screen opens
    LaunchedEffect(isAdmin) {
        if (isAdmin) {
            adminVM?.loadJobs()
        } else {
            alumniVM?.loadJobs()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // 🔍 Search bar
        OutlinedTextField(
            value = search,
            onValueChange = { query ->
                if (isAdmin) adminVM?.onSearchChange(query)
                else alumniVM?.onSearchChange(query)
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search jobs…") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ⏳ Loading UI
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            return
        }

        // ❗ No jobs
        if (jobs.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No jobs available.", style = MaterialTheme.typography.bodyLarge)
            }
            return
        }

        // 📌 Jobs list
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(jobs) { job ->

                // ⭐ Only check applied status for alumni
                val hasApplied by if (!isAdmin) {
                    produceState<Boolean?>(initialValue = null, key1 = job.jobId) {
                        value = alumniVM?.hasUserApplied(job.jobId) ?: false
                    }
                } else {
                    remember { mutableStateOf(false) }   // ✅ Fixed
                }


                JobCard(
                    job = job,
                    isAdmin = isAdmin,
                    hasApplied = hasApplied == true,   // ⭐ APPLY STATUS HERE
                    applicantCount = job.applicantCount,

                    onApplyClick = {
                        if (!isAdmin) {
                            navController.navigate("alumni_job_details/${job.jobId}")
                        }
                    },

                    onViewApplicantsClick = {
                        if (isAdmin) {
                            navController.navigate("applicants/${job.jobId}")
                        }
                    },

                    onManageJobClick = {
                        if (isAdmin) {
                            navController.navigate("admin_edit_job/${job.jobId}")
                        }
                    },

                    onClick = {
                        if (isAdmin) {
                            navController.navigate("admin_job_details/${job.jobId}")
                        } else {
                            navController.navigate("alumni_job_details/${job.jobId}")
                        }
                    }
                )
            }
        }
    }
}
