package com.example.moviematch.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
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
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState




@Composable
fun FriendsScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    // State holders
    val usernames = remember { mutableStateOf<List<String>>(emptyList()) }
    val friendsList = authViewModel.friendsList // Observe the friendsList from ViewModel
    val searchQuery = remember { mutableStateOf("") }
    val resultMessage = remember { mutableStateOf("") }
    val isRefreshing = remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Fetch usernames when the screen is first composed
    LaunchedEffect(Unit) {
        authViewModel.fetchAllUsernames { fetchedUsernames ->
            val filtered = fetchedUsernames.filter { it != authViewModel.username }
            usernames.value = filtered
        }
    }

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
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = isRefreshing.value),
            onRefresh = {
                isRefreshing.value = true
                authViewModel.fetchFriendsList()
                isRefreshing.value = false
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Header: "Your friends"
                    Text(
                        text = "Your Friends",
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Search bar
                    TextField(
                        value = searchQuery.value,
                        onValueChange = { searchQuery.value = it },
                        label = { Text("Search for a friend") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Add friend button
                    Button(
                        onClick = {
                            if (userExists) {
                                authViewModel.addFriend(searchQuery.value) { message ->
                                    resultMessage.value = message
                                    if (message.contains("success", ignoreCase = true)) {
                                        authViewModel.fetchFriendsList()
                                    }
                                }
                            } else {
                                resultMessage.value = "User doesn't exist"
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add Friend")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Result message
                    if (resultMessage.value.isNotEmpty()) {
                        Text(
                            text = resultMessage.value,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (resultMessage.value.contains("success", ignoreCase = true))
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Friends List
                    Text(
                        text = "Friends List",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Display the list of friends
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(friendsList) { friend ->
                            FriendItem(friend)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FriendItem(friend: String) {
    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Friend Icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = friend,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
        }
    }
}


