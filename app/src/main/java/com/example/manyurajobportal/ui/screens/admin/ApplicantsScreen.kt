package com.example.manyurajobportal.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.manyurajobportal.utils.SharedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicantsScreen(
    jobId: String,
    sharedViewModel: SharedViewModel,
    navController: NavController
) {
    val loading by sharedViewModel.loading.collectAsState()
    val applicants by remember { sharedViewModel.applicantsForJob } // State you'll add below

    LaunchedEffect(Unit) {
        sharedViewModel.fetchApplicantsForJob(jobId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Applicants") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        if (loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (applicants.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No applicants found for this job.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(applicants) { applicant ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {

                            Text(
                                "${applicant.firstName} ${applicant.lastName}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(4.dp))
                            Text("Email: ${applicant.userEmail}")
                            Text("Phone: ${applicant.phoneNumber}")
                            Text("Location: ${applicant.location}")

                            Spacer(Modifier.height(8.dp))

                            Text(
                                text = applicant.status,
                                color = when (applicant.status) {
                                    "Application Approved" -> Color(0xFF008000)
                                    "Application Denied" -> Color.Red
                                    else -> Color.Gray
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.background(Color(0x11000000))
                                    .padding(6.dp)
                            )

                            Spacer(Modifier.height(12.dp))

                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {

                                // APPROVE BUTTON
                                Button(
                                    onClick = {
                                        sharedViewModel.updateApplicationStatus(
                                            applicant.id,
                                            "Application Approved"
                                        )
                                    }
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                    Spacer(Modifier.width(6.dp))
                                    Text("Approve")
                                }

                                // DENY BUTTON
                                OutlinedButton(
                                    onClick = {
                                        sharedViewModel.updateApplicationStatus(
                                            applicant.id,
                                            "Application Denied"
                                        )
                                    }
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = null)
                                    Spacer(Modifier.width(6.dp))
                                    Text("Deny")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
