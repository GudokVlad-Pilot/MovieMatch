package com.example.moviematch

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel


class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val usersCollection: CollectionReference = firestore.collection("users")

    private val sharedPreferences = application.getSharedPreferences("MovieMatchPrefs", Context.MODE_PRIVATE)

    // Reactive state for user data
    var displayName by mutableStateOf("")
        private set
    var username by mutableStateOf("")
        private set
    var email by mutableStateOf("")
        private set
    var isLoggedIn by mutableStateOf(false)
        private set
    var name by mutableStateOf("")
        private set
    var surname by mutableStateOf("")
        private set

    // Reactive state for friends list
    var friendsList by mutableStateOf<List<String>>(emptyList())
        private set

    // Reactive state for friends list
    var requestsList by mutableStateOf<List<String>>(emptyList())
        private set

    // Initialize with current user details
    init {
        updateUserState()
    }

    fun setRememberMe(value: Boolean) {
        sharedPreferences.edit().putBoolean("REMEMBER_ME", value).apply()
    }

    private fun getRememberMe(): Boolean {
        return sharedPreferences.getBoolean("REMEMBER_ME", false)
    }

    /**
     * Registers a new user with the provided details.
     * Updates the user's profile with name, surname, and saves the username, display name, and email to Firestore.
     */
    fun registerUser(
        email: String,
        password: String,
        name: String,
        surname: String,
        username: String,
        displayName: String,
        onResult: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser

                    // Update the user's Firebase Authentication profile with display name
                    val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName)
                        .build()

                    currentUser?.updateProfile(profileUpdates)?.addOnCompleteListener { profileTask ->
                        if (profileTask.isSuccessful) {
                            // Save user details to Firestore
                            val userId = currentUser.uid
                            val userData = mapOf(
                                "name" to name,
                                "surname" to surname,
                                "username" to username,
                                "displayName" to displayName,
                                "email" to email
                            )
                            usersCollection.document(userId).set(userData)
                                .addOnCompleteListener { firestoreTask ->
                                    if (firestoreTask.isSuccessful) {
                                        // Send verification email
                                        currentUser.sendEmailVerification()
                                            .addOnCompleteListener { emailTask ->
                                                if (emailTask.isSuccessful) {
                                                    onResult("Registration successful. Please verify your email before logging in.")
                                                } else {
                                                    onResult("Failed to send verification email. ${emailTask.exception?.message}")
                                                }
                                            }
                                    } else {
                                        onResult("Failed to save user data to Firestore: ${firestoreTask.exception?.message}")
                                    }
                                }
                        } else {
                            onResult("Failed to update profile: ${profileTask.exception?.message}")
                        }
                    }
                } else {
                    onResult("Registration failed: ${task.exception?.message}")
                }
            }
    }

    /**
     * Logs in a user with the provided email and password.
     */
    fun loginUser(email: String, password: String, rememberMe: Boolean, onResult: (String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user?.isEmailVerified == true) {
                        setRememberMe(rememberMe)
                        onResult("Login successful.")
                    } else {
                        auth.signOut()
                        onResult("Please verify your email before logging in.")
                    }
                } else {
                    onResult("Login failed: ${task.exception?.message}")
                }
            }
    }

    /**
     * Logs out the currently logged-in user.
     */
    fun logout() {
        auth.signOut()
        setRememberMe(false) // Clear "Remember Me" state
        clearUserState()
    }

    /**
     * Updates the ViewModel's state based on the currently logged-in user.
     */
    private fun updateUserState() {
        val user = auth.currentUser
        isLoggedIn = user != null
        if (user != null) {
            displayName = user.displayName ?: "User"
            fetchUserDataFromFirestore(user.uid)
            fetchFriendsList() // Fetch the friends list when the user state is updated
        } else {
            clearUserState()
        }
    }

    /**
     * Fetches the username, display name, and email from Firestore.
     */
    private fun fetchUserDataFromFirestore(userId: String) {
        usersCollection.document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    username = document.getString("username") ?: ""
                    displayName = document.getString("displayName") ?: ""
                    email = document.getString("email") ?: ""
                    name = document.getString("name") ?: ""
                    surname = document.getString("surname") ?: ""
                }
            }
            .addOnFailureListener { exception ->
                Log.e("AuthViewModel", "Failed to fetch user data: ${exception.message}")
            }
    }

    /**
     * Fetches the friends list from Firestore and updates the state.
     */
    fun fetchFriendsList() {
        getFriends { friends ->
            friendsList = friends // Update the mutable state with the fetched friends
        }
    }

    /**
     * Fetches the requests list from Firestore and updates the state.
     */
    fun fetchRequestsList() {
        getRequests { requests ->
            requestsList = requests // Update the mutable state with the fetched friends
        }
    }

    /**
     * Clears the ViewModel's state when the user logs out.
     */
    private fun clearUserState() {
        displayName = ""
        username = ""
        email = ""
        name = ""
        surname = ""
        friendsList = emptyList() // Clear the friends list
        isLoggedIn = false
    }

    /**
     * Retrieves the currently logged-in user.
     */
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    /**
     * Checks if the user should stay logged in based on "Remember Me."
     */
    fun shouldStayLoggedIn(): Boolean {
        return getRememberMe() && auth.currentUser != null
    }

    /**
     * Retrieves all registered users' usernames from Firestore.
     */
    fun fetchAllUsernames(onResult: (List<String>) -> Unit) {
        usersCollection.get()
            .addOnSuccessListener { querySnapshot ->
                val usernames = mutableListOf<String>()
                for (document in querySnapshot.documents) {
                    val username = document.getString("username")
                    if (!username.isNullOrEmpty()) {
                        usernames.add(username)
                    }
                }
                onResult(usernames) // Return the list of usernames
            }
            .addOnFailureListener { exception ->
                Log.e("AuthViewModel", "Failed to fetch usernames: ${exception.message}")
                onResult(emptyList()) // Return an empty list in case of failure
            }
    }

    fun addFriend(username2: String, onResult: (String) -> Unit) {
        // Check if the user is logged in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val username1 = this.username // Current user's username

            // Create a document name as a combination of both usernames (order doesn't matter)
            val friendDocumentName = if (username1 < username2) {
                "$username1-$username2"
            } else {
                "$username2-$username1"
            }

            // Create the document data
            val friendData = hashMapOf(
                "username1" to username1,
                "username2" to username2,
                "status" to "pending"
            )

            // Add the friend document to Firestore under "friends" collection
            val friendsCollection = firestore.collection("friends")

            // Check if the document already exists to avoid duplicates
            friendsCollection.document(friendDocumentName).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // Document already exists
                        onResult("This friendship already exists.")
                    } else {
                        // Document doesn't exist, so create it
                        friendsCollection.document(friendDocumentName)
                            .set(friendData)
                            .addOnSuccessListener {
                                onResult("Friendship request sent successfully.")
                            }
                            .addOnFailureListener { exception ->
                                onResult("Failed to add friend: ${exception.message}")
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    onResult("Error checking friendship existence: ${exception.message}")
                }
        } else {
            onResult("User is not logged in.")
        }
    }


    /**
     * Retrieves the list of friends from Firestore.
     */
    fun getFriends(onResult: (List<String>) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val username1 = this.username
            val friendsCollection = firestore.collection("friends")
            val friends = mutableListOf<String>()

            friendsCollection.whereEqualTo("username1", username1)
                .get()
                .addOnSuccessListener { querySnapshot1 ->
                    querySnapshot1.documents.forEach { document ->
                        val username2 = document.getString("username2")
                        val status = document.getString("status")
                        if (status == "accepted" && !username2.isNullOrEmpty()) {
                            friends.add(username2)
                        }
                    }

                    friendsCollection.whereEqualTo("username2", username1)
                        .get()
                        .addOnSuccessListener { querySnapshot2 ->
                            querySnapshot2.documents.forEach { document ->
                                val username1 = document.getString("username1")
                                val status = document.getString("status")
                                if (status == "accepted" && !username1.isNullOrEmpty()) {
                                    friends.add(username1)
                                }
                            }

                            onResult(friends) // Return the combined friends list
                        }
                        .addOnFailureListener { exception ->
                            Log.e("AuthViewModel", "Error fetching friends: ${exception.message}")
                            onResult(emptyList())
                        }
                }
                .addOnFailureListener { exception ->
                    Log.e("AuthViewModel", "Error fetching friends: ${exception.message}")
                    onResult(emptyList())
                }
        } else {
            onResult(emptyList())
        }
    }

    /**
     * Retrieves the list of friends from Firestore.
     */
    fun getRequests(onResult: (List<String>) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val username2 = this.username // Assume `this.username` is the current user's username
            val friendsCollection = firestore.collection("friends")
            val requests = mutableListOf<String>()

            // Fetch requests where the current user is "username2"
            friendsCollection.whereEqualTo("username2", username2)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    querySnapshot.documents.forEach { document ->
                        val username1 = document.getString("username1")
                        val status = document.getString("status")
                        if (status == "pending" && !username1.isNullOrEmpty()) {
                            requests.add(username1) // Add the requesting user's username
                        }
                    }

                    onResult(requests) // Return the filtered requests list
                }
                .addOnFailureListener { exception ->
                    Log.e("AuthViewModel", "Error fetching requests: ${exception.message}")
                    onResult(emptyList()) // Return an empty list on failure
                }
        } else {
            onResult(emptyList()) // Return an empty list if the user is not logged in
        }
    }


    fun acceptFriendRequest(username1: String, username2: String, onResult: (String) -> Unit) {
        val friendDocumentName = if (username1 < username2) {
            "$username1-$username2"
        } else {
            "$username2-$username1"
        }

        val friendsCollection = firestore.collection("friends")

        // Check if the document exists
        friendsCollection.document(friendDocumentName).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val currentStatus = documentSnapshot.getString("status")
                    if (currentStatus == "pending") {
                        // Change the status to 'accepted'
                        friendsCollection.document(friendDocumentName)
                            .update("status", "accepted")
                            .addOnSuccessListener {
                                onResult("Friend request accepted.")
                            }
                            .addOnFailureListener { exception ->
                                onResult("Failed to accept friend request: ${exception.message}")
                            }
                    } else {
                        onResult("The status is not pending. Cannot accept.")
                    }
                } else {
                    onResult("Friend request not found.")
                }
            }
            .addOnFailureListener { exception ->
                onResult("Error checking friend request: ${exception.message}")
            }
    }

    fun deleteFriendRequest(username1: String, username2: String, onResult: (String) -> Unit) {
        val friendDocumentName = if (username1 < username2) {
            "$username1-$username2"
        } else {
            "$username2-$username1"
        }

        val friendsCollection = firestore.collection("friends")

        // Check if the document exists
        friendsCollection.document(friendDocumentName).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Delete the document
                    friendsCollection.document(friendDocumentName)
                        .delete()
                        .addOnSuccessListener {
                            onResult("Friend request deleted.")
                        }
                        .addOnFailureListener { exception ->
                            onResult("Failed to delete friend request: ${exception.message}")
                        }
                } else {
                    onResult("Friend request not found.")
                }
            }
            .addOnFailureListener { exception ->
                onResult("Error checking friend request: ${exception.message}")
            }
    }

}
