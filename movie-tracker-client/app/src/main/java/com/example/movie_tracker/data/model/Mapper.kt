package com.example.movie_tracker.data.model


// Mappers
fun MovieDto.toEntity() = MovieEntity(
    id = id,
    title = title,
    year = year,
    director = director,
    genre = genre,
    rating = rating,
    plot = plot,
    imageUrl = imageUrl,
    isFavorite = false,
    isWatched = false
)

fun MovieEntity.toDomain() = Movie(
    id = id,
    title = title,
    year = year,
    director = director,
    genre = genre,
    rating = rating,
    plot = plot,
    imageUrl = imageUrl,
    isFavorite = isFavorite,
    isWatched = isWatched
)

fun MovieDto.toDomain() = Movie(
    id = id,
    title = title,
    year = year,
    director = director,
    genre = genre,
    rating = rating,
    plot = plot,
    imageUrl = imageUrl,
    isFavorite = false,
    isWatched = false
)

fun Movie.toEntity() = MovieEntity(
    id = id,
    title = title,
    year = year,
    director = director,
    genre = genre,
    rating = rating,
    plot = plot,
    imageUrl = imageUrl,
    isFavorite = isFavorite,
    isWatched = isWatched
)