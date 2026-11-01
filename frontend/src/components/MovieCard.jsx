
import { useApp } from '../context/AppContext';
import { getPosterSrc, handlePosterError } from '../utils/poster';

const formatCertificate = (cert) => {
  if (!cert) return '';
  return cert
    .replace('UA_16_PLUS', 'UA 16+')
    .replace('UA_7_PLUS', 'UA 7+')
    .replace('UA_13_PLUS', 'UA 13+')
    .replace('UA_18_PLUS', 'A')
    .replace('U_A', 'UA')
    .replace(/_/g, ' ');
};

export default function MovieCard({ movie }) {
  const { navigateTo } = useApp();

  // Navigate to the movie detail page
  const openMovie = () => {
    window.scrollTo({ top: 0, behavior: 'smooth' });
    navigateTo(`movie/${movie.movieId}`);
  };

  // Allow keyboard navigation when the card itself is focused
  const handleCardKeyDown = (event) => {
    if (event.target !== event.currentTarget) return;

    if (event.key === 'Enter' || event.key === ' ') {
      event.preventDefault();
      openMovie();
    }
  };

  return (
    <div
      className="movie-card"
      onClick={openMovie}
      onKeyDown={handleCardKeyDown}
      role="link"
      tabIndex={0}
      aria-label={`View details for ${movie.title}`}
    >
      <div className="movie-card__poster-wrap">
        <img
          className="movie-card__poster"
          src={getPosterSrc(movie.poster || movie.posterUrl)}
          alt={movie.title}
          onError={handlePosterError}
        />

        <div className="movie-card__badge">IMDb</div>

        {movie.certificate && (
          <div className="movie-card__certificate">
            {formatCertificate(movie.certificate)}
          </div>
        )}
      </div>

      <div className="movie-card__info">
        <div className="movie-card__title">{movie.title}</div>

        <div className="movie-card__rating">
          <span className="movie-card__rating-star">★</span>{' '}
          <span className="movie-card__rating-value">
            {movie.imdbRating
              ? `${movie.imdbRating}/10 IMDb`
              : 'Not Rated'}
          </span>
        </div>

        <div className="movie-card__genres">{movie.genre || '-'}</div>

        <button
          type="button"
          className="movie-card__book-btn"
          onClick={(event) => {
            event.stopPropagation();
            openMovie();
          }}
        >
          BOOK TICKETS
        </button>
      </div>
    </div>
  );
}
