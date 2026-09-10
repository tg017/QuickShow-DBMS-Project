import { useState, useEffect } from 'react';
import { useApp } from '../context/AppContext';
import { getMovieById, getShowsByMovie, getTheatres } from '../api/api';
import { formatLocalDate } from '../api/data';

export default function MovieDetail({ movieId }) {
  const { selectedCity, navigateTo } = useApp();
  const [movie, setMovie] = useState(null);
  const [loading, setLoading] = useState(true);
  const [selectedDayOffset, setSelectedDayOffset] = useState(0);
  const [theatres, setTheatres] = useState([]);
  const [theatreShows, setTheatreShows] = useState([]);
  const [selectedTheatreId, setSelectedTheatreId] = useState(null);
  const [selectedScreenType, setSelectedScreenType] = useState(null);
  const [showsLoading, setShowsLoading] = useState(false);

  useEffect(() => {
    getTheatres()
      .then((data) => {
        if (data) setTheatres(data);
      })
      .catch(() => {});
  }, []);

  useEffect(() => {
    setLoading(true);
    getMovieById(movieId)
      .then((data) => {
        setMovie(data);
        setLoading(false);
      })
      .catch((err) => {
        console.error(err);
        setLoading(false);
      });
  }, [movieId]);

  const getOffsetDate = (offset) => {
    const d = new Date();
    d.setDate(d.getDate() + offset);
    return d;
  };

  const resolveCity = (g) => {
    if (g.city) return g.city;
    const match = theatres.find((t) => t.theatreId === g.theatreId);
    return match?.city || '';
  };

  useEffect(() => {
    if (!movieId) return;
    setShowsLoading(true);
    const dateStr = formatLocalDate(getOffsetDate(selectedDayOffset));

    getShowsByMovie(movieId, dateStr)
      .then((shows) => {
        let list = shows || [];

        if (selectedCity && list.length > 0) {
          const cityTheatres = list.filter(g => resolveCity(g).toLowerCase() === selectedCity.toLowerCase());
          if (cityTheatres.length > 0) {
            list = [...cityTheatres, ...list.filter(g => resolveCity(g).toLowerCase() !== selectedCity.toLowerCase())];
          }
        }

        setTheatreShows(list);
        if (list.length > 0) {
          setSelectedTheatreId(list[0].theatreId);
          const firstGroupShows = list[0].shows || [];
          const types = [...new Set(firstGroupShows.map((s) => s.screenType).filter(Boolean))];
          setSelectedScreenType(types[0] || null);
        } else {
          setSelectedTheatreId(null);
          setSelectedScreenType(null);
        }
        setShowsLoading(false);
      })
      .catch((err) => {
        console.error(err);
        setTheatreShows([]);
        setShowsLoading(false);
      });
  }, [movieId, selectedDayOffset, selectedCity, theatres]);

  const handleTheatreChange = (tId) => {
    setSelectedTheatreId(tId);
    const group = theatreShows.find((g) => g.theatreId === tId);
    const types = [...new Set((group?.shows || []).map((s) => s.screenType).filter(Boolean))];
    setSelectedScreenType(types[0] || null);
  };

  if (loading) {
    return <div className="spinner" style={{ margin: '60px auto' }}></div>;
  }

  if (!movie) {
    return (
      <div style={{ padding: '40px', textAlign: 'center', color: 'var(--text-secondary)' }}>
        <h2>Movie not found</h2>
        <button className="movie-card__book-btn" style={{ marginTop: '16px' }} onClick={() => navigateTo('home')}>
          Back to Home
        </button>
      </div>
    );
  }

  const formatDuration = (mins) => {
    if (!mins) return '-';
    const h = Math.floor(mins / 60);
    const m = mins % 60;
    return `${h}h ${m}m`;
  };

  const formatDate = (dateStr) => {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleDateString(undefined, {
      day: 'numeric', month: 'short', year: 'numeric'
    });
  };

  const activeGroup = theatreShows.find((g) => g.theatreId === selectedTheatreId) || theatreShows[0];
  const groupShows = activeGroup?.shows || [];
  const screenTypes = [...new Set(groupShows.map((s) => s.screenType).filter(Boolean))];
  const filteredShows = selectedScreenType 
    ? groupShows.filter((s) => s.screenType === selectedScreenType) 
    : groupShows;

  return (
    <div>

      <div className="movie-detail">
        <img 
          className="movie-detail__poster" 
          src={movie.poster || movie.posterUrl || 'https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg'} 
          alt={movie.title || ''} 
          onError={(e) => { e.target.src = 'https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg'; }}
        />
        <div className="movie-detail__content">
          <h1 className="movie-detail__title">{movie.title || ''}</h1>

          <div className="movie-detail__meta-table">
            <span className="movie-detail__meta-label">IMDb Rating</span>
            <span className="movie-detail__meta-value movie-detail__meta-value--rating">★ {movie.imdbRating || '-'}</span>
            <span className="movie-detail__meta-label">Certificate</span>
            <span className="movie-detail__meta-value">{movie.certificate || '-'}</span>
            <span className="movie-detail__meta-label">Language</span>
            <span className="movie-detail__meta-value">{movie.language || '-'}</span>
            <span className="movie-detail__meta-label">Genre</span>
            <span className="movie-detail__meta-value">{movie.genre || '-'}</span>
            <span className="movie-detail__meta-label">Duration</span>
            <span className="movie-detail__meta-value">{formatDuration(movie.duration || movie.durationMinutes)}</span>
            <span className="movie-detail__meta-label">Release Date</span>
            <span className="movie-detail__meta-value">{formatDate(movie.releaseDate)}</span>
            <span className="movie-detail__meta-label">Director</span>
            <span className="movie-detail__meta-value">{movie.director || '-'}</span>
            <span className="movie-detail__meta-label">Cast</span>
            <span className="movie-detail__meta-value">{Array.isArray(movie.cast) ? movie.cast.join(', ') : (movie.cast || '-')}</span>
          </div>

          <h3 style={{ marginTop: '20px', marginBottom: '8px', color: 'var(--text-primary)' }}>Description</h3>
          <p className="movie-detail__description">{movie.description || ''}</p>
        </div>
      </div>

      <div className="shows-section">

        <div className="date-strip">
          {[0, 1, 2, 3, 4, 5, 6].map((offset) => {
            const d = getOffsetDate(offset);
            const dayName = d.toLocaleDateString(undefined, { weekday: 'short' });
            const dateNum = d.getDate();
            return (
              <div 
                key={offset}
                className={`date-strip__item ${offset === selectedDayOffset ? 'date-strip__item--active' : ''}`}
                onClick={() => setSelectedDayOffset(offset)}
                style={{ cursor: 'pointer' }}
              >
                <div>{dayName}</div>
                <div>{dateNum}</div>
              </div>
            );
          })}
        </div>

        {theatreShows.length > 0 && (
          <div className="shows-section__filters">
            <select 
              className="shows-section__theatre-select"
              value={selectedTheatreId || ''}
              onChange={(e) => handleTheatreChange(parseInt(e.target.value))}
            >
              {theatreShows.map((g) => {
                const c = resolveCity(g);
                return (
                  <option key={g.theatreId} value={g.theatreId}>
                    {g.theatreName} {c ? `(${c})` : ''}
                  </option>
                );
              })}
            </select>

            {screenTypes.length > 0 && (
              <div className="shows-section__screen-tabs">
                {screenTypes.map((st) => (
                  <button 
                    key={st}
                    className={`shows-section__screen-tab ${st === selectedScreenType ? 'shows-section__screen-tab--active' : ''}`}
                    onClick={() => setSelectedScreenType(st)}
                  >
                    {st}
                  </button>
                ))}
              </div>
            )}
          </div>
        )}

        <div className="shows-table">
          {showsLoading ? (
            <div className="spinner" style={{ margin: '30px auto' }}></div>
          ) : theatreShows.length === 0 ? (
            <div className="empty-state" style={{ padding: '30px', textAlign: 'center' }}>
              <div className="empty-state__icon" style={{ fontSize: '32px' }}>🎬</div>
              <p style={{ color: 'var(--text-secondary)', marginTop: '8px' }}>
                No shows available for this date.
              </p>
            </div>
          ) : filteredShows.length === 0 ? (
            <div className="empty-state" style={{ padding: '20px', textAlign: 'center', color: 'var(--text-secondary)' }}>
              <p>No shows found for the selected screen type.</p>
            </div>
          ) : (
            filteredShows.map((show) => (
              <div key={show.showId} className="shows-table__row">
                <div className="shows-table__time">{show.showTime ? show.showTime.slice(0, 5) : '-'}</div>
                <div className="shows-table__type">{show.screenType || '2D'}</div>
                <div className="shows-table__price">₹{show.ticketPrice || '250'}</div>
                <button 
                  className="shows-table__book-btn"
                  onClick={() => navigateTo(`shows/${show.showId}/seats`)}
                >
                  Book Now
                </button>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
}
