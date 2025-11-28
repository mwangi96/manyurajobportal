package com.example.manyurajobportal.ui.screens.jobs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.manyurajobportal.utils.JobData
import com.example.manyurajobportal.utils.SharedViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun JobDetailsScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    jobId: String
) {
    val db = FirebaseFirestore.getInstance()
    var job by remember { mutableStateOf<JobData?>(null) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val role by sharedViewModel.currentUserRole.collectAsState()
    val isAdmin = role == "admin"

    LaunchedEffect(jobId) {
        try {
            val doc = db.collection("jobs").document(jobId).get().await()
            job = doc.toObject(JobData::class.java)
        } catch (e: Exception) {
            error = e.message
        }
        loading = false
    }

    if (loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (error != null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error: $error")
        }
        return
    }

    job?.let { item ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // --- Job Header ---
            Text(
                text = item.jobTitle,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = item.companyName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // --- Job Details Card ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text("Workplace Type: ${item.workplace}")
                    Text("Employment Type: ${item.employmentType}")
                    Text("Location: ${item.city}, ${item.country}")

                    Text(
                        "Salary: ${item.currency} ${item.minSalary} - ${item.maxSalary} (${item.salaryType})"
                    )

                    Text("Description:")
                    Text(item.jobDescription)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // --- Action Buttons ---
            if (isAdmin) {

                Button(
                    onClick = {
                        navController.navigate(
                            Routes.ApplicantsScreen.passId(jobId)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("View Applicants")
                }

                Button(
                    onClick = { navController.navigate("editJob/$jobId") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Edit Job")
                }

                Button(
                    onClick = {
                        db.collection("jobs").document(jobId).delete()
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Delete Job")
                }
            } else {
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
