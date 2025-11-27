package com.example.manyurajobportal.ui.screens.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.manyurajobportal.ui.screens.components.JobCard
import com.example.manyurajobportal.utils.JobData
import com.example.manyurajobportal.utils.SharedViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostedJobScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    val db = FirebaseFirestore.getInstance()

    var jobs by remember { mutableStateOf<List<JobData>>(emptyList()) }
    var search by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    // -----------------------------------------
    // 🔥 Fetch jobs + applicant count
    // -----------------------------------------
    LaunchedEffect(Unit) {
        try {
            val result = db.collection("jobs")
                .get()
                .await()

            val jobList = result.map { it.toObject(JobData::class.java) }

            // For each job → fetch applicant count
            val updated = jobList.map { job ->
                val apps = db.collection("applications")
                    .whereEqualTo("jobId", job.jobId)
                    .get()
                    .await()

                job.copy(applicantCount = apps.size())
            }

            jobs = updated
        } catch (e: Exception) {
            errorMsg = e.message
        }
        loading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // 🔍 Search Bar
        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search jobs…") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ⏳ Loading
        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }

        // ❗ Error
        errorMsg?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
            return
        }

        // ❗ Empty
        if (jobs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No jobs posted yet.")
            }
            return
        }

        // 🔎 Filtered jobs
        val filteredJobs = jobs.filter {
            it.jobTitle.contains(search, ignoreCase = true) ||
                    it.companyName.contains(search, ignoreCase = true)
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(filteredJobs.size) { index ->
                val job = filteredJobs[index]
                JobCard(
                    job = job,
                    isAdmin = true,
                    applicantCount = job.applicantCount,
                    onViewApplicantsClick = {
                        navController.navigate("applicants/${job.jobId}")
                    },
                    onManageJobClick = {
                        navController.navigate("editJob/${job.jobId}")
                    },
                    onClick = {
                        navController.navigate("jobDetails/${job.jobId}")
                    }
                )

            }
        }
    }
}
