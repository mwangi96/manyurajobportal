package com.example.manyurajobportal.ui.screens.alumni

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.manyurajobportal.viewmodel.alumni.AlumniJobViewModel

@Composable
fun AlumniJobDetailsScreen(
    navController: NavController,
    jobId: String,
    viewModel: AlumniJobViewModel = viewModel()
) {
    val job by viewModel.job.collectAsState()
    val hasApplied by viewModel.hasApplied.collectAsState()
    val loading by viewModel.loading.collectAsState()

    LaunchedEffect(jobId) {
        viewModel.loadJob(jobId)
        viewModel.checkIfApplied(jobId)
    }

    if (loading) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    job?.let { j ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            // Job Title & Company
            Text(
                j.jobTitle,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                j.companyName,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(16.dp))

            // Basic info section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(Modifier.padding(16.dp)) {
                    InfoRow("Location", "${j.city}, ${j.country}")
                    InfoRow("Workplace", j.workplace)
                    InfoRow("Employment", j.employmentType)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Salary card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "Salary",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "${j.currency} ${j.minSalary} - ${j.maxSalary} (${j.salaryType})",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Description
            Text(
                "Job Description",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            Text(j.jobDescription, style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(16.dp))

            // Skills section
            Text(
                "Required Skills",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            Column {
                j.skills.forEach { skill ->
                    Text("• $skill", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(Modifier.height(24.dp))

            // Apply Button
            if (hasApplied == true) {
                Button(
                    onClick = {},
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Already Applied")
                }
            } else {
                Button(
                    onClick = { navController.navigate("apply/$jobId") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Apply Now")
                }
            }

            Spacer(Modifier.height(30.dp))
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column(Modifier.padding(vertical = 6.dp)) {
        Text(label, fontWeight = FontWeight.SemiBold)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}
