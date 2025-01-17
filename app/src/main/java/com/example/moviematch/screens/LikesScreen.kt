package com.example.moviematch.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.moviematch.AuthViewModel
import com.example.moviematch.components.AppBar
import com.example.moviematch.components.BottomNavigationBar
import com.example.moviematch.movies.MoviesViewModel

@Composable
fun LikesScreen(
    navController: NavController,
    viewModel: MoviesViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    var movieList by remember { mutableStateOf<List<String>>(emptyList()) } // Movie list state
    var isLoading by remember { mutableStateOf(false) } // Loading state

    // Function to fetch liked movies
    fun fetchLikedMovies(username: String) {
        isLoading = true
        authViewModel.getMoviesByStatus(username, "liked") { result ->
            movieList = result
            isLoading = false
        }
    }

    val username = authViewModel.username

    // Initialize movie fetch when the screen is displayed
    LaunchedEffect(username) {
        if (username.isNotEmpty()) {
            fetchLikedMovies(username)
        } else {
            // Handle the case where the user is not logged in
            movieList = emptyList()
        }
    }
    Scaffold(
        topBar = {
            AppBar(
                onProfileClick = { navController.navigate("profile") },
                viewModel = authViewModel
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title of the screen
                Text(
                    text = "Liked Movies",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(8.dp)
                )

                // Loading indicator
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    // Movie list
                    MovieList(movieList)
                }
            }
        }
    }
}

@Composable
fun MovieList(movieList: List<String>) {
    if (movieList.isEmpty()) {
        Text(text = "No liked movies found.", style = MaterialTheme.typography.bodyMedium)
    } else {
        LazyColumn {
            items(movieList) { movie ->
                Text(
                    text = movie,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}
