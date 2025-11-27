package com.example.manyurajobportal.ui.screens.alumni

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.manyurajobportal.screens.ApplicationData
import com.example.manyurajobportal.utils.SharedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    val applications by sharedViewModel.applications.collectAsState()
    val loading by sharedViewModel.loading.collectAsState()
    val error by sharedViewModel.errorMessage.collectAsState()

    val jobId = navController.currentBackStackEntry?.arguments?.getString("jobId") ?: ""

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Applications") }
            )
        }
    ) { padding ->
        Box(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .padding(16.dp)) {

            when {
                loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                error != null -> {
                    Text(
                        text = error ?: "Something went wrong",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                applications.isEmpty() -> {
                    Text(
                        text = "You haven't applied for any jobs yet.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(applications) { application ->
                            ApplicationItemCard(application)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ApplicationItemCard(application: ApplicationData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = application.jobTitle, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(text = application.companyName, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))

            Text(
                text = "Status: ${application.status}",
                style = MaterialTheme.typography.bodyMedium,
                color = when (application.status.lowercase()) {
                    "approved" -> MaterialTheme.colorScheme.primary
                    "denied" -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.secondary
                }
            )
        }
    }
}
