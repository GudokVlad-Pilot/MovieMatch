package com.example.moviematch.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.moviematch.AuthViewModel
import com.example.moviematch.components.AppBar
import com.example.moviematch.components.BottomNavigationBar

@Composable
fun SettingsScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    // State to control which confirmation dialog is shown
    var showDialog by remember { mutableStateOf<Pair<Boolean, String>?>(null) }
    val context = LocalContext.current

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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(text = "Settings Screen", style = MaterialTheme.typography.headlineSmall)

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { showDialog = Pair(true, "liked") },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = "Delete Liked Movies")
                }

                Button(
                    onClick = { showDialog = Pair(true, "watched") },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = "Delete Watched Movies")
                }

                Button(
                    onClick = { showDialog = Pair(true, "disliked") },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = "Delete Disliked Movies")
                }
            }

            // Confirmation Dialog
            showDialog?.let { (isVisible, status) ->
                if (isVisible) {
                    AlertDialog(
                        onDismissRequest = { showDialog = null },
                        title = { Text(text = "Confirm Deletion") },
                        text = { Text(text = "Are you sure you want to delete all $status movies from your collection?") },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showDialog = null
                                    authViewModel.deleteMoviesByStatus(status) { result ->
                                        Log.d("MovieStatusDelete", result)
                                    }
                                    Toast.makeText(context, "Successfully deleted $status movies", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Text("Yes, Delete")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showDialog = null }) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
        }
    }
}
