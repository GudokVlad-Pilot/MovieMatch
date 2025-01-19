package com.example.moviematch.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.moviematch.movies.Movie
import com.example.moviematch.movies.MoviesViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moviematch.AuthViewModel
import com.example.moviematch.components.AppBar


@Composable
fun MovieDetailScreen(
    movieId: Int,
    navController: NavController,
    viewModel: MoviesViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    var movie by remember { mutableStateOf<Movie?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch the movie details by movieId
    LaunchedEffect(movieId) {
        try {
            viewModel.searchMovieById(movieId) { fetchedMovie ->
                movie = fetchedMovie
                isLoading = false
            }
        } catch (e: Exception) {
            errorMessage = "Error fetching movie details: ${e.message}"
            isLoading = false
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
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                errorMessage != null -> {
                    Text(
                        text = errorMessage.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    movie?.let {
                        MovieDetailContent(movie = it)
                    }

                    // Bottom action buttons
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.BottomCenter)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Dislike Button
                            IconButton(
                                onClick = {
                                    movie?.let {
                                        authViewModel.addMovieStatus(it.id.toString(), "disliked") { result ->
                                            Log.d("MovieStatus", result)
                                        }
                                        navController.popBackStack()
                                    }
                                },
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dislike",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            // Watched Button
                            IconButton(
                                onClick = {
                                    movie?.let {
                                        authViewModel.addMovieStatus(it.id.toString(), "watched") { result ->
                                            Log.d("MovieStatus", result)
                                        }
                                        navController.popBackStack()
                                    }
                                },
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = "Watched",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            // Like Button
                            IconButton(
                                onClick = {
                                    movie?.let {
                                        authViewModel.addMovieStatus(it.id.toString(), "liked") { result ->
                                            Log.d("MovieStatus", result)
                                        }
                                        navController.popBackStack()
                                    }
                                },
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Like",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun MovieDetailContent(movie: Movie) {

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        movie.let {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState) // Make the content scrollable
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Movie Title
                Text(text = it.title, style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))

                // Poster Image, stretched from left to right
                val imageUrl = "https://image.tmdb.org/t/p/w500${it.posterPath}"
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth() // Ensures full width
                        .aspectRatio(2f / 3f) // Maintains aspect ratio for movie posters
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Movie Overview
                Text(text = it.overview, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Release date: ${it.release_date.substring(0, 4)}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))

                // Rating
                Text(
                    text = "Rating: ${"%.1f".format(it.rating)} / 10",
                    style = MaterialTheme.typography.bodyMedium
                )

                // Production Countries
                if (it.countries.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Production Countries: ${
                            it.countries.joinToString(", ") { country -> country.name }
                        }",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Cast
                if (it.cast.isNotEmpty()) {
                    Text(
                        text = "Cast: ${
                            it.cast.joinToString(", ") { actor -> actor.name }
                        }",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Producers
                if (it.producers.isNotEmpty()) {
                    Text(
                        text = "Producers: ${
                            it.producers.joinToString(", ")
                        }",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }
}
