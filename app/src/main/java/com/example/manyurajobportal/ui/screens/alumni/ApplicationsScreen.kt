package com.example.manyurajobportal.ui.screens.alumni

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.manyurajobportal.ui.screens.alumni.components.ApplicationItem
import com.example.manyurajobportal.viewmodel.SharedViewModel
import com.example.manyurajobportal.viewmodel.alumni.ApplicationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationsScreen(
    userId: String,
    applicationViewModel: ApplicationViewModel = viewModel()
) {
    val applications by applicationViewModel.applications.collectAsState()
    val loading by applicationViewModel.loading.collectAsState()

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            applicationViewModel.loadUserApplications(userId)
        }
    }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("My Applications") }) }
    ) { padding ->

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                applications.isEmpty() -> Text(
                    text = "You haven't applied for any jobs yet.",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge
                )

                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(applications) { app ->
                        ApplicationItem(app)
                    }
                }
            }
        }
    }
}
