// JobCard.kt
package com.example.manyurajobportal.ui.screens.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.manyurajobportal.data.model.Job

@Composable
fun JobCard(
    job: Job,
    isAdmin: Boolean,
    hasApplied: Boolean = false,
    applicantCount: Int = 0,
    onApplyClick: () -> Unit = {},
    onViewApplicantsClick: () -> Unit = {},
    onManageJobClick: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(job.jobTitle, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(6.dp))
            Text(job.companyName, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text("${job.city}, ${job.country}", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(6.dp))
            Text("Salary: ${job.currency} ${job.minSalary} - ${job.maxSalary} (${job.salaryType})", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(10.dp))

            if (isAdmin) {
                Text("Applicants: $applicantCount")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onViewApplicantsClick, modifier = Modifier.fillMaxWidth()) {
                    Text("View Applicants")
                }
                Spacer(modifier = Modifier.height(6.dp))
                Button(onClick = onManageJobClick, modifier = Modifier.fillMaxWidth()) {
                    Text("Manage Job")
                }
            } else {
                Button(
                    onClick = onApplyClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !hasApplied
                ) {
                    Text(if (hasApplied) "Already Applied" else "Apply Now")
                }
            }
        }
    }
}