package com.example.movie_tracker.ui.viewmodels

import androidx.lifecycle.*
import com.example.movie_tracker.data.model.Movie
import com.example.movie_tracker.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _searchResults = MutableLiveData<List<Movie>>(emptyList())
    val searchResults: LiveData<List<Movie>> = _searchResults

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    val favoriteMovies = repository.getFavoriteMovies().asLiveData()

    val watchedMovies = repository.getWatchedMovies().asLiveData()

    fun searchMovies(query: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val results = repository.searchMovies(query)
                _searchResults.value = results
            } catch (e: Exception) {
                _error.value = "Error al buscar películas: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            repository.updateFavoriteStatus(movie.id, !movie.isFavorite)
        }
    }

    fun toggleWatched(movie: Movie) {
        viewModelScope.launch {
            repository.updateWatchedStatus(movie.id, !movie.isWatched)
        }
    }

    private val _selectedMovie = MutableLiveData<Movie?>(null)
    val selectedMovie: LiveData<Movie?> = _selectedMovie

    fun getMovieDetails(id: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val movie = repository.getMovieById(id)
                _selectedMovie.value = movie
            } catch (e: Exception) {
                _error.value = "Error al obtener detalles: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshMovies() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                repository.refreshMovies()
            } catch (e: Exception) {
                _error.value = "Error al actualizar películas: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadAllMovies() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                repository.refreshMovies()
                repository.getAllMovies().collect { movies ->
                    _searchResults.value = movies
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load movies"
                _isLoading.value = false
            }
        }
    }
}