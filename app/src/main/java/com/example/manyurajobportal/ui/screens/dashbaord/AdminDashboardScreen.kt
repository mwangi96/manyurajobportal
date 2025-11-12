package com.example.manyurajobportal.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.manyurajobportal.viewmodel.SharedViewModel
import com.example.manyurajobportal.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    var showMenu by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf("Posted Jobs") }

    // ✅ Access logged-in user data from SharedViewModel
    val userName = sharedViewModel.userName.value
    val userEmail = sharedViewModel.userEmail.value
    val userRole = sharedViewModel.userRole.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Admin Dashboard", fontWeight = FontWeight.Bold)
                        Text(
                            text = "Welcome, $userName ($userRole)",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Blog") },
                                onClick = {
                                    showMenu = false
                                    navController.navigate("blog")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Contact Us") },
                                onClick = {
                                    showMenu = false
                                    navController.navigate("contact_us")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Logout") },
                                onClick = {
                                    showMenu = false
                                    showLogoutDialog = true
                                }
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedItem == "Posted Jobs",
                    onClick = {
                        selectedItem = "Posted Jobs"
                        navController.navigate("posted_jobs")
                    },
                    label = { Text("Posted Jobs") },
                    icon = { Icon(Icons.Default.Work, null) }
                )
                NavigationBarItem(
                    selected = selectedItem == "Post Job",
                    onClick = {
                        selectedItem = "Post Job"
                        try {
                            navController.navigate("post_job")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    },
                    label = { Text("Post Job") },
                    icon = { Icon(Icons.Default.PostAdd, null) }
                )
                NavigationBarItem(
                    selected = selectedItem == "Chat",
                    onClick = {
                        selectedItem = "Chat"
                        navController.navigate("chat")
                    },
                    label = { Text("Chat") },
                    icon = { Icon(Icons.Default.Chat, null) }
                )
                NavigationBarItem(
                    selected = selectedItem == "Profile",
                    onClick = {
                        selectedItem = "Profile"
                        navController.navigate("profile")
                    },
                    label = { Text("Profile") },
                    icon = { Icon(Icons.Default.Person, null) }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Welcome, $userName!\nEmail: $userEmail",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Medium)
            )
        }

        // 🔹 Logout Confirmation
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Confirm Logout") },
                text = { Text("Do you really want to log out?") },
                confirmButton = {
                    TextButton(onClick = {
                        showLogoutDialog = false
                        sharedViewModel.clearUserInfo()
                        navController.navigate("login") {
                            popUpTo("admin_dashboard") { inclusive = true }
                        }
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("No")
                    }
                }
            )
        }
    }
}
