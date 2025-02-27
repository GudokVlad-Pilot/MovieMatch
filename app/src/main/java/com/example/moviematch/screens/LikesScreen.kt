package com.example.moviematch.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.moviematch.AuthViewModel
import com.example.moviematch.components.AppBar
import com.example.moviematch.components.BottomNavigationBar
import com.example.moviematch.movies.Movie
import com.example.moviematch.movies.MoviesViewModel

@Composable
fun LikesScreen(
    navController: NavController,
    viewModel: MoviesViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    var movieList by remember { mutableStateOf<List<Pair<String, Boolean>>>(emptyList()) } // Movie list state with "isWatched" flag
    var isLoading by remember { mutableStateOf(false) } // Loading state

    // Function to fetch movies by status
    fun fetchMovies(username: String) {
        isLoading = true
        authViewModel.getMoviesByStatus(username, "liked") { likedResult ->
            authViewModel.getMoviesByStatus(username, "watched") { watchedResult ->
                // Merge liked and watched movies, tagging watched with `true`
                movieList = likedResult.map { it to false } + watchedResult.map { it to true }
                isLoading = false
            }
        }
    }

    val username = authViewModel.username

    // Initialize movie fetch when the screen is displayed
    LaunchedEffect(username) {
        if (username.isNotEmpty()) {
            fetchMovies(username)
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
                    text = "Movies",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(8.dp)
                )

                // Loading indicator
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    // Movie list
                    LikedWatchedList(movieList, viewModel, navController)
                }
            }
        }
    }
}

@Composable
fun LikedWatchedList(
    movieList: List<Pair<String, Boolean>>,
    viewModel: MoviesViewModel = viewModel(),
    navController: NavController
) {
    if (movieList.isEmpty()) {
        Text(text = "No movies found.", style = MaterialTheme.typography.bodyMedium)
    } else {
        // Use LazyVerticalGrid for a grid layout with 3 columns
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(movieList) { (movieId, isWatched) ->
                // Use a remember to store the fetched movie data locally
                var movie by remember { mutableStateOf<Movie?>(null) }

                LaunchedEffect(movieId) {
                    // Fetch the movie details by ID and update the state
                    viewModel.searchMovieById(movieId.toInt()) { fetchedMovie ->
                        movie = fetchedMovie
                    }
                }

                // Display the movie once it's fetched
                movie?.let {
                    LikedWatchedItem(
                        movie = it,
                        isWatched = isWatched,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun LikedWatchedItem(movie: Movie, isWatched: Boolean, navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable {
                // Navigate to movie details screen
                navController.navigate("movieDetail/${movie.id}")
            },
        contentAlignment = Alignment.TopCenter // Ensures overlay text is positioned at the top center
    ) {
        // Card with movie details
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isWatched) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Display movie poster
                movie.posterPath?.let {
                    val imageUrl = "https://image.tmdb.org/t/p/w500${it}"
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Movie title
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .fillMaxWidth(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Watched overlay text
        if (isWatched) {
            Text(
                text = "Watched",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .align(Alignment.TopCenter)
            )
        }
    }
}



