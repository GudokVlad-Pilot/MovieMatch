package com.example.moviematch.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween // Ensures even spacing
        ) {
            // Friends Tab
            BottomBarIcon(
                label = "Friends",
                icon = Icons.Default.Face,
                isSelected = currentRoute == "friends",
                onClick = {
                    if (currentRoute != "friends") navController.navigate("friends")
                }
            )

            // Search Tab
            BottomBarIcon(
                label = "Search",
                icon = Icons.Default.Search,
                isSelected = currentRoute == "search",
                onClick = {
                    if (currentRoute != "search") navController.navigate("search")
                }
            )

            // Movies Tab
            BottomBarIcon(
                label = "Movies",
                icon = Icons.Default.Home,
                isSelected = currentRoute == "movies",
                onClick = {
                    if (currentRoute != "movies") navController.navigate("movies")
                }
            )

            // Likes Tab
            BottomBarIcon(
                label = "Likes",
                icon = Icons.Default.Favorite,
                isSelected = currentRoute == "likes",
                onClick = {
                    if (currentRoute != "likes") navController.navigate("likes")
                }
            )

            // Settings Tab
            BottomBarIcon(
                label = "Settings",
                icon = Icons.Default.Settings,
                isSelected = currentRoute == "settings",
                onClick = {
                    if (currentRoute != "settings") navController.navigate("settings")
                }
            )
        }
    }
}

@Composable
fun BottomBarIcon(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        enabled = !isSelected // Disable the button if it's the selected tab
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) MaterialTheme.colorScheme.secondary else Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}



