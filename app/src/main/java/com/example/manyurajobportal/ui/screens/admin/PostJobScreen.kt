package com.example.manyurajobportal.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.manyurajobportal.data.model.Job
import com.example.manyurajobportal.data.repository.JobRepository
import com.example.manyurajobportal.viewmodel.JobViewModel
import com.example.manyurajobportal.viewmodel.JobViewModelFactory
import com.example.manyurajobportal.viewmodel.SharedViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostJobScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val jobRepository = remember { JobRepository(FirebaseFirestore.getInstance()) }
    val jobViewModel: JobViewModel = viewModel(factory = JobViewModelFactory(jobRepository))

    // Form fields
    var jobTitle by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    var workplace by remember { mutableStateOf("") }
    var employmentType by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("") }
    var salaryType by remember { mutableStateOf("") }
    var minSalary by remember { mutableStateOf("") }
    var maxSalary by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var jobDescription by remember { mutableStateOf("") }

    // ⭐ NEW: skills
    var skillInput by remember { mutableStateOf("") }
    var skillsList by remember { mutableStateOf(listOf<String>()) }

    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(18.dp)
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Post a New Job", style = MaterialTheme.typography.headlineMedium)
            Divider()

            // Job details
            Text("Job Details", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(jobTitle, { jobTitle = it }, label = { Text("Job Title") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(companyName, { companyName = it }, label = { Text("Company Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(workplace, { workplace = it }, label = { Text("Workplace") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(employmentType, { employmentType = it }, label = { Text("Employment Type") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(currency, { currency = it }, label = { Text("Currency") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(salaryType, { salaryType = it }, label = { Text("Salary Type") }, modifier = Modifier.fillMaxWidth())

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(minSalary, { minSalary = it }, label = { Text("Min Salary") }, modifier = Modifier.weight(1f))
                OutlinedTextField(maxSalary, { maxSalary = it }, label = { Text("Max Salary") }, modifier = Modifier.weight(1f))
            }

            OutlinedTextField(country, { country = it }, label = { Text("Country") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(city, { city = it }, label = { Text("City") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(
                jobDescription,
                { jobDescription = it },
                label = { Text("Job Description") },
                modifier = Modifier.fillMaxWidth().height(130.dp)
            )

            // ⭐ NEW — SKILLS INPUT
            Text("Required Skills", style = MaterialTheme.typography.titleMedium)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = skillInput,
                    onValueChange = { skillInput = it },
                    label = { Text("Enter skill") },
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        if (skillInput.isNotBlank()) {
                            skillsList = skillsList + skillInput.trim()
                            skillInput = ""
                        }
                    }
                ) {
                    Text("Add")
                }
            }

            // Show added skills
            if (skillsList.isNotEmpty()) {
                Column {
                    Text("Skills Added:", style = MaterialTheme.typography.labelLarge)
                    skillsList.forEach { skill ->
                        Text("• $skill")
                    }
                }
            }

            // Submit Button
            Button(
                onClick = {
                    // Validation
                    if (
                        jobTitle.isBlank() || companyName.isBlank() || workplace.isBlank() ||
                        employmentType.isBlank() || currency.isBlank() || salaryType.isBlank() ||
                        minSalary.isBlank() || maxSalary.isBlank() ||
                        country.isBlank() || city.isBlank() || jobDescription.isBlank()
                    ) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Please fill all fields!")
                        }
                        return@Button
                    }

                    val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                    val adminId = currentUser?.uid ?: ""

                    // Create job object
                    val job = Job(
                        jobId = "",
                        adminId = adminId,
                        jobTitle = jobTitle,
                        companyName = companyName,
                        workplace = workplace,
                        employmentType = employmentType,
                        currency = currency,
                        salaryType = salaryType,
                        minSalary = minSalary,
                        maxSalary = maxSalary,
                        country = country,
                        city = city,
                        jobDescription = jobDescription,
                        skills = skillsList,
                        timestamp = System.currentTimeMillis()
                    )

                    jobViewModel.postjob(job) { success, message ->
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(message)
                        }

                        if (success) {
                            navController.navigate("posted_jobs") {
                                popUpTo("post_job") { inclusive = true }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text("Post Job")
            }
        }
    }
}
