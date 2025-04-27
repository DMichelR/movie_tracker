package com.example.movie_tracker.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.movie_tracker.databinding.FragmentSearchBinding
import com.example.movie_tracker.ui.adapters.MovieAdapter
import com.example.movie_tracker.ui.viewmodels.MovieListViewModel
import com.example.movie_tracker.ui.viewmodels.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MovieListViewModel by viewModels()
    private lateinit var adapter: MovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchView()
        observeViewModel()

        viewModel.refreshMovies()
    }

    private fun setupRecyclerView() {
        adapter = MovieAdapter(
            onMovieClick = { movie ->
                findNavController().navigate(
                    SearchFragmentDirections.actionSearchFragmentToMovieDetailFragment(movie.id)
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

    private fun setupSearchView() {
        binding.searchButton.setOnClickListener {
            performSearch()
        }

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun performSearch() {
        val query = binding.searchEditText.text.toString().trim()
        viewModel.searchMovies(query)
    }

    private fun observeViewModel() {
        viewModel.searchResults.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.isVisible = true
                    binding.moviesRecyclerView.isVisible = false
                    binding.errorText.isVisible = false
                }
                is UiState.Success -> {
                    binding.progressBar.isVisible = false
                    binding.moviesRecyclerView.isVisible = true
                    binding.errorText.isVisible = false
                    adapter.submitList(state.data)
                    binding.emptyListText.isVisible = state.data.isEmpty()
                }
                is UiState.Error -> {
                    binding.progressBar.isVisible = false
                    binding.errorText.isVisible = true
                    binding.errorText.text = state.message
                }
            }
        }

        viewModel.isSearching.observe(viewLifecycleOwner) { isSearching ->
            binding.progressBar.isVisible = isSearching
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}