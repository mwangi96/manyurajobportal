package com.example.manyurajobportal.ui.screens.alumni

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    job?.let { j ->
        Column(modifier = Modifier.padding(16.dp)) {

            Text(j.jobTitle, style = MaterialTheme.typography.headlineSmall)
            Text(j.companyName, style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(10.dp))
            Text("${j.city}, ${j.country}")

            Spacer(Modifier.height(10.dp))
            Text("Workplace: ${j.workplace}")
            Text("Employment: ${j.employmentType}")

            Spacer(Modifier.height(10.dp))
            Text("Salary: ${j.currency} ${j.minSalary} - ${j.maxSalary} (${j.salaryType})")

            Spacer(Modifier.height(12.dp))
            Text("Job Description:", fontWeight = FontWeight.Bold)
            Text(j.jobDescription)

            Spacer(Modifier.height(12.dp))
            Text("Skills:", fontWeight = FontWeight.Bold)
            j.skills.forEach { Text("- $it") }

            Spacer(Modifier.height(20.dp))

            when {
                hasApplied == true -> {
                    Button(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Already Applied")
                    }
                }
                else -> {
                    Button(
                        onClick = { navController.navigate("apply/$jobId") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Apply Now")
                    }
                }
            }
        }
    }
}
