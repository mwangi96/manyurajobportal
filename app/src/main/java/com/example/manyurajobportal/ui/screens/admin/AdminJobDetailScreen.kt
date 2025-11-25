package com.example.manyurajobportal.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.manyurajobportal.viewmodel.admin.AdminJobsViewModel

@Composable
fun AdminJobDetailsScreen(
    jobId: String,
    navController: NavController,
    viewModel: AdminJobsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {

    val job by viewModel.getJobById(jobId).collectAsState(initial = null)
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (job == null) {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val jobData = job!!

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(jobData.jobTitle, style = MaterialTheme.typography.headlineSmall)
        Text(jobData.companyName, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(8.dp))

        Text("${jobData.city}, ${jobData.country}", style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(12.dp))

        Text(
            "Salary: ${jobData.currency} ${jobData.minSalary} - ${jobData.maxSalary} (${jobData.salaryType})",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(16.dp))

        Text("Job Description", style = MaterialTheme.typography.titleMedium)
        Text(jobData.jobDescription)

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = { navController.navigate("admin_edit_job/${jobData.jobId}") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Edit Job")
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = { navController.navigate("applicants/${jobData.jobId}") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("View Applicants")
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = { showDeleteDialog = true },
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Delete Job")
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Job?") },
            text = { Text("Are you sure you want to delete this job? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteJob(jobData.jobId)
                    showDeleteDialog = false
                    navController.popBackStack()
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
