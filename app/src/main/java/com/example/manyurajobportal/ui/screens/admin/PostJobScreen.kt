package com.example.manyurajobportal.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.manyurajobportal.utils.SaveJobToFirebase
import com.example.manyurajobportal.utils.SharedViewModel
import com.google.firebase.auth.FirebaseAuth
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

    val saveJobToFirebase = SaveJobToFirebase()

    // ------------------- FORM DATA -------------------
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

    var skillInput by remember { mutableStateOf("") }
    var skillsList by remember { mutableStateOf(listOf<String>()) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)        // Use scaffold padding ONLY
                .padding(horizontal = 16.dp)  // Removed extra top padding
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(10.dp) // Reduced spacing
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Post a Job",
                style = MaterialTheme.typography.headlineSmall,
            )
            Divider()

            // -------- INPUT FIELDS --------
            OutlinedTextField(jobTitle, { jobTitle = it }, label = { Text("Job Title") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(companyName, { companyName = it }, label = { Text("Company Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(workplace, { workplace = it }, label = { Text("Workplace Type") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(employmentType, { employmentType = it }, label = { Text("Employment Type") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(currency, { currency = it }, label = { Text("Currency") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(salaryType, { salaryType = it }, label = { Text("Salary Type") }, modifier = Modifier.fillMaxWidth())

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(minSalary, { minSalary = it }, label = { Text("Min Salary") }, modifier = Modifier.weight(1f))
                OutlinedTextField(maxSalary, { maxSalary = it }, label = { Text("Max Salary") }, modifier = Modifier.weight(1f))
            }

            OutlinedTextField(country, { country = it }, label = { Text("Country") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(city, { city = it }, label = { Text("City") }, modifier = Modifier.fillMaxWidth())

            OutlinedTextField(
                jobDescription,
                { jobDescription = it },
                label = { Text("Job Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp) // Slightly smaller height
            )

            // -------- SKILLS --------
            Text("Required Skills", style = MaterialTheme.typography.titleMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
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
                ) { Text("Add") }
            }

            if (skillsList.isNotEmpty()) {
                Column {
                    Text("Skills Added:", style = MaterialTheme.typography.labelLarge)
                    skillsList.forEach { skill -> Text("• $skill") }
                }
            }

            // -------- SUBMIT BUTTON --------
            Button(
                onClick = {
                    // validate fields
                    if (
                        jobTitle.isBlank() || companyName.isBlank() || workplace.isBlank() ||
                        employmentType.isBlank() || currency.isBlank() || salaryType.isBlank() ||
                        minSalary.isBlank() || maxSalary.isBlank() || country.isBlank() ||
                        city.isBlank() || jobDescription.isBlank()
                    ) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Please fill all fields!")
                        }
                        return@Button
                    }

                    val adminId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

                    saveJobToFirebase.saveJobToFirebase(
                        jobTitle,
                        companyName,
                        workplace,
                        employmentType,
                        currency,
                        salaryType,
                        minSalary,
                        maxSalary,
                        country,
                        city,
                        skillsList,
                        jobDescription,
                        adminId,
                        onSuccess = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Job Posted Successfully!")
                            }

                            val role = sharedViewModel.currentUserRole.value

                            if (role == "admin") {
                                navController.navigate("posted_jobs") {
                                    popUpTo("post_job") { inclusive = true }
                                }
                            } else {
                                navController.navigate("alumni_dashboard") {
                                    popUpTo("post_job") { inclusive = true }
                                }
                            }
                        },
                        onFailure = { e ->
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Failed: ${e.message}")
                            }
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text("Post Job")
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
