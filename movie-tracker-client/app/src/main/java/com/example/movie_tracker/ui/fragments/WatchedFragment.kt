package com.example.movie_tracker.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.movie_tracker.databinding.FragmentMovieListBinding
import com.example.movie_tracker.ui.adapters.MovieAdapter
import com.example.movie_tracker.ui.viewmodels.MovieListViewModel
import com.example.movie_tracker.ui.viewmodels.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WatchedFragment : Fragment() {

    private var _binding: FragmentMovieListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MovieListViewModel by viewModels()
    private lateinit var adapter: MovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.titleText.text = "Películas Vistas"

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = MovieAdapter(
            onMovieClick = { movie ->
                findNavController().navigate(
                    WatchedFragmentDirections.actionWatchedFragmentToMovieDetailFragment(movie.id)
                )
            },
            onFavoriteClick = { movie ->
                viewModel.toggleFavorite(movie)
            },
            onWatchedClick = { movie ->
                viewModel.toggleWatched(movie)
            }
        )
        binding.moviesRecyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.watchedMovies.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.moviesRecyclerView.isVisible = false
                }
                is UiState.Success -> {
                    binding.moviesRecyclerView.isVisible = true
                    adapter.submitList(state.data)
                    binding.emptyListText.isVisible = state.data.isEmpty()
                }
                is UiState.Error -> {
                    binding.moviesRecyclerView.isVisible = false
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}