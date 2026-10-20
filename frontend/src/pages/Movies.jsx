import { useEffect, useMemo, useState } from 'react';
import { getMovies, getMovieById } from '../api/api';
import MovieCard from '../components/MovieCard';

const initialFilters = { search: '', language: '', genre: '', certificate: '' };

export default function Movies() {
  const [movies, setMovies] = useState([]);
  const [filters, setFilters] = useState(initialFilters);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    setLoading(true);
    getMovies()
      .then(async (all) => {
        const list = Array.isArray(all) ? all : [];
        const enriched = await Promise.all(
          list.map(async (m) => {
            if (m.language && m.certificate) return m;
            try {
              const details = await getMovieById(m.movieId);
              return details ? { ...m, ...details } : m;
            } catch {
              return m;
            }
          })
        );
        setMovies(enriched);
      })
      .catch((requestError) => setError(requestError.message || 'Unable to load movies.'))
      .finally(() => setLoading(false));
  }, []);

  const filteredMovies = useMemo(() => {
    const search = filters.search.trim().toLowerCase();
    return movies.filter((movie) => {
      const matchesSearch = !search
        || [movie.title, movie.genre, movie.language].some((value) => String(value || '').toLowerCase().includes(search));
      const matchesLanguage = !filters.language || String(movie.language || '').trim().toLowerCase() === filters.language.trim().toLowerCase();
      const matchesGenre = !filters.genre || String(movie.genre || '').toLowerCase().includes(filters.genre.toLowerCase());
      const matchesCertificate = !filters.certificate || String(movie.certificate || '').trim().toUpperCase() === filters.certificate.trim().toUpperCase();
      return matchesSearch && matchesLanguage && matchesGenre && matchesCertificate;
    });
  }, [filters, movies]);

  const updateFilter = (name, value) => {
    setFilters((current) => ({ ...current, [name]: value }));
  };

  return (
    <div className="section-container movies-page">
      <div className="section-header">
        <div>
          <h2 className="section-header__title">ALL MOVIES</h2>
          <p className="movies-page__subtitle">Find your next movie night.</p>
        </div>
        <span className="movies-page__count">{filteredMovies.length} movies</span>
      </div>

      <div className="movies-page__filters">
        <input
          type="search"
          placeholder="Search movies..."
          value={filters.search}
          onChange={(event) => updateFilter('search', event.target.value)}
          aria-label="Search movies"
        />
        <select value={filters.language} onChange={(event) => updateFilter('language', event.target.value)} aria-label="Filter by language">
          <option value="">All languages</option>
          <option value="English">English</option>
          <option value="Hindi">Hindi</option>
          <option value="Telugu">Telugu</option>
          <option value="Tamil">Tamil</option>
          <option value="Kannada">Kannada</option>
          <option value="Malayalam">Malayalam</option>
        </select>
        <select value={filters.genre} onChange={(event) => updateFilter('genre', event.target.value)} aria-label="Filter by genre">
          <option value="">All genres</option>
          <option value="Action">Action</option>
          <option value="Sci-Fi">Sci-Fi</option>
          <option value="Drama">Drama</option>
          <option value="Comedy">Comedy</option>
          <option value="Thriller">Thriller</option>
          <option value="Adventure">Adventure</option>
          <option value="Animation">Animation</option>
          <option value="Horror">Horror</option>
          <option value="Romance">Romance</option>
        </select>
        <select value={filters.certificate} onChange={(event) => updateFilter('certificate', event.target.value)} aria-label="Filter by certificate">
          <option value="">All ratings</option>
          <option value="U">U - Universal</option>
          <option value="UA_7_PLUS">UA 7+</option>
          <option value="UA_13_PLUS">UA 13+</option>
          <option value="UA_16_PLUS">UA 16+</option>
          <option value="A">A - Adults Only</option>
        </select>
        <button type="button" className="movies-page__clear-btn" onClick={() => setFilters(initialFilters)}>
          Clear
        </button>
      </div>

      {loading ? (
        <div className="spinner" />
      ) : error ? (
        <div className="empty-state"><p className="empty-state__text">{error}</p></div>
      ) : filteredMovies.length === 0 ? (
        <div className="empty-state"><p className="empty-state__text">No movies match your search or filters.</p></div>
      ) : (
        <div className="movie-grid">
          {filteredMovies.map((movie) => <MovieCard key={movie.movieId} movie={movie} />)}
        </div>
      )}
    </div>
  );
}
