package com.example.moviematch.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.Modifier
import com.example.moviematch.AuthViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginRegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {
    // State variables
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") } // New field for password confirmation
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var isRegister by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var emptyFields = remember { mutableStateOf(setOf<String>()) } // Track empty fields

    // Helper function to validate if fields are empty and confirm password
    fun validateFields(): Boolean {
        val emptyFieldsList = mutableSetOf<String>()

        if (email.isEmpty()) emptyFieldsList.add("Email")
        if (password.isEmpty()) emptyFieldsList.add("Password")
        if (isRegister) {
            if (name.isEmpty()) emptyFieldsList.add("Name")
            if (surname.isEmpty()) emptyFieldsList.add("Surname")
            if (username.isEmpty()) emptyFieldsList.add("Username")
            if (confirmPassword.isEmpty()) emptyFieldsList.add("ConfirmPassword")
            if (password != confirmPassword) emptyFieldsList.add("PasswordMismatch")
        }

        emptyFields.value = emptyFieldsList
        return emptyFieldsList.isEmpty()
    }

    // Reset empty fields when switching between Register/Login modes
    LaunchedEffect(isRegister) {
        emptyFields.value = setOf() // Reset empty fields whenever the mode changes
    }

    // Get the keyboard controller
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isRegister) {
            // Name
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(text = "Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = if ("Name" in emptyFields.value) Color.Red else Color.Transparent
                ),
                singleLine = true,
                maxLines = 1,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Surname
            TextField(
                value = surname,
                onValueChange = { surname = it },
                label = { Text(text = "Surname") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = if ("Surname" in emptyFields.value) Color.Red else Color.Transparent
                ),
                singleLine = true,
                maxLines = 1,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Username
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(text = "Username") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = if ("Username" in emptyFields.value) Color.Red else Color.Transparent
                ),
                singleLine = true,
                maxLines = 1,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Email
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Email") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = if ("Email" in emptyFields.value) Color.Red else Color.Transparent
            ),
            singleLine = true,
            maxLines = 1,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Password
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = if ("Password" in emptyFields.value) Color.Red else Color.Transparent
            ),
            singleLine = true,
            maxLines = 1,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (isRegister) {
            // Confirm Password
            TextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text(text = "Confirm Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = if ("ConfirmPassword" in emptyFields.value || "PasswordMismatch" in emptyFields.value) Color.Red else Color.Transparent
                ),
                singleLine = true,
                maxLines = 1,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (!isRegister) {
            // Remember Me and Forgot Password
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it }
                    )
                    Text(text = "Remember Me")
                }

                TextButton(onClick = {
                    // Navigate to "Forgot Password" screen or trigger recovery logic
                    navController.navigate("forgotPassword")
                }) {
                    Text(text = "Forgot Password?")
                }
            }
        }

        // Submit Button
        Button(onClick = {
            if (validateFields()) { // Ensure all required fields are filled
                if (isRegister) {
                    displayName = "$name $surname"
                    viewModel.registerUser(email, password, name, surname, username, displayName) { result ->
                        message = result
                    }
                } else {
                    viewModel.loginUser(email, password, rememberMe) { result ->
                        if (result.contains("successful", ignoreCase = true)) {
                            navController.navigate("movies")
                        } else {
                            message = result
                        }
                    }
                }
            }
        }) {
            Text(text = if (isRegister) "Register" else "Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Toggle between Register and Login screens
        TextButton(onClick = {
            isRegister = !isRegister
            emptyFields.value = setOf()
        }) {
            Text(
                text = if (isRegister) "Already have an account? Log in" else "Don't have an account? Register"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display message after submission
        if (message.isNotEmpty()) {
            Text(
                text = message,
                color = if (message.contains("successful", ignoreCase = true))
                    Color.Green
                else Color.Red
            )
        }
    }
}
