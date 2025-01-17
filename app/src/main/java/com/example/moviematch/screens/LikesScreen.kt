package com.example.moviematch.screens

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
                    LikedList(movieList, viewModel, navController)
                }
            }
        }
    }
}

@Composable
fun LikedList(movieList: List<String>, viewModel: MoviesViewModel = viewModel(), navController: NavController) {
    if (movieList.isEmpty()) {
        Text(text = "No movies found.", style = MaterialTheme.typography.bodyMedium)
    } else {
        // Use LazyVerticalGrid for a grid layout with 3 columns
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(movieList) { movieId ->
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
                    LikedItem(
                        movie = it,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun LikedItem(movie: Movie, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable {
                // Navigate to movie details screen
                navController.navigate("movieDetail/${movie.id}")
            },
        elevation = CardDefaults.cardElevation(4.dp)
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
}
