package com.example.movie_tracker.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.movie_tracker.R
import com.example.movie_tracker.databinding.FragmentMovieDetailBinding
import com.example.movie_tracker.ui.viewmodels.MovieDetailViewModel
import com.example.movie_tracker.ui.viewmodels.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieDetailFragment : Fragment() {

    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MovieDetailViewModel by viewModels()
    private val args: MovieDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupButtons()
        observeViewModel()
    }

    private fun setupButtons() {
        binding.favoriteButton.setOnClickListener {
            viewModel.toggleFavorite()
        }

        binding.watchedButton.setOnClickListener {
            viewModel.toggleWatched()
        }
    }

    private fun observeViewModel() {
        viewModel.movie.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    // Hide content during loading
                    binding.movieImage.isVisible = false
                    binding.movieTitle.isVisible = false
                    binding.moviePlot.isVisible = false
                    binding.movieYear.isVisible = false
                    binding.favoriteButton.isVisible = false
                    binding.watchedButton.isVisible = false
                }
                is UiState.Success -> {
                    // Show content
                    binding.movieImage.isVisible = true
                    binding.movieTitle.isVisible = true
                    binding.moviePlot.isVisible = true
                    binding.movieYear.isVisible = true
                    binding.favoriteButton.isVisible = true
                    binding.watchedButton.isVisible = true

                    val movie = state.data
                    binding.movieTitle.text = movie.title
                    binding.movieYear.text = movie.year.toString()
                    binding.movieDirector.text = "Director: ${movie.director ?: "No disponible"}"
                    binding.movieGenre.text = "Género: ${movie.genre ?: "No disponible"}"
                    binding.movieRating.text = "Rating: ${movie.rating ?: "N/A"}"
                    binding.moviePlot.text = movie.plot ?: "No hay descripción disponible"

                    // Cargar imagen
                    Glide.with(requireContext())
                        .load(movie.imageUrl)
                        .placeholder(R.drawable.placeholder_movie)
                        .error(R.drawable.error_movie)
                        .centerCrop()
                        .into(binding.movieImage)

                    // Actualizar textos de botones según estado
                    updateButtonTexts(movie.isFavorite, movie.isWatched)
                }
                is UiState.Error -> {
                    // Hide content and show error with Toast
                    binding.movieImage.isVisible = false
                    binding.movieTitle.isVisible = false
                    binding.moviePlot.isVisible = false
                    binding.movieYear.isVisible = false
                    binding.favoriteButton.isVisible = false
                    binding.watchedButton.isVisible = false

                    // Show error message using Toast
                    Toast.makeText(
                        requireContext(),
                        state.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun updateButtonTexts(isFavorite: Boolean, isWatched: Boolean) {
        binding.favoriteButton.text = if (isFavorite) "Quitar de favoritos" else "Añadir a favoritos"
        binding.watchedButton.text = if (isWatched) "Marcar como no vista" else "Marcar como vista"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}