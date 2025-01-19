package com.example.moviematch.movies

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale
import com.example.moviematch.BuildConfig

class MoviesViewModel : ViewModel() {

    private val _movie = MutableLiveData<Movie>()
    private val _movies = MutableLiveData<List<Movie>>()
    val movie: LiveData<Movie> get() = _movie
    val movies: LiveData<List<Movie>> get() = _movies

    private val apiKey = BuildConfig.API_KEY
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val service = retrofit.create(MovieService::class.java)

    fun fetchRandomMovie() {
        viewModelScope.launch {
            try {
                // Get the user's default language (e.g., "en" for English, "fr" for French)
                val language = Locale.getDefault().language

                // Fetch popular movies in the user's language
                val response = service.getPopularMovies(apiKey, language)

                // Filter movies with valid poster paths
                val validMovies = response.results.filter { !it.posterPath.isNullOrEmpty() }
                if (validMovies.isNotEmpty()) {
                    // Pick a random movie
                    val randomMovie = validMovies.random()

                    // Fetch additional details for the selected movie
                    val movieDetails = service.getMovieDetails(randomMovie.id, apiKey, language)
                    val movieCredits = service.getMovieCredits(randomMovie.id, apiKey)

                    // Map data to the Movie class
                    val enrichedMovie = Movie(
                        id = movieDetails.id,
                        title = movieDetails.title,
                        overview = movieDetails.overview,
                        release_date = movieDetails.release_date,
                        posterPath = movieDetails.posterPath,
                        rating = movieDetails.rating,
                        countries = movieDetails.countries, // Already a list of Country objects
                        cast = movieCredits.cast.take(10).map { actor -> // Top 10 actors
                            Actor(
                                id = actor.id,
                                name = actor.name,
                                profilePath = actor.profilePath
                            )
                        },
                        producers = movieCredits.crew.filter { it.job == "Producer" }
                            .map { it.name }
                    )

                    _movie.postValue(enrichedMovie)
                } else {
                    Log.e("MoviesViewModel", "No valid movies with poster paths found.")
                }
            } catch (e: Exception) {
                // Handle errors
                Log.e("MoviesViewModel", "Error fetching movie: ${e.message}")
            }
        }
    }

    fun searchMovies(query: String) {
        viewModelScope.launch {
            try {
                val language = Locale.getDefault().language
                val response = service.searchMovies(apiKey, query, language)

                // Process the search results
                val moviesList = response.results.filter { !it.posterPath.isNullOrEmpty() }
                _movies.postValue(moviesList)
            } catch (e: Exception) {
                Log.e("MoviesViewModel", "Error searching for movies: ${e.message}")
            }
        }
    }

    fun searchMovieById(movieId: Int, callback: (Movie) -> Unit) {
        viewModelScope.launch {
            try {
                val language = Locale.getDefault().language

                // Fetch movie details by ID
                val movieDetails = service.getMovieDetails(movieId, apiKey, language)
                val movieCredits = service.getMovieCredits(movieId, apiKey)

                // Map data to the Movie class
                val enrichedMovie = Movie(
                    id = movieDetails.id,
                    title = movieDetails.title,
                    overview = movieDetails.overview,
                    release_date = movieDetails.release_date,
                    posterPath = movieDetails.posterPath,
                    rating = movieDetails.rating,
                    countries = movieDetails.countries, // Already a list of Country objects
                    cast = movieCredits.cast.take(10).map { actor ->
                        Actor(
                            id = actor.id,
                            name = actor.name,
                            profilePath = actor.profilePath
                        )
                    },
                    producers = movieCredits.crew.filter { it.job == "Producer" }.map { it.name }
                )

                // Callback with the enriched movie
                callback(enrichedMovie)

            } catch (e: Exception) {
                Log.e("MoviesViewModel", "Error fetching movie by ID: ${e.message}")
            }
        }
    }

}




