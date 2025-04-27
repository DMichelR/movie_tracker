package com.example.movie_tracker.data.model

// Database Entity
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey
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
