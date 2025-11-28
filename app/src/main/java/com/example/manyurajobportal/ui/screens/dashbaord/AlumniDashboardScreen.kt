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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.manyurajobportal.ui.screens.admin.PostedJobScreen
import com.example.manyurajobportal.ui.screens.alumni.ApplicationScreen
import com.example.manyurajobportal.utils.SharedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlumniDashboardScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    var showMenu by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val userId = sharedViewModel.currentUser()

    /* ---------------------------------------------------------
     * AUTO REDIRECT IF USER IS NULL
     * --------------------------------------------------------- */
    LaunchedEffect(userId) {
        if (userId == null) {
            navController.navigate(Routes.LoginScreen.route) {
                popUpTo(0) { inclusive = true }
            }
            return@LaunchedEffect
        }

        sharedViewModel.getUserFirestoreData(userId)
    }

    /* ---------------------------------------------------------
     * USER FIRESTORE DATA
     * --------------------------------------------------------- */
    val userData by sharedViewModel.firestoreData.collectAsState()
    val userName = userData?.get("name")?.toString() ?: "Loading..."
    val userRole = userData?.get("role")?.toString() ?: "alumni"

    /* ---------------------------------------------------------
     * BOTTOM NAV STATE
     * --------------------------------------------------------- */
    var selectedItem by remember { mutableStateOf("Home") }

    LaunchedEffect(Unit) {
        sharedViewModel.fetchApplications(sharedViewModel.currentUserEmail ?: "")
    }


    Scaffold(

        /* --------------------------- TOP BAR --------------------------- */
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Alumni Dashboard", fontWeight = FontWeight.Bold)
                        Text("Welcome, $userName ($userRole)")
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

        /* ----------------------- BOTTOM NAV ----------------------- */
        bottomBar = {
            NavigationBar {

                NavigationBarItem(
                    selected = selectedItem == "Home",
                    onClick = { selectedItem = "Home" },
                    label = { Text("Home") },
                    icon = { Icon(Icons.Default.Home, null) }
                )

                NavigationBarItem(
                    selected = selectedItem == "Applications",
                    onClick = { selectedItem = "Applications" },
                    label = { Text("Applications") },
                    icon = { Icon(Icons.Default.Work, null) }
                )

                NavigationBarItem(
                    selected = selectedItem == "Chat",
                    onClick = { selectedItem = "Chat" },
                    label = { Text("Chat") },
                    icon = { Icon(Icons.Default.Chat, null) }
                )

                NavigationBarItem(
                    selected = selectedItem == "Profile",
                    onClick = { selectedItem = "Profile" },
                    label = { Text("Profile") },
                    icon = { Icon(Icons.Default.Person, null) }
                )
            }
        }
    ) { padding ->

        Box(modifier = Modifier.padding(padding)) {

            when (selectedItem) {
                "Home" ->
                    PostedJobScreen(navController, sharedViewModel)

                "Applications" ->
                    ApplicationScreen(navController, sharedViewModel)

                "Chat" -> {
                    // TODO: Chat screen
                }

                "Profile" -> {
                    // TODO: Profile screen
                }
            }
        }
    }

    /* --------------------------- LOGOUT DIALOG --------------------------- */
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Confirm Logout") },
            text = { Text("Do you really want to log out?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    sharedViewModel.logout()

                    navController.navigate(Routes.LoginScreen.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("No")
                }
            }
        )
    }
}
