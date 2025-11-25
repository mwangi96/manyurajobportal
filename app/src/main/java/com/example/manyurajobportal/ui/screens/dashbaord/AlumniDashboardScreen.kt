package com.example.manyurajobportal.ui.screens.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.manyurajobportal.ui.screens.admin.PostedJobScreen
import com.example.manyurajobportal.ui.screens.alumni.AlumniProfileScreen
import com.example.manyurajobportal.ui.screens.alumni.ApplicationsScreen
import com.example.manyurajobportal.viewmodel.SharedViewModel
import com.example.manyurajobportal.viewmodel.alumni.AlumniJobViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlumniDashboardScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    var showMenu by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
//    var selectedItem by remember { mutableStateOf("Home") }


    val backStackEntry = navController.currentBackStackEntryAsState().value
    val tab = backStackEntry?.arguments?.getString("tab") ?: "Home"

    var selectedItem by remember { mutableStateOf("Home") }

    LaunchedEffect(tab) {
        selectedItem = tab
    }

    val userName = sharedViewModel.userName.value
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
                    onClick = { selectedItem = "Home" },
                    label = { Text("Home") },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) }
                )
                NavigationBarItem(
                    selected = selectedItem == "Applications",
                    onClick = { selectedItem = "Applications" },
                    label = { Text("Applications") },
                    icon = { Icon(Icons.Default.Work, contentDescription = null) }
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

                "Home" -> {
                    PostedJobScreen(
                        navController = navController,
                        sharedViewModel = sharedViewModel   // ⭐ FIXED
                    )
                }

                "Applications" -> {
                    ApplicationsScreen(
                        navController = navController,
                        userId = sharedViewModel.userId.value ?: "",
                        sharedViewModel = sharedViewModel
                    )
                }


//                "Chat" -> {
//                    ChatScreen(
//                        navController = navController,
//                        sharedViewModel = sharedViewModel     // ⭐ USE SAME VIEWMODEL
//                    )
//                }

                "Profile" -> {
                    AlumniProfileScreen(
                        navController = navController,
                        sharedViewModel = sharedViewModel     // ⭐ KEEP USER DATA
                    )
                }
            }
        }
    }

    // 🔹 Logout Dialog
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
