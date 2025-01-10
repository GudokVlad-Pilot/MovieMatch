package com.example.moviematch.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
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
    val requestsList = authViewModel.requestsList // Observe the friendsList from ViewModel
    val searchQuery = remember { mutableStateOf("") }
    val resultMessage = remember { mutableStateOf("") }
    val isRefreshing = remember { mutableStateOf(false) }
    val friendsLoading = authViewModel.fetchFriendsList() // Needed val to fetch friends before the page is loaded
    val requestsLoading = authViewModel.fetchRequestsList() // Needed val to fetch requests before the page is loaded
    val context = LocalContext.current

    // Fetch usernames when the screen is first composed
    LaunchedEffect(Unit) {
        authViewModel.fetchAllUsernames { fetchedUsernames ->
            val filtered = fetchedUsernames.filter { it != authViewModel.username }
            usernames.value = filtered
        }
        authViewModel.fetchFriendsList()
        authViewModel.fetchRequestsList()
    }

    val userExists = usernames.value.any { it.equals(searchQuery.value, ignoreCase = true) }


    Scaffold(
        topBar = {
            AppBar(
                viewModel = authViewModel,
                onProfileClick = { navController.navigate("profile") },
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
                authViewModel.fetchRequestsList()
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
                                        authViewModel.fetchRequestsList()
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

                    // Requests List
                    // Display the list of requests
                    if (requestsList.isNotEmpty()) {
                        Text(
                            text = "Requests List",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        ) {
                            items(requestsList) { request ->
                                RequestItem(request)
                            }
                        }
                    }

                    // Friends List
                    Text(
                        text = "Friends List",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Display the list of friends
                    if (friendsList.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            items(friendsList) { friend ->
                                FriendItem(friend)
                            }
                        }
                    }
                    else {
                        Button(
                            onClick = {
                                authViewModel.fetchFriendsList()
                                authViewModel.fetchRequestsList()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Update Friends List")
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

@Composable
fun RequestItem(friend: String, authViewModel: AuthViewModel = viewModel()) {

    // State to track if the request is denied
    val requestDenied = remember { mutableStateOf(false) }
    val context = LocalContext.current // Get the context

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
            Spacer(modifier = Modifier.width(16.dp))

            // Check if the request has been denied
            if (!requestDenied.value) {
                // Checkmark button
                IconButton(
                    onClick = {
                        val username1 = authViewModel.username // Current logged-in user's username
                        val username2 = friend // The other user's username

                        // Call the acceptFriendRequest function
                        authViewModel.acceptFriendRequest(username1, username2) { resultMessage ->
                            // Show a Toast message or update UI
                            Toast.makeText(context, resultMessage, Toast.LENGTH_SHORT).show()
                        }
                        authViewModel.fetchFriendsList()
                        authViewModel.fetchRequestsList()
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Check",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Cross button
                IconButton(
                    onClick = {
                        val username1 = authViewModel.username // Current logged-in user's username
                        val username2 = friend // The other user's username

                        // Call the deleteFriendRequest function
                        authViewModel.deleteFriendRequest(username1, username2) { resultMessage ->
                            // Show a Toast message
                            Toast.makeText(context, resultMessage, Toast.LENGTH_SHORT).show()
                        }
                        authViewModel.fetchRequestsList()
                        authViewModel.fetchFriendsList()

                        // Mark the request as denied
                        requestDenied.value = true
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.errorContainer)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cross",
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            } else {
                // Show "request denied" text
                Text(
                    text = "Request denied",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}




