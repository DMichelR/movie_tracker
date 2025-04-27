package com.example.movie_tracker.data.model


// Domain Model
data class Movie(
    val id: Int,
    val title: String,
    val year: Int,
    val director: String?,
    val genre: String?,
    val rating: Double?,
    val plot: String?,
    val imageUrl: String?,
    val isFavorite: Boolean = false,
    val isWatched: Boolean = false
)