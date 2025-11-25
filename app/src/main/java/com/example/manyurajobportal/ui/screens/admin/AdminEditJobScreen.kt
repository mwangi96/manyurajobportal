package com.example.manyurajobportal.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.manyurajobportal.data.model.Job
import com.example.manyurajobportal.viewmodel.admin.AdminJobsViewModel

@Composable
fun AdminEditJobScreen(
    jobId: String,
    navController: NavController,
    viewModel: AdminJobsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val job by viewModel.getJobById(jobId).collectAsState(initial = null)

    if (job == null) {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val data = job!!

    // 📝 Editable State (pre-filled)
    var title by remember { mutableStateOf(data.jobTitle) }
    var company by remember { mutableStateOf(data.companyName) }
    var workplace by remember { mutableStateOf(data.workplace) }
    var employmentType by remember { mutableStateOf(data.employmentType) }
    var currency by remember { mutableStateOf(data.currency) }
    var salaryType by remember { mutableStateOf(data.salaryType) }
    var minSalary by remember { mutableStateOf(data.minSalary) }
    var maxSalary by remember { mutableStateOf(data.maxSalary) }
    var country by remember { mutableStateOf(data.country) }
    var city by remember { mutableStateOf(data.city) }
    var description by remember { mutableStateOf(data.jobDescription) }
    var skillsText by remember { mutableStateOf(data.skills.joinToString(", ")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Text("Edit Job Posting", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        // FORM FIELDS
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Job Title") }
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = company,
            onValueChange = { company = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Company Name") }
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = workplace,
            onValueChange = { workplace = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Workplace (Remote/Hybrid/On-site)") }
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = employmentType,
            onValueChange = { employmentType = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Employment Type (Full-time, Part-time…)") }
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = currency,
            onValueChange = { currency = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Currency (USD, KES, etc.)") }
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = salaryType,
            onValueChange = { salaryType = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Salary Type (Monthly / Yearly)") }
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = minSalary,
            onValueChange = { minSalary = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Minimum Salary") }
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = maxSalary,
            onValueChange = { maxSalary = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Maximum Salary") }
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = country,
            onValueChange = { country = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Country") }
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("City") }
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Job Description") },
            minLines = 3
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = skillsText,
            onValueChange = { skillsText = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Skills (comma separated)") }
        )

        Spacer(Modifier.height(24.dp))

        // SAVE BUTTON
        Button(
            onClick = {
                val updatedJob = Job(
                    jobId = data.jobId,
                    adminId = data.adminId,
                    jobTitle = title,
                    companyName = company,
                    workplace = workplace,
                    employmentType = employmentType,
                    currency = currency,
                    salaryType = salaryType,
                    minSalary = minSalary,
                    maxSalary = maxSalary,
                    country = country,
                    city = city,
                    jobDescription = description,
                    skills = skillsText.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                    timestamp = data.timestamp
                )

                viewModel.updateJob(updatedJob)
                navController.popBackStack()   // back to job details
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Changes")
        }
    }
}
