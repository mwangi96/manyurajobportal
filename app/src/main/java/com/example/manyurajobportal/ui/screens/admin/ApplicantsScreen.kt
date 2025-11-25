package com.example.manyurajobportal.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.manyurajobportal.viewmodel.admin.ApplicantsViewModel

@Composable
fun ApplicantsScreen(
    jobId: String,
    navController: NavController,
    viewModel: ApplicantsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val applicants by viewModel.applicants.collectAsState()
    val loading by viewModel.loading.collectAsState()

    LaunchedEffect(true) {
        viewModel.loadApplicants(jobId)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Applicants", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        if (loading) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }

        if (applicants.isEmpty()) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text("No applicants yet")
            }
            return
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(applicants) { app ->
                ApplicantCard(
                    jobId = jobId, // pass jobId here
                    applicantId = app["id"].toString(),
                    name = app["fullName"].toString(),
                    email = app["email"].toString(),
                    phone = app["phone"].toString(),
                    location = app["location"].toString(),
                    appliedOn = app["timestamp"] as Long,
                    status = app["status"] as String,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun ApplicantCard(
    jobId: String,
    applicantId: String,
    name: String,
    email: String,
    phone: String,
    location: String,
    appliedOn: Long,
    status: String,
    viewModel: ApplicantsViewModel
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(name, style = MaterialTheme.typography.titleMedium)
            Text(email, style = MaterialTheme.typography.bodyMedium)
            Text("Phone: $phone", style = MaterialTheme.typography.bodySmall)
            Text("Location: $location", style = MaterialTheme.typography.bodySmall)

            Spacer(Modifier.height(8.dp))

            Text(
                "Applied: ${java.text.SimpleDateFormat("dd MMM yyyy").format(appliedOn)}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                "Status: ${status.replace("_", " ").replaceFirstChar { it.uppercase() }}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(Modifier.height(8.dp))

            Row {
                Button(onClick = { viewModel.updateStatus(jobId, applicantId, "approved") }) {
                    Text("Approve")
                }
                Spacer(Modifier.width(8.dp))
                Button(onClick = { viewModel.updateStatus(jobId, applicantId, "rejected") }) {
                    Text("Reject")
                }
            }
        }
    }
}
