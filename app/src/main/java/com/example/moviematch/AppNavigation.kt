    package com.example.moviematch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.moviematch.screens.ForgotPasswordScreen
import com.example.moviematch.screens.FriendDetailScreen
import com.example.moviematch.screens.FriendsScreen
import com.example.moviematch.screens.LandingScreen
import com.example.moviematch.screens.LikesScreen
import com.example.moviematch.screens.LoginRegisterScreen
import com.example.moviematch.screens.MovieDetailScreen
import com.example.moviematch.screens.MoviesScreen
import com.example.moviematch.screens.ProfileScreen
import com.example.moviematch.screens.SearchScreen
import com.example.moviematch.screens.SettingsScreen

    @Composable
fun AppNavigation(startDestination: String) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {
        composable("landing") { LandingScreen(navController) }
        composable("login") { LoginRegisterScreen(navController) }
        composable("forgotPassword") { ForgotPasswordScreen(navController) }
        composable("movies") { MoviesScreen(navController) }
        composable("likes") { LikesScreen(navController) }
        composable("friends") { FriendsScreen(navController) }
        composable("search") { SearchScreen(navController) }
        composable("settings") { SettingsScreen(navController) }
        composable("profile") { ProfileScreen(viewModel = viewModel(), navController = navController) }
        composable(
            "friendDetail/{username}",
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            FriendDetailScreen(friendsUsername = username, navController = navController)
        }
        composable("movieDetail/{movieId}") { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId")?.toInt() ?: return@composable
            MovieDetailScreen(movieId = movieId, navController = navController)
        }
    }
}


