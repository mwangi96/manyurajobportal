package com.example.manyurajobportal.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.manyurajobportal.navigation.Routes
import com.example.manyurajobportal.viewmodel.AuthViewModel

@Composable
fun SignUpScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("alumni") }

    val state by authViewModel.authState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Create Account", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                singleLine = true
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = role == "admin", onClick = { role = "admin" })
                Text("Admin", modifier = Modifier.padding(end = 16.dp))
                RadioButton(selected = role == "alumni", onClick = { role = "alumni" })
                Text("Alumni")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    authViewModel.signUpUser(name, email, password, role) { success ->
                        if (success) {
                            Toast.makeText(context, "Signup successful!", Toast.LENGTH_SHORT).show()
                            navController.navigate(
                                if (role == "admin") Routes.AdminDashboard.route
                                else Routes.AlumniDashboard.route
                            ) {
                                popUpTo(Routes.SignUpScreen.route) { inclusive = true }
                            }
                        } else {
                            Toast.makeText(context, "Signup failed!", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign Up")
            }

            TextButton(onClick = { navController.navigate(Routes.LoginScreen.route) }) {
                Text("Already have an account? Login")
            }

            if (state is AuthViewModel.AuthState.Loading) {
                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
            }

            if (state is AuthViewModel.AuthState.Error) {
                Text(
                    text = (state as AuthViewModel.AuthState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}
