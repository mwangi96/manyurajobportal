package com.example.manyurajobportal.ui.screens.alumni

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.manyurajobportal.data.model.Job
import com.example.manyurajobportal.navigation.Routes
import com.example.manyurajobportal.viewmodel.alumni.ApplicationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplyScreen(
    navController: NavController,
    job: Job,
    userId: String,
    applicationViewModel: ApplicationViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val loading by applicationViewModel.loading.collectAsState()
    val success by applicationViewModel.applicationSuccess.collectAsState()

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    LaunchedEffect(success) {
        if (success) {
            navController.navigate("alumni_job_details/${job.jobId}") {
                popUpTo(Routes.ApplyScreen.route) { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Apply for Job") })
        }
    ) { padding ->

        Column(
            Modifier
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Text("Job Information", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            Text("Title: ${job.jobTitle}")
            Text("Company: ${job.companyName}")
            Text("Location: ${job.city}, ${job.country}")
            Text("Salary: ${job.currency} ${job.minSalary} - ${job.maxSalary}")

            Spacer(Modifier.height(20.dp))

            Text("Your Details", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    applicationViewModel.applyForJob(
                        jobId = job.jobId,
                        userId = userId,
                        fullName = fullName,
                        email = email,
                        phone = phone,
                        location = location
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !loading
            ) {
                if (loading) CircularProgressIndicator(Modifier.size(20.dp))
                else Text("Submit Application")
            }
        }
    }
}
