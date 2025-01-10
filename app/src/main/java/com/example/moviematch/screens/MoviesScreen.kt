package com.example.moviematch.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.moviematch.AuthViewModel
import com.example.moviematch.components.AppBar
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moviematch.components.BottomNavigationBar

@Composable
fun MoviesScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            AppBar(
                viewModel = viewModel,
                onProfileClick = { navController.navigate("profile") },
                )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Apply padding from Scaffold
        ) {
            Text(text = "Movies")
        }
    }
}

