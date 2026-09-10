import { useState, useEffect, useRef } from 'react';
import { useApp } from '../context/AppContext';
import { getMovies, searchMovies } from '../api/api';

export default function Home() {
  const { filters, setFilters, navigateTo, showToast } = useApp();
  const [movies, setMovies] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [activeSlide, setActiveSlide] = useState(0);
  const [favorites, setFavorites] = useState({});
  const [loading, setLoading] = useState(true);
  const scrollRef = useRef(null);

  useEffect(() => {
    let active = true;
    const hasFilter = Boolean(searchQuery.trim() || filters.language || filters.genre || filters.rating);
    const delay = searchQuery ? 300 : 0;

    const timer = setTimeout(async () => {
      try {
        if (hasFilter) {
          const params = {};
          if (searchQuery.trim()) params.title = searchQuery.trim();
          if (filters.language) params.language = filters.language;
          if (filters.genre) params.genre = filters.genre;
          if (filters.rating) {
            params.certificate = filters.rating;
            params.rating = filters.rating;
          }
          const results = await searchMovies(params);
          if (active) setMovies(results || []);
        } else {
          const all = await getMovies();
          if (active && all) setMovies(all);
        }
      } catch (e) {
        console.error(e);
      } finally {
        if (active) setLoading(false);
      }
    }, delay);

    return () => {
      active = false;
      clearTimeout(timer);
    };
  }, [searchQuery, filters.language, filters.genre, filters.rating]);

  const carouselMovies = movies.slice(0, 5);

  useEffect(() => {
    if (carouselMovies.length === 0) return;
    const timer = setInterval(() => {
      setActiveSlide((prev) => (prev + 1) % carouselMovies.length);
    }, 5000);
    return () => clearInterval(timer);
  }, [carouselMovies.length]);

  const filteredMovies = movies.filter((m) => {
    if (filters.language && (m.language || '').toLowerCase() !== filters.language.toLowerCase()) {
      return false;
    }
    if (filters.genre && !(m.genre || '').toLowerCase().includes(filters.genre.toLowerCase())) {
      return false;
    }
    if (filters.rating && (m.certificate || '').toLowerCase() !== filters.rating.toLowerCase()) {
      return false;
    }
    return true;
  });

  const scroll = (offset) => {
    if (scrollRef.current) {
      scrollRef.current.scrollBy({ left: offset, behavior: 'smooth' });
    }
  };

  const toggleFav = (e, movieId) => {
    e.stopPropagation();
    setFavorites((prev) => {
      const next = !prev[movieId];
      showToast(next ? 'Added to Favorites ❤️' : 'Removed from Favorites', 'info');
      return { ...prev, [movieId]: next };
    });
  };

  const today = new Date().toLocaleDateString('en-US', {
    weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'
  });

  return (
    <div>

      <div className="search-bar">
        <svg className="search-bar__icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <circle cx="11" cy="11" r="8"></circle>
          <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
        </svg>
        <input 
          className="search-bar__input" 
          placeholder="Search Movies, Theatres, Genres..." 
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
        />
      </div>

      <div className="date-display">{today}</div>

      {carouselMovies.length > 0 && (
        <div className="hero-carousel">
          <div 
            className="hero-carousel__track"
            style={{ transform: `translateX(-${activeSlide * 100}%)`, transition: 'transform 0.5s ease-in-out' }}
          >
            {carouselMovies.map((m, idx) => (
              <div 
                key={m.movieId} 
                className={`hero-carousel__slide ${idx === activeSlide ? 'hero-carousel__slide--active' : ''}`}
                onClick={() => navigateTo(`movie/${m.movieId}`)}
                style={{ cursor: 'pointer' }}
              >
                <img className="hero-carousel__img" src={m.poster || ''} alt={m.title} />
                <div className="hero-carousel__overlay">
                  <div className="hero-carousel__tag">UPCOMING FILM</div>
                  <div className="hero-carousel__title">{m.title}</div>
                </div>
              </div>
            ))}
          </div>

          <button 
            className="hero-carousel__arrow hero-carousel__arrow--prev"
            onClick={() => setActiveSlide((prev) => (prev - 1 + carouselMovies.length) % carouselMovies.length)}
          >
            ‹
          </button>
          <button 
            className="hero-carousel__arrow hero-carousel__arrow--next"
            onClick={() => setActiveSlide((prev) => (prev + 1) % carouselMovies.length)}
          >
            ›
          </button>

          <div className="hero-carousel__dots">
            {carouselMovies.map((_, idx) => (
              <div 
                key={idx} 
                className={`hero-carousel__dot ${idx === activeSlide ? 'hero-carousel__dot--active' : ''}`}
                onClick={() => setActiveSlide(idx)}
              />
            ))}
          </div>
        </div>
      )}

      <div className="section-container">
        <div className="section-header">
          <h2 className="section-header__title">NOW SHOWING</h2>
          <div className="section-header__arrows">
            <button className="section-header__arrow scroll-left" onClick={() => scroll(-400)}>‹</button>
            <button className="section-header__arrow scroll-right" onClick={() => scroll(400)}>›</button>
          </div>
        </div>

        <div className="movie-scroll" ref={scrollRef}>
          {loading ? (
            <div className="spinner" style={{ margin: '30px auto' }}></div>
          ) : filteredMovies.length === 0 ? (
            <div style={{ padding: '24px', color: 'var(--text-secondary)' }}>
              <p>No movies match the selected filters.</p>
              {(filters.language || filters.genre || filters.rating) && (
                <button 
                  className="add-btn" 
                  style={{ marginTop: '12px' }}
                  onClick={() => setFilters({ language: '', genre: '', rating: '' })}
                >
                  Clear Filters
                </button>
              )}
            </div>
          ) : (
            filteredMovies.map((m) => (
              <div key={m.movieId} className="movie-card">
                <div className="movie-card__poster-wrap">
                  <img 
                    className="movie-card__poster" 
                    src={m.poster || m.posterUrl || 'https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg'} 
                    alt={m.title} 
                    onError={(e) => { e.target.src = 'https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg'; }}
                  />
                  <div className="movie-card__badge">IMDb</div>
                  <div 
                    className={`movie-card__fav ${favorites[m.movieId] ? 'active' : ''}`}
                    style={{ color: favorites[m.movieId] ? '#00E5FF' : '' }}
                    onClick={(e) => toggleFav(e, m.movieId)}
                    title="Add to Favorites"
                  >
                    ♥
                  </div>
                </div>

                <div className="movie-card__info">
                  <div className="movie-card__title">{m.title}</div>
                  <div className="movie-card__rating">
                    <span className="movie-card__rating-star">★</span>{' '}
                    <span className="movie-card__rating-value">{m.imdbRating ? `${m.imdbRating}/10 IMDb` : 'Not Rated'}</span>
                  </div>
                  <div className="movie-card__genres">{m.genre || 'Action, Drama'}</div>
                  <button 
                    className="movie-card__book-btn"
                    onClick={() => navigateTo(`movie/${m.movieId}`)}
                  >
                    BOOK TICKETS
                  </button>
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
}
