package com.example.moviematch.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.moviematch.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    viewModel: AuthViewModel,
    onProfileClick: () -> Unit,
) {
    // Get the first letter of the username (use username instead of displayName)
    val firstLetter = viewModel.username.firstOrNull()?.uppercase() ?: ""

    TopAppBar(
        title = { Text(text = "MovieMatch") },
        actions = {
            // Profile Circle (if logged in)
            if (viewModel.isLoggedIn) {
                Box(
                    modifier = Modifier
                        .size(40.dp) // Size of the circle
                        .clip(CircleShape) // Clip to circle shape
                        .background(MaterialTheme.colorScheme.primary) // Circle background color
                        .clickable { onProfileClick() }, // Make the circle clickable
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = firstLetter, // Display the first letter of the username
                        color = Color.White, // Text color (white for contrast)
                        style = MaterialTheme.typography.bodyLarge // You can customize the style
                    )
                }
            }
        }
    )
}

