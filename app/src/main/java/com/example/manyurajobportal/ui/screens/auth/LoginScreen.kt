package com.example.manyurajobportal.ui.screens.auth

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.manyurajobportal.R
import com.example.manyurajobportal.navigation.Routes
import com.example.manyurajobportal.viewmodel.AuthViewModel
import com.example.manyurajobportal.viewmodel.SharedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    sharedViewModel: SharedViewModel // ✅ Added shared view model
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)

    var email by remember { mutableStateOf(sharedPreferences.getString("email", "") ?: "") }
    var password by remember { mutableStateOf(sharedPreferences.getString("password", "") ?: "") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(sharedPreferences.getBoolean("remember_me", false)) }

    val state by authViewModel.authState.collectAsState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

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
                // Logo
                Image(
                    painter = painterResource(id = R.drawable.ist),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(120.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Welcome Back",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Login to continue your journey",
                    color = Color.Gray,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image =
                            if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = image,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // ✅ Remember Me Checkbox
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                    )
                    Text(
                        text = "Remember Me",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ✅ Login Button
                Button(
                    onClick = {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            authViewModel.loginUser(email, password) { success, role ->
                                if (success) {
                                    // ✅ Save credentials if Remember Me is checked
                                    with(sharedPreferences.edit()) {
                                        if (rememberMe) {
                                            putString("email", email)
                                            putString("password", password)
                                            putBoolean("remember_me", true)
                                        } else {
                                            clear()
                                        }
                                        apply()
                                    }

                                    val currentUser = authViewModel.repository.currentUser()
                                    val uid = currentUser?.uid
                                    if (uid != null) {
                                        authViewModel.repository.firestore.collection("users")
                                            .document(uid)
                                            .get()
                                            .addOnSuccessListener { doc ->
                                                val name = doc.getString("name") ?: ""
                                                val emailFetched = doc.getString("email") ?: ""
                                                val roleFetched = doc.getString("role") ?: "alumni"

                                                // ✅ Save user info into SharedViewModel
                                                sharedViewModel.setUserInfo(
                                                    name = name,
                                                    email = emailFetched,
                                                    role = roleFetched
                                                )

                                                Toast.makeText(
                                                    context,
                                                    "Welcome, $name!",
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                                // ✅ Navigate based on role
                                                navController.navigate(
                                                    if (roleFetched == "admin")
                                                        Routes.AdminDashboard.route
                                                    else
                                                        Routes.AlumniDashboard.route
                                                ) {
                                                    popUpTo(Routes.LoginScreen.route) {
                                                        inclusive = true
                                                    }
                                                }
                                            }
                                            .addOnFailureListener {
                                                Toast.makeText(
                                                    context,
                                                    "Failed to fetch user data.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Invalid email or password!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Please fill all fields.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Login")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Navigation options
                TextButton(onClick = { navController.navigate(Routes.SignUpScreen.route) }) {
                    Text("Don’t have an account? Sign up")
                }

                TextButton(onClick = { authViewModel.resetPassword(email) }) {
                    Text("Forgot Password?")
                }

                // Loading state
                if (state is AuthViewModel.AuthState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(top = 24.dp)
                            .size(40.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Error state
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
}
