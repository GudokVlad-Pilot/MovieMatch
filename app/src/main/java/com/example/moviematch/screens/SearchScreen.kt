package com.example.moviematch.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun SearchScreen(navController: NavController,
                 viewModel: MoviesViewModel = viewModel(),
                 authViewModel: AuthViewModel = viewModel()
) {
    var query by remember { mutableStateOf("") }
    val movies by viewModel.movies.observeAsState(emptyList())

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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Search TextField
                TextField(
                    value = query,
                    onValueChange = { newQuery ->
                        query = newQuery
                        if (query.isNotEmpty()) {
                            // Call searchMovies when query changes
                            viewModel.searchMovies(query)
                        }
                    },
                    label = { Text("Search for a movie") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Show results in a LazyColumn
                LazyColumn {
                    items(movies) { movie ->
                        MovieItem(movie = movie)
                    }
                }
            }
        }
    }
}

@Composable
fun MovieItem(movie: Movie) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = movie.title, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        // Display poster image if available
        movie.posterPath?.let {
            val imageUrl = "https://image.tmdb.org/t/p/w500$it"
            AsyncImage(
                model = imageUrl,
                contentDescription = movie.title,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = movie.overview, style = MaterialTheme.typography.bodyMedium)
    }
}
