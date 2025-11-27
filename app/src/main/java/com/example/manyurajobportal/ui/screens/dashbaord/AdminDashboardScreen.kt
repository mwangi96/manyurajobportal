package com.example.manyurajobportal.ui.screens.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.manyurajobportal.ui.screens.admin.PostJobScreen
import com.example.manyurajobportal.ui.screens.admin.PostedJobScreen
import com.example.manyurajobportal.utils.SharedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    var showMenu by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf("Posted Jobs") }

    val userName by sharedViewModel.userName.collectAsState()
    val userRole by sharedViewModel.userRole.collectAsState()

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
                    onClick = { selectedItem = "Posted Jobs" },
                    label = { Text("Posted Jobs") },
                    icon = { Icon(Icons.Default.Work, contentDescription = null) }
                )

                NavigationBarItem(
                    selected = selectedItem == "Post Job",
                    onClick = { selectedItem = "Post Job" },
                    label = { Text("Post Job") },
                    icon = { Icon(Icons.Default.PostAdd, contentDescription = null) }
                )

                NavigationBarItem(
                    selected = selectedItem == "Chat",
                    onClick = { selectedItem = "Chat" },
                    label = { Text("Chat") },
                    icon = { Icon(Icons.Default.Chat, contentDescription = null) }
                )

                NavigationBarItem(
                    selected = selectedItem == "Profile",
                    onClick = { selectedItem = "Profile" },
                    label = { Text("Profile") },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) }
                )
            }
        }
    ) { padding ->

        Box(modifier = Modifier.padding(padding)) {

            when (selectedItem) {

                "Posted Jobs" -> PostedJobScreen(
                    navController = navController,
                    sharedViewModel = sharedViewModel
                )

                "Post Job" -> PostJobScreen(
                    navController = navController,
                    sharedViewModel = sharedViewModel
                )

                "Chat" -> {
                    Text(
                        "Chat Screen Placeholder",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                "Profile" -> {
                    Text(
                        "Admin Profile Placeholder",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Confirm Logout") },
            text = { Text("Do you really want to log out?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    sharedViewModel.clearUserSession()
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
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
