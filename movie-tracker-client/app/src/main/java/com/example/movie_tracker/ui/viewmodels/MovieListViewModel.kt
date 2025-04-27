package com.example.movie_tracker.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.movie_tracker.data.model.Movie
import com.example.movie_tracker.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = _searchQuery

    private val _searchResults = MutableLiveData<UiState<List<Movie>>>(UiState.Success(emptyList()))
    val searchResults: LiveData<UiState<List<Movie>>> = _searchResults

    private val _isSearching = MutableLiveData(false)
    val isSearching: LiveData<Boolean> = _isSearching

    // Convert Flow to LiveData for favorite movies
    val favoriteMovies: LiveData<UiState<List<Movie>>> = repository.getFavoriteMovies()
        .map { UiState.Success(it) as UiState<List<Movie>> }
        .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
        .asLiveData()

    // Convert Flow to LiveData for watched movies
    val watchedMovies: LiveData<UiState<List<Movie>>> = repository.getWatchedMovies()
        .map { UiState.Success(it) as UiState<List<Movie>> }
        .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
        .asLiveData()

    init {
        refreshMovies()
    }

    // In the MovieListViewModel class

    fun searchMovies(query: String) {
        _searchQuery.value = query

        viewModelScope.launch {
            _isSearching.value = true
            _searchResults.value = UiState.Loading

            try {
                if (query.isBlank()) {
                    // For empty queries, load all movies but don't collect continuously
                    val allMovies = repository.getAllMovies().first()
                    _searchResults.value = UiState.Success(allMovies)
                } else {
                    // For actual search queries, use the search function
                    val results = repository.searchMovies(query)
                    _searchResults.value = UiState.Success(results)
                }
            } catch (e: Exception) {
                _searchResults.value = UiState.Error(e.message ?: "Search failed")
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun loadAllMovies() {
        viewModelScope.launch {
            _searchResults.value = UiState.Loading
            try {
                repository.getAllMovies()
                    .collect { movies ->
                        _searchResults.postValue(UiState.Success(movies))
                    }
            } catch (e: Exception) {
                _searchResults.value = UiState.Error(e.message ?: "Failed to load movies")
            }
        }
    }

    fun refreshMovies() {
        viewModelScope.launch {
            try {
                repository.refreshMovies()
                loadAllMovies()
            } catch (e: Exception) {
                _searchResults.value = UiState.Error(e.message ?: "Failed to refresh movies")
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
}