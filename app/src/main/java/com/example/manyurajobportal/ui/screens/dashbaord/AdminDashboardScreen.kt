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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.manyurajobportal.ui.screens.admin.PostJobScreen
import com.example.manyurajobportal.ui.screens.admin.PostedJobScreen
//import com.example.manyurajobportal.ui.screens.chat.ChatScreen
//import com.example.manyurajobportal.ui.screens.profile.AdminProfileScreen
import com.example.manyurajobportal.viewmodel.SharedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    var showMenu by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf("Posted Jobs") }

    val userName = sharedViewModel.userName.value
    val userRole = sharedViewModel.userRole.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Admin Dashboard", fontWeight = FontWeight.Bold)
                        Text(
                            text = "Welcome, $userName ($userRole)",
                            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
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

        // Render selected page here
        Box(modifier = Modifier.padding(padding)) {
            when (selectedItem) {

                "Posted Jobs" -> {
                    PostedJobScreen(
                        navController = navController,
                        sharedViewModel = sharedViewModel
                    )
                }


                "Post Job" -> {
                    PostJobScreen(
                        navController = navController,
                        sharedViewModel = sharedViewModel
                    )
                }

//                "Chat" -> {
//                    ChatScreen(navController = navController)
//                }
//
//                "Profile" -> {
//                    AdminProfileScreen(navController = navController)
//                }
            }
        }
    }

    // Logout Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Confirm Logout") },
            text = { Text("Do you really want to log out?") },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
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
                androidx.compose.material3.TextButton(onClick = { showLogoutDialog = false }) {
                    Text("No")
                }
            }
        )
    }
}
