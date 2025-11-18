package com.example.manyurajobportal.ui.screens.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.manyurajobportal.viewmodel.admin.AdminJobsViewModel
import com.example.manyurajobportal.data.model.Job

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostedJobScreen(
    navController: NavController,
    viewModel: AdminJobsViewModel = viewModel()
) {
    val jobs by viewModel.jobs.collectAsState()
    val search by viewModel.search.collectAsState()
    val isLoading by viewModel.loading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // 🔍 Search Bar
        OutlinedTextField(
            value = search,
            onValueChange = viewModel::onSearchChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search posted jobs…") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ⏳ Loading State
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }

        // ❗ Empty UI
        if (jobs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("No jobs posted yet.", style = MaterialTheme.typography.bodyLarge)
            }
            return
        }

        // 📌 Jobs list
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(jobs) { job ->
                JobCard(
                    job = job,
                    onClick = {
                        // OPTIONAL: navigate to job details
                        // navController.navigate("job_details/${job.jobId}")
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JobCard(
    job: Job,
    isAdmin: Boolean = false,   // Pass this from your dashboard
    onClick: () -> Unit = {},   // Handle navigation outside
    onManageJobClick: () -> Unit = {}, // Admin button
    onApplyClick: () -> Unit = {}      // Alumni button
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // 🔵 Title + Company
            Text(job.jobTitle, style = MaterialTheme.typography.titleLarge)
            Text(job.companyName, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(6.dp))

            // 📍 Location
            Text("${job.city}, ${job.country}", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(12.dp))

            // 💰 Salary Range
            Text(
                text = "Salary: ${job.currency} ${job.minSalary} - ${job.maxSalary} (${job.salaryType})",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ============================
            //  🔘 ACTION BUTTON (Admin vs Alumni)
            // ============================
            if (isAdmin) {
                Button(
                    onClick = onManageJobClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Manage Job")     // Future: show applicants count here
                }
            } else {
                Button(
                    onClick = onApplyClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Apply Now")
                }
            }
        }
    }
}
