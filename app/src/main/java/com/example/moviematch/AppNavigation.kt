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
import com.example.moviematch.screens.LoginRegisterScreen
import com.example.moviematch.screens.MessagesScreen
import com.example.moviematch.screens.MoviesScreen
import com.example.moviematch.screens.ProfileScreen
import com.example.moviematch.screens.SearchScreen

@Composable
fun AppNavigation(startDestination: String) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {
        composable("landing") { LandingScreen(navController) }
        composable("login") { LoginRegisterScreen(navController) }
        composable("forgotPassword") { ForgotPasswordScreen(navController) }
        composable("movies") { MoviesScreen(navController) }
        composable("messages") { MessagesScreen(navController) }
        composable("friends") { FriendsScreen(navController) }
        composable("search") { SearchScreen(navController) }
        composable("profile") { ProfileScreen(viewModel = viewModel(), navController = navController) }
        composable(
            "friendDetail/{username}",
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            FriendDetailScreen(username = username, navController = navController)
        }
    }
}


