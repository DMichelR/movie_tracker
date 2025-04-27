package com.example.movie_tracker.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movie_tracker.R
import com.example.movie_tracker.data.model.Movie

class MovieAdapter(
    private val onMovieClick: (Movie) -> Unit,
    private val onFavoriteClick: (Movie) -> Unit,
    private val onWatchedClick: (Movie) -> Unit
) : ListAdapter<Movie, MovieAdapter.MovieViewHolder>(MovieDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = getItem(position)
        holder.bind(movie)
    }

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.movie_image)
        private val titleTextView: TextView = itemView.findViewById(R.id.movie_title)
        private val yearTextView: TextView = itemView.findViewById(R.id.movie_year)
        private val favoriteIcon: ImageView = itemView.findViewById(R.id.favorite_icon)
        private val watchedIcon: ImageView = itemView.findViewById(R.id.watched_icon)

        fun bind(movie: Movie) {
            titleTextView.text = movie.title
            yearTextView.text = movie.year.toString()

            // Cargar imagen con Glide
            Glide.with(itemView.context)
                .load(movie.imageUrl)
                .placeholder(R.drawable.placeholder_movie)
                .error(R.drawable.error_movie)
                .centerCrop()
                .into(imageView)

            // Configurar íconos de favorito/visto
            favoriteIcon.setImageResource(
                if (movie.isFavorite) R.drawable.ic_favorite
                else R.drawable.ic_favorite_border
            )

            watchedIcon.setImageResource(
                if (movie.isWatched) R.drawable.ic_watched
                else R.drawable.ic_watched_border
            )

            // Configurar listener de clicks
            itemView.setOnClickListener { onMovieClick(movie) }
            favoriteIcon.setOnClickListener { onFavoriteClick(movie) }
            watchedIcon.setOnClickListener { onWatchedClick(movie) }
        }
    }

    // DiffUtil para actualizaciones eficientes
    class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }
}