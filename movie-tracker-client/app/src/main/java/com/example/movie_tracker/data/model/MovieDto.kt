package com.example.movie_tracker.data.model

// API Model
data class MovieDto(
    val id: Int,
    val title: String,
    val year: Int,
    val director: String?,
    val genre: String?,
    val rating: Double?,
    val plot: String?,
    val imageUrl: String?
)