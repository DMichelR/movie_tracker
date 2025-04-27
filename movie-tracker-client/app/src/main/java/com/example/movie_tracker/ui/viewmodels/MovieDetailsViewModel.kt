package com.example.movie_tracker.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movie_tracker.data.model.Movie
import com.example.movie_tracker.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val repository: MovieRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val movieId: Int = savedStateHandle.get<Int>("movieId") ?: -1

    private val _movie = MutableLiveData<UiState<Movie>>(UiState.Loading)
    val movie: LiveData<UiState<Movie>> = _movie

    init {
        loadMovie()
    }

    private fun loadMovie() {
        viewModelScope.launch {
            _movie.value = UiState.Loading
            try {
                val result = repository.getMovieById(movieId)
                if (result != null) {
                    _movie.value = UiState.Success(result)
                } else {
                    _movie.value = UiState.Error("Movie not found")
                }
            } catch (e: Exception) {
                _movie.value = UiState.Error(e.message ?: "Failed to load movie")
            }
        }
    }

    fun toggleFavorite() {
        val currentMovie = (_movie.value as? UiState.Success)?.data ?: return
        viewModelScope.launch {
            repository.updateFavoriteStatus(currentMovie.id, !currentMovie.isFavorite)
            loadMovie()
        }
    }

    fun toggleWatched() {
        val currentMovie = (_movie.value as? UiState.Success)?.data ?: return
        viewModelScope.launch {
            repository.updateWatchedStatus(currentMovie.id, !currentMovie.isWatched)
            loadMovie()
        }
    }
}