import { useState, useEffect } from 'react';
import { useApp } from '../context/AppContext';
import { getMovies, getMovieById } from '../api/api';
import MovieCard from '../components/MovieCard';
import { getPosterSrc, handlePosterError } from '../utils/poster';

export default function Home() {
  const { navigateTo } = useApp();
  const [movies, setMovies] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getMovies()
      .then(async (all) => {
        const list = (all || []).slice(0, 5);
        const enriched = await Promise.all(
          list.map(async (m) => {
            if (m.certificate && m.language) return m;
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
      .catch((e) => console.error(e))
      .finally(() => setLoading(false));
  }, []);

  const today = new Date().toLocaleDateString('en-US', {
    weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'
  });

  return (
    <div className="home-page">
      <section className="home-page__welcome">
        <div>
          <div className="home-page__eyebrow">WELCOME TO QUICKSHOW</div>
          <h1>Find your next <span>movie night.</span></h1>
          <p>Book the latest movies at your favourite cinema.</p>
        </div>
        <div className="home-page__date">
          <span>SHOWING TODAY</span>
          <strong>{today}</strong>
        </div>
      </section>

      {!loading && movies.length > 0 && (
        <section className="home-featured">
          <img
            className="home-featured__poster"
            src={getPosterSrc(movies[0].poster || movies[0].posterUrl)}
            alt=""
            onError={handlePosterError}
          />
          <div className="home-featured__content">
            <div className="home-featured__eyebrow">FEATURED THIS WEEK</div>
            <h2>{movies[0].title}</h2>
            <p>{movies[0].description || 'Catch the latest movies on the big screen at a theatre near you.'}</p>
            <div className="home-featured__meta">
              <span>{movies[0].language || 'Multiple languages'}</span>
              <span>{movies[0].genre || 'Now showing'}</span>
              {movies[0].imdbRating && <span>★ {movies[0].imdbRating}/10</span>}
            </div>
            <button
              type="button"
              className="home-featured__button"
              onClick={() => navigateTo(`movie/${movies[0].movieId}`)}
            >
              BOOK TICKETS <span>→</span>
            </button>
          </div>
        </section>
      )}

      <section className="section-container home-page__movies">
        <div className="section-header">
          <div>
            <h2 className="section-header__title">NOW SHOWING</h2>
            <p className="home-page__section-subtitle">Movies playing near you</p>
          </div>
          <button className="add-btn" onClick={() => navigateTo('movies')}>View All</button>
        </div>

        <div className="movie-grid">
          {loading ? (
            <div className="spinner" style={{ margin: '30px auto' }}></div>
          ) : movies.length === 0 ? (
            <div style={{ padding: '24px', color: 'var(--text-secondary)' }}>
              <p>No movies available right now.</p>
            </div>
          ) : (
            movies.map((movie) => <MovieCard key={movie.movieId} movie={movie} />)
          )}
        </div>
      </section>
    </div>
  );
}