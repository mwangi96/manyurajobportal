package com.example.manyurajobportal.ui.screens.admin

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.manyurajobportal.data.model.Job
import com.example.manyurajobportal.viewmodel.JobViewModel
import com.example.manyurajobportal.viewmodel.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostJobScreen(
    navController: NavController,
    jobViewModel: JobViewModel = viewModel(),
    sharedViewModel: SharedViewModel
) {
    val context = LocalContext.current

    // --- Form State ---
    var jobTitle by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    var workplace by remember { mutableStateOf("Select") }
    val workplaceOptions = listOf("On-site", "Remote", "Hybrid")

    var employmentType by remember { mutableStateOf("Select") }
    val employmentOptions = listOf("Full-time", "Part-time", "Contract", "Internship")

    var currency by remember { mutableStateOf("Select") }
    val currencyOptions = listOf("USD", "Euro", "KSH")

    var salaryType by remember { mutableStateOf("Select") }
    val salaryTypeOptions = listOf("Hourly", "Weekly", "Monthly")

    var minSalary by remember { mutableStateOf("") }
    var maxSalary by remember { mutableStateOf("") }

    var jobDescription by remember { mutableStateOf("") }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // --- Image Picker ---
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> selectedImageUri = uri }

    // --- Countries & Cities ---
    val countries = jobViewModel.countryList
    val cities = jobViewModel.cityList
    val selectedCountry = jobViewModel.selectedCountry
    val selectedCity = jobViewModel.selectedCity

    var expandedCountry by remember { mutableStateOf(false) }
    var expandedCity by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        jobViewModel.fetchCountries()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Post a Job", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = jobTitle,
            onValueChange = { jobTitle = it },
            label = { Text("Job Title") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = companyName,
            onValueChange = { companyName = it },
            label = { Text("Company Name") },
            modifier = Modifier.fillMaxWidth()
        )

        // Workplace Dropdown
        var expandedWorkplace by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expandedWorkplace,
            onExpandedChange = { expandedWorkplace = !expandedWorkplace }
        ) {
            OutlinedTextField(
                value = workplace,
                onValueChange = {},
                label = { Text("Workplace") },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedWorkplace) },
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenu(
                expanded = expandedWorkplace,
                onDismissRequest = { expandedWorkplace = false }
            ) {
                workplaceOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            workplace = option
                            expandedWorkplace = false
                        }
                    )
                }
            }
        }

        // Employment Type Dropdown
        var expandedEmployment by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expandedEmployment,
            onExpandedChange = { expandedEmployment = !expandedEmployment }
        ) {
            OutlinedTextField(
                value = employmentType,
                onValueChange = {},
                label = { Text("Employment Type") },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedEmployment) },
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenu(
                expanded = expandedEmployment,
                onDismissRequest = { expandedEmployment = false }
            ) {
                employmentOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            employmentType = option
                            expandedEmployment = false
                        }
                    )
                }
            }
        }

        // Currency Dropdown
        var expandedCurrency by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expandedCurrency,
            onExpandedChange = { expandedCurrency = !expandedCurrency }
        ) {
            OutlinedTextField(
                value = currency,
                onValueChange = {},
                label = { Text("Currency") },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedCurrency) },
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenu(
                expanded = expandedCurrency,
                onDismissRequest = { expandedCurrency = false }
            ) {
                currencyOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            currency = option
                            expandedCurrency = false
                        }
                    )
                }
            }
        }

        // Salary Type Dropdown
        var expandedSalary by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expandedSalary,
            onExpandedChange = { expandedSalary = !expandedSalary }
        ) {
            OutlinedTextField(
                value = salaryType,
                onValueChange = {},
                label = { Text("Salary Type") },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedSalary) },
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenu(
                expanded = expandedSalary,
                onDismissRequest = { expandedSalary = false }
            ) {
                salaryTypeOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            salaryType = option
                            expandedSalary = false
                        }
                    )
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = minSalary,
                onValueChange = { minSalary = it },
                label = { Text("Min Salary") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = maxSalary,
                onValueChange = { maxSalary = it },
                label = { Text("Max Salary") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        // Country Dropdown
        ExposedDropdownMenuBox(
            expanded = expandedCountry,
            onExpandedChange = { expandedCountry = !expandedCountry }
        ) {
            OutlinedTextField(
                value = selectedCountry.value.ifEmpty { "Select Country" },
                onValueChange = {},
                label = { Text("Country") },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedCountry) },
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenu(
                expanded = expandedCountry,
                onDismissRequest = { expandedCountry = false }
            ) {
                countries.forEach { country ->
                    DropdownMenuItem(
                        text = { Text(country) },
                        onClick = {
                            selectedCountry.value = country
                            jobViewModel.fetchCities(country)
                            expandedCountry = false
                        }
                    )
                }
            }
        }

        // City Dropdown
        ExposedDropdownMenuBox(
            expanded = expandedCity,
            onExpandedChange = { expandedCity = !expandedCity }
        ) {
            OutlinedTextField(
                value = selectedCity.value.ifEmpty { "Select City" },
                onValueChange = {},
                label = { Text("City") },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedCity) },
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenu(
                expanded = expandedCity,
                onDismissRequest = { expandedCity = false }
            ) {
                cities.forEach { city ->
                    DropdownMenuItem(
                        text = { Text(city) },
                        onClick = {
                            selectedCity.value = city
                            expandedCity = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = jobDescription,
            onValueChange = { jobDescription = it },
            label = { Text("Job Description") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )

        // Image Picker
        Button(onClick = { launcher.launch("image/*") }) {
            Icon(Icons.Default.Upload, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Upload Cover Image")
        }

        selectedImageUri?.let { uri ->
            var bitmap by remember { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }
            LaunchedEffect(uri) {
                withContext(Dispatchers.IO) {
                    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                    val androidBitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
                    bitmap = androidBitmap.asImageBitmap()
                }
            }
            bitmap?.let {
                Image(it, contentDescription = "Job Cover", modifier = Modifier.fillMaxWidth().height(180.dp))
            }
        }

        // Post Job Button
        Button(
            onClick = {
                if (
                    jobTitle.isBlank() || companyName.isBlank() || workplace == "Select" ||
                    employmentType == "Select" || currency == "Select" || salaryType == "Select" ||
                    minSalary.isBlank() || maxSalary.isBlank() ||
                    selectedCountry.value.isBlank() || selectedCity.value.isBlank() ||
                    jobDescription.isBlank() || selectedImageUri == null
                ) {
                    Toast.makeText(context, "Please fill all fields!", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val job = Job(
                    jobId = "",
                    jobTitle = jobTitle,
                    companyName = companyName,
                    workplace = workplace,
                    employmentType = employmentType,
                    currency = currency,
                    salaryType = salaryType,
                    minSalary = minSalary,
                    maxSalary = maxSalary,
                    country = selectedCountry.value,
                    city = selectedCity.value,
                    jobDescription = jobDescription,
                    imageUrl = "",
                    timestamp = 0L
                )

                jobViewModel.postJob(job, selectedImageUri) { success, message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    if (success) navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Post Job")
        }
    }
}
