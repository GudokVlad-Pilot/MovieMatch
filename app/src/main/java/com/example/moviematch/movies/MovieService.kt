package com.example.moviematch.movies

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieService {

    // Fetch popular movies
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String
    ): MovieResponse

    // Fetch details of a specific movie
    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String
    ): MovieDetailsResponse

    // Fetch credits (cast and crew) of a specific movie
    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): CreditsResponse

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("language") language: String
    ): MovieResponse
}


data class MovieResponse(
    val results: List<Movie>
)

data class MovieDetailsResponse(
    val id: Int,
    val title: String,
    val overview: String,
    val release_date: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("vote_average") val rating: Double,
    @SerializedName("production_countries") val countries: List<Country>
)

data class CreditsResponse(
    val cast: List<Actor>,
    val crew: List<CrewMember>
)

data class CrewMember(
    val id: Int,
    val name: String,
    @SerializedName("job") val job: String,
    @SerializedName("profile_path") val profilePath: String?
)

