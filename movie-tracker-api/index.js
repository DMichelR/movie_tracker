const express = require('express');
const cors = require('cors');
const app = express();
const PORT = 3000;

// Middleware
app.use(express.json());
app.use(cors());

// Sample movie data with more details
let movies = [
  { 
    id: 1, 
    title: 'Batman Begins', 
    year: 2005, 
    director: 'Christopher Nolan',
    genre: 'Action, Adventure',
    description: 'After training with his mentor, Batman begins his fight to free crime-ridden Gotham City from corruption.',
    rating: 8.2,
    imageUrl: "https://upload.wikimedia.org/wikipedia/en/8/85/Batman_Begins_Poster.jpg" 
  },
  { 
    id: 2, 
    title: 'The Dark Knight', 
    year: 2008, 
    director: 'Christopher Nolan',
    genre: 'Action, Crime, Drama',
    description: 'When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.',
    rating: 9.0,
    imageUrl: "https://upload.wikimedia.org/wikipedia/en/8/8a/Dark_Knight.jpg" 
  },
  { 
    id: 3, 
    title: 'The Dark Knight Rises', 
    year: 2012, 
    director: 'Christopher Nolan',
    genre: 'Action, Adventure',
    description: 'Eight years after the Joker\'s reign of anarchy, Batman, with the help of the enigmatic Catwoman, is forced from his exile to save Gotham City from the brutal guerrilla terrorist Bane.',
    rating: 8.4,
    imageUrl: "https://upload.wikimedia.org/wikipedia/en/8/83/Dark_knight_rises_poster.jpg"
  },
  {
    id: 4,
    title: 'Inception',
    year: 2010,
    director: 'Christopher Nolan',
    genre: 'Action, Adventure, Sci-Fi',
    description: 'A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.',
    rating: 8.8,
    imageUrl: "https://upload.wikimedia.org/wikipedia/en/2/2e/Inception_%282010%29_theatrical_poster.jpg"
  },
  {
    id: 5,
    title: 'Interstellar',
    year: 2014,
    director: 'Christopher Nolan',
    genre: 'Adventure, Drama, Sci-Fi',
    description: 'A team of explorers travel through a wormhole in space in an attempt to ensure humanity\'s survival.',
    rating: 8.6,
    imageUrl: "https://upload.wikimedia.org/wikipedia/en/b/bc/Interstellar_film_poster.jpg"
  }
];

// Get all movies or search by title
app.get('/movies', (req, res) => {
  const search = req.query.search?.toLowerCase();
  if (search) {
    return res.json(movies.filter(m => 
      m.title.toLowerCase().includes(search)
    ));
  }
  res.json(movies);
});

// Get movie by ID
app.get('/movies/:id', (req, res) => {
  const movie = movies.find(m => m.id == req.params.id);
  if (movie) res.json(movie);
  else res.status(404).send('Movie not found');
});

// Add a new movie
app.post('/movies', (req, res) => {
  const { title, year, director, genre, description, rating, imageUrl } = req.body;
  
  if (!title || !year) {
    return res.status(400).json({ error: 'Title and year are required fields' });
  }
  
  const id = movies.length > 0 ? Math.max(...movies.map(m => m.id)) + 1 : 1;
  const newMovie = { 
    id, 
    title, 
    year,
    director: director || 'Unknown',
    genre: genre || 'Uncategorized',
    description: description || '',
    rating: rating || 0,
    imageUrl: imageUrl || `https://via.placeholder.com/300x450?text=${encodeURIComponent(title)}`
  };
  
  movies.push(newMovie);
  res.status(201).json(newMovie);
});

// Server start
app.listen(PORT, () => {
  console.log(`Movie Tracker API running on http://localhost:${PORT}`);
  console.log('Available endpoints:');
  console.log('  GET  /movies         - Get all movies');
  console.log('  GET  /movies?search=keyword - Search movies by title');
  console.log('  GET  /movies/:id     - Get movie by ID');
  console.log('  POST /movies         - Add a new movie');
});
