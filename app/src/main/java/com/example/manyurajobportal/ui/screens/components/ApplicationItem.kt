package com.example.manyurajobportal.ui.screens.alumni.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.manyurajobportal.data.model.ApplicationDisplay

@Composable
fun ApplicationItem(app: ApplicationDisplay) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(app.jobTitle, style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(6.dp))

            Text("Status: ${app.status}")

            Text(
                "Applied: " + java.text.SimpleDateFormat("dd MMM yyyy")
                    .format(java.util.Date(app.timestamp))
            )
        }
    }
}
