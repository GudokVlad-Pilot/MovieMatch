package com.example.moviematch.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.moviematch.AuthViewModel
import com.example.moviematch.components.AppBar
import com.example.moviematch.movies.MoviesViewModel

@Composable
fun FriendDetailScreen(
    friendsUsername: String,
    navController: NavController,
    viewModel: MoviesViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var friendLikedMovies by remember { mutableStateOf<List<String>>(emptyList()) }
    var friendWatchedMovies by remember { mutableStateOf<List<String>>(emptyList()) }
    var userLikedMovies by remember { mutableStateOf<List<String>>(emptyList()) }
    var userWatchedMovies by remember { mutableStateOf<List<String>>(emptyList()) }
    var commonMovieList by remember { mutableStateOf<List<Pair<String, Boolean>>>(emptyList()) }

    // Function to fetch movies for a given username and status
    fun fetchMovies(username: String, status: String, onResult: (List<String>) -> Unit) {
        authViewModel.getMoviesByStatus(username, status, onResult)
    }

    // Initialize movie fetch when the screen is displayed
    LaunchedEffect(friendsUsername) {
        if (friendsUsername.isNotEmpty()) {
            isLoading = true

            // Fetch movies for the friend
            fetchMovies(friendsUsername, "liked") { friendLiked ->
                friendLikedMovies = friendLiked
                fetchMovies(friendsUsername, "watched") { friendWatched ->
                    friendWatchedMovies = friendWatched

                    // Fetch movies for the current user
                    val currentUser = authViewModel.username
                    fetchMovies(currentUser, "liked") { userLiked ->
                        userLikedMovies = userLiked
                        fetchMovies(currentUser, "watched") { userWatched ->
                            userWatchedMovies = userWatched

                            // Combine and filter common movies
                            val friendMovies = (friendLikedMovies + friendWatchedMovies).toSet()
                            val userMovies = (userLikedMovies + userWatchedMovies).toSet()
                            val commonMovies = friendMovies.intersect(userMovies)

                            // Prepare display list
                            commonMovieList = commonMovies.map { movieId ->
                                val userLiked = userLikedMovies.contains(movieId)
                                val userWatched = userWatchedMovies.contains(movieId)
                                val friendLiked = friendLikedMovies.contains(movieId)
                                val friendWatched = friendWatchedMovies.contains(movieId)

                                // Display as "liked" if one likes and the other watches
                                val isWatched = userWatched && friendWatched // Both watched
                                movieId to isWatched
                            }

                            isLoading = false
                        }
                    }
                }
            }
        } else {
            friendLikedMovies = emptyList()
            friendWatchedMovies = emptyList()
            userLikedMovies = emptyList()
            userWatchedMovies = emptyList()
            commonMovieList = emptyList()
        }
    }

    Scaffold(
        topBar = {
            AppBar(
                viewModel = authViewModel,
                onProfileClick = { navController.navigate("profile") },
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Friend: $friendsUsername",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { showDialog.value = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Delete Friend")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Common Movies",
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(8.dp)
                    )

                    if (isLoading) {
                        CircularProgressIndicator()
                    } else {
                        if (commonMovieList.isEmpty()) {
                            Text("No common movies found.")
                        } else {
                            // Display common movies with watched/liked status
                            LikedWatchedList(commonMovieList, viewModel, navController)
                        }
                    }
                }

                if (showDialog.value) {
                    AlertDialog(
                        onDismissRequest = { showDialog.value = false },
                        title = { Text(text = "Confirm Deletion") },
                        text = {
                            Text(
                                text = "Are you sure you want to delete $friendsUsername from your friends?"
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    val currentUser = authViewModel.username
                                    authViewModel.deleteFriend(currentUser, friendsUsername) { resultMessage ->
                                        Toast.makeText(context, resultMessage, Toast.LENGTH_SHORT).show()
                                    }
                                    showDialog.value = false
                                    navController.navigate("friends")
                                }
                            ) {
                                Text("Yes")
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = { showDialog.value = false }
                            ) {
                                Text("No")
                            }
                        }
                    )
                }
            }
        }
    }
}