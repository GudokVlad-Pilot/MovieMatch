package com.example.moviematch.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.moviematch.AuthViewModel
import com.example.moviematch.components.AppBar
import com.example.moviematch.components.BottomNavigationBar

@Composable
fun FriendsScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    // A state to hold the list of usernames
    val usernames = remember { mutableStateOf<List<String>>(emptyList()) }
    val searchQuery = remember { mutableStateOf("") } // State for the search input
    val resultMessage = remember { mutableStateOf("") } // State to show the result message
    val context = LocalContext.current

    // Fetch the usernames when the Composable is first composed
    LaunchedEffect(Unit) {
        authViewModel.fetchAllUsernames { fetchedUsernames ->
            // Filter out the current user's username if they are in the list
            val filtered = fetchedUsernames.filter { it != authViewModel.username }
            usernames.value = filtered
        }
    }

    // Check if the search query matches any username (case insensitive)
    val userExists = usernames.value.any { it.equals(searchQuery.value, ignoreCase = true) }

    Scaffold(
        topBar = {
            AppBar(
                viewModel = authViewModel,
                onProfileClick = { navController.navigate("profile") },
                onMessagesClick = { navController.navigate("messages") },
                onSearchClick = { navController.navigate("search") }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize().padding(16.dp)
            ) {
                // Add "Your friends" header at the top
                Text(
                    text = "Your friends",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(bottom = 16.dp) // Add space below the header
                )

                // Search box to filter usernames
                TextField(
                    value = searchQuery.value,
                    onValueChange = { searchQuery.value = it },
                    label = { Text("Search for a friend") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Button to check if user exists and add friend
                Button(
                    onClick = {
                        if (userExists) {
                            // Call the addFriend function to add the user to friends
                            authViewModel.addFriend(searchQuery.value) { message ->
                                resultMessage.value = message
                            }
                        } else {
                            resultMessage.value = "User doesn't exist"
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add friend")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Display the result message
                Text(
                    text = resultMessage.value,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}





