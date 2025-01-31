package com.example.moviematch.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.moviematch.AuthViewModel
import com.example.moviematch.components.AppBar
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.example.moviematch.components.BottomBarIcon
import com.example.moviematch.components.BottomNavigationBar
import com.example.moviematch.movies.Movie
import com.example.moviematch.movies.MoviesViewModel

@Composable
fun MoviesScreen(
    navController: NavController,
    viewModel: MoviesViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val movie by viewModel.movie.observeAsState()
    val scrollState = rememberScrollState() // Remember the scroll state
    var refreshKey by remember { mutableStateOf(0) }

    var movieList by remember { mutableStateOf<List<Pair<String, Boolean>>>(emptyList()) } // Movie list state with "isWatched" flag
    var isLoading by remember { mutableStateOf(false) } // Loading state

    val movieState = remember { MutableTransitionState(movie) }
    movieState.targetState = movie

    // Function to fetch a random movie that is not liked or watched
    fun fetchRandomMovie() {
        val likedMovies = movieList.filter { !it.second }.map { it.first } // Liked but not watched
        val watchedMovies = movieList.filter { it.second }.map { it.first } // Watched
        viewModel.fetchRandomMovie(likedMovies, watchedMovies)
    }

    // Function to fetch movies by status
    fun fetchMovies(username: String) {
        isLoading = true
        authViewModel.getMoviesByStatus(username, "liked") { likedResult ->
            authViewModel.getMoviesByStatus(username, "watched") { watchedResult ->
                // Merge liked and watched movies, tagging watched with true
                movieList = likedResult.map { it to false } + watchedResult.map { it to true }
                isLoading = false
                fetchRandomMovie() // Fetch random movie after the lists are populated
            }
        }
    }

    val username = authViewModel.username

    // Fetch the random movie when the screen is loaded
    LaunchedEffect(username) {
        if (username.isNotEmpty()) {
            fetchMovies(username)
        } else {
            movieList = emptyList()
        }
    }

    LaunchedEffect(refreshKey) {
        scrollState.scrollTo(0)
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
            movie?.let { currentMovie ->
                val swipeThreshold = with(LocalDensity.current) { 150.dp.toPx() }

                var offsetX by remember { mutableStateOf(0f) }
                var isSwiping by remember { mutableStateOf(false) }

                AnimatedContent(
                    targetState = movie,
                    transitionSpec = {
                        slideInVertically { it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
                    },
                    label = "MovieTransition"
                ) { currentMovie ->
                    currentMovie?.let {
                        var offsetX by remember { mutableStateOf(0f) }
                        val animatedOffsetX by animateFloatAsState(
                            targetValue = if (isSwiping) offsetX else 0f,
                            animationSpec = tween(durationMillis = 300)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    detectHorizontalDragGestures(
                                        onDragStart = { isSwiping = true },
                                        onHorizontalDrag = { change, dragAmount ->
                                            offsetX += dragAmount
                                            change.consume()
                                        },
                                        onDragEnd = {
                                            if (offsetX > swipeThreshold) {
                                                // Swiped right (like)
                                                authViewModel.addMovieStatus(it.id.toString(), "liked") { _ -> }
                                                fetchMovies(username)
                                                refreshKey++
                                            } else if (offsetX < -swipeThreshold) {
                                                // Swiped left (dislike)
                                                authViewModel.addMovieStatus(it.id.toString(), "disliked") { _ -> }
                                                fetchMovies(username)
                                                refreshKey++
                                            }
                                            offsetX = 0f
                                            isSwiping = false
                                        }
                                    )
                                }
                                .graphicsLayer {
                                    translationX = animatedOffsetX
                                    rotationZ = animatedOffsetX / 50f
                                }
                        ) {
                            MovieCard(
                                movie = it,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            } ?: run {
                Text(
                    text = "Loading...",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // Buttons for Like, Dislike, and Refresh
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
                            }
                            fetchMovies(username)
                            refreshKey++
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

                    // Refresh Button
                    IconButton(
                        onClick = {
                            fetchMovies(username)
                            refreshKey++
                        },
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
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
                            }
                            fetchMovies(username)
                            refreshKey++
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

@Composable
fun MovieCard(
    movie: Movie, // Replace with your actual Movie data class
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))

            val imageUrl = "https://image.tmdb.org/t/p/w500${movie.posterPath}"
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(text = movie.overview, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Rating: ${"%.1f".format(movie.rating)} / 10",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(64.dp))
        }
    }
}