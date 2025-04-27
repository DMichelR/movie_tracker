package com.example.movie_tracker.data.repository

import com.example.movie_tracker.data.local.MovieDao
import com.example.movie_tracker.data.model.*
import com.example.movie_tracker.data.remote.MovieApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val movieApi: MovieApiService,
    private val movieDao: MovieDao
) {
    // Get all movies from local database as Flow
    fun getAllMovies(): Flow<List<Movie>> {
        return movieDao.getAllMovies().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    // Get favorite movies
    fun getFavoriteMovies(): Flow<List<Movie>> {
        return movieDao.getFavoriteMovies().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    // Get watched movies
    fun getWatchedMovies(): Flow<List<Movie>> {
        return movieDao.getWatchedMovies().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    // Get a specific movie by ID
    suspend fun getMovieById(id: Int): Movie? {
        return withContext(Dispatchers.IO) {
            // Try to get from database first
            val localMovie = movieDao.getMovieById(id)
            if (localMovie != null) {
                localMovie.toDomain()
            } else {
                try {
                    // If not in database, fetch from API and save to database
                    val remoteMovie = movieApi.getMovieById(id)
                    movieDao.insertMovie(remoteMovie.toEntity())
                    remoteMovie.toDomain()
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    // Search movies from API
    suspend fun searchMovies(query: String): List<Movie> {
        return withContext(Dispatchers.IO) {
            try {
                val movies = movieApi.searchMovies(query)
                // Save to database
                movieDao.insertAll(movies.map { it.toEntity() })
                movies.map { it.toDomain() }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    // Refresh movies from API
    suspend fun refreshMovies() {
        withContext(Dispatchers.IO) {
            try {
                val movies = movieApi.getMovies()
                val entities = movies.map { dto ->
                    // Preserve favorite and watched status for existing movies
                    val existingMovie = movieDao.getMovieById(dto.id)
                    dto.toEntity().copy(
                        isFavorite = existingMovie?.isFavorite ?: false,
                        isWatched = existingMovie?.isWatched ?: false
                    )
                }
                movieDao.insertAll(entities)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    // Update favorite status
    suspend fun updateFavoriteStatus(id: Int, isFavorite: Boolean) {
        withContext(Dispatchers.IO) {
            movieDao.updateFavoriteStatus(id, isFavorite)
        }
    }

    // Update watched status
    suspend fun updateWatchedStatus(id: Int, isWatched: Boolean) {
        withContext(Dispatchers.IO) {
            movieDao.updateWatchedStatus(id, isWatched)
        }
    }
}