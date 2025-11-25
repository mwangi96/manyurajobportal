package com.example.manyurajobportal.ui.screens.alumni

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.manyurajobportal.viewmodel.SharedViewModel
import com.example.manyurajobportal.viewmodel.alumni.ApplicationViewModel

@Composable
fun ApplicationsScreen(
    userId: String,
    navController: NavController,
    sharedViewModel: SharedViewModel,
    viewModel: ApplicationViewModel = viewModel()
) {
    val applications by viewModel.applications.collectAsState()
    val loading by viewModel.loading.collectAsState()

    LaunchedEffect(true) { viewModel.loadUserApplications(userId) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Text("My Applications", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))

        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }

        if (applications.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("No applications sent yet")
            }
            return
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(applications) { app ->
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Job: ${app.jobTitle}", style = MaterialTheme.typography.titleMedium)
                        Text("Applied on: ${java.text.SimpleDateFormat("dd MMM yyyy").format(app.timestamp)}")
                        Text("Status: ${app.status.replace("_", " ").capitalize()}")
                    }
                }
            }
        }
    }
}
