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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlumniDashboardScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    var showMenu by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf("Home") }

    // ✅ Access logged-in user data
    val userName = sharedViewModel.userName.value
    val userEmail = sharedViewModel.userEmail.value
    val userRole = sharedViewModel.userRole.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Alumni Dashboard", fontWeight = FontWeight.Bold)
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
                    selected = selectedItem == "Home",
                    onClick = {
                        selectedItem = "Home"
                        navController.navigate("home")
                    },
                    label = { Text("Home") },
                    icon = { Icon(Icons.Default.Home, null) }
                )
                NavigationBarItem(
                    selected = selectedItem == "Applications",
                    onClick = {
                        selectedItem = "Applications"
                        navController.navigate("applications")
                    },
                    label = { Text("Applications") },
                    icon = { Icon(Icons.Default.Work, null) }
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
                            popUpTo("alumni_dashboard") { inclusive = true }
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
