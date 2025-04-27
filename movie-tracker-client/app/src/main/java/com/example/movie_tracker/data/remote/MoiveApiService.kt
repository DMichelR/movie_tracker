package com.example.movie_tracker.data.remote

import com.example.movie_tracker.data.model.MovieDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiService {
    @GET("movies")
    suspend fun getMovies(): List<MovieDto>

    @GET("movies")
    suspend fun searchMovies(@Query("search") searchQuery: String): List<MovieDto>

    @GET("movies/{id}")
    suspend fun getMovieById(@Path("id") id: Int): MovieDto
}