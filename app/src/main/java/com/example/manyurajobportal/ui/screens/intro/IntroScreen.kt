package com.example.manyurajobportal.ui.screens.intro

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.manyurajobportal.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntroScreen(navController: NavController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val loadingMessages = listOf(
        "Loading resources...",
        "Preparing dashboard...",
        "Fetching jobs...",
        "Almost there..."
    )

    var currentMessageIndex by remember { mutableStateOf(0) }

    // Change loading message every 1.5 seconds
    LaunchedEffect(Unit) {
        while (true) {
            delay(1500)
            currentMessageIndex = (currentMessageIndex + 1) % loadingMessages.size
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Manyura Job Portal",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
                Divider()
                DrawerItem("About IST") { /* TODO */ }
                DrawerItem("Contact Us") { /* TODO */ }
                DrawerItem("Privacy Policy") { /* TODO */ }
            }
        }
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Static Logo (no animation)
                Image(
                    painter = painterResource(id = R.drawable.ist),
                    contentDescription = "IST Logo",
                    modifier = Modifier.size(160.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Manyura Job Portal",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = loadingMessages[currentMessageIndex],
                    fontSize = 18.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))

                Button(
                    onClick = { navController.navigate(Routes.LoginScreen.route) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Continue")
                }
            }
        }
    }
}

@Composable
fun DrawerItem(title: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
