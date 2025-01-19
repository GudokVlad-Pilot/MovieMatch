package com.example.moviematch.movies

import com.google.gson.annotations.SerializedName

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val release_date: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("vote_average") val rating: Double, // TMDB rating
    @SerializedName("production_countries") val countries: List<Country> = emptyList(), // Production countries
    val cast: List<Actor> = emptyList(), // Custom field for cast
    val producers: List<String> = emptyList() // Custom field for producers
)

data class Country(
    @SerializedName("iso_3166_1") val code: String,
    val name: String
)

data class Actor(
    val id: Int,
    val name: String,
    @SerializedName("profile_path") val profilePath: String? // Actor's profile image
)

