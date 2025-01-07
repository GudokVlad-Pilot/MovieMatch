package com.example.moviematch.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

@Composable
fun BottomNavigationBar(navController: NavController) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        // Movies Tab
        IconButton(onClick = { navController.navigate("movies") }) {
            Icon(Icons.Default.PlayArrow, contentDescription = "Movies", tint = Color.White)
        }

        // Messages Tab
        IconButton(onClick = { navController.navigate("messages") }) {
            Icon(Icons.Default.Email, contentDescription = "Messages", tint = Color.White)
        }

        // Search Tab
        IconButton(onClick = { navController.navigate("search") }) {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
        }

        // Profile Tab
        IconButton(onClick = { navController.navigate("profile") }) {
            Icon(Icons.Default.AccountCircle, contentDescription = "Profile", tint = Color.White)
        }
    }
}
