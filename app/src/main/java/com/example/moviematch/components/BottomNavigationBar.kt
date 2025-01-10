package com.example.moviematch.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

@Composable
fun BottomNavigationBar(navController: NavController) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween // Ensures even spacing
        ) {
            // Movies Tab
            IconButton(
                onClick = { navController.navigate("movies") },
                modifier = Modifier.weight(1f) // Distribute space evenly
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Movies",
                    tint = Color.White,
                    modifier = Modifier.fillMaxSize() // Adjust icon size
                )
            }

            // Friends Tab
            IconButton(
                onClick = { navController.navigate("friends") },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    Icons.Default.Face,
                    contentDescription = "Friends",
                    tint = Color.White,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Messages Tab
            IconButton(
                onClick = { navController.navigate("messages") },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    Icons.Default.Email,
                    contentDescription = "Messages",
                    tint = Color.White,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Search Tab
            IconButton(
                onClick = { navController.navigate("search") },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.White,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Profile Tab
            IconButton(
                onClick = { navController.navigate("profile") },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = "Profile",
                    tint = Color.White,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

