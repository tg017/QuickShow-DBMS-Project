import { useEffect, useMemo, useState } from 'react';
import { getTheatreShows, getTheatres } from '../api/api';
import { formatLocalDate } from '../api/data';
import { useApp } from '../context/AppContext';

const formatScreenType = (value) => {
  if (!value) return '2D';
  return value.replace('TWO_D', '2D').replace('THREE_D', '3D')
    .replace('IMAX_3D', 'IMAX 3D').replace('IMAX_2D', 'IMAX 2D')
    .replace('FOUR_DX', '4DX').replace(/_/g, ' ');
};

const dateOptions = () => Array.from({ length: 6 }, (_, index) => {
  const date = new Date();
  date.setDate(date.getDate() + index);
  return date;
});

const formatTime = (value) => {
  if (!value) return 'Time unavailable';
  const [hours, minutes] = String(value).slice(0, 5).split(':');
  const date = new Date();
  date.setHours(Number(hours), Number(minutes), 0, 0);
  return date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });
};

export default function TheatreDetail({ theatreId }) {
  const { navigateTo } = useApp();
  const [theatre, setTheatre] = useState(null);
  const [selectedDate, setSelectedDate] = useState(formatLocalDate(new Date()));
  const [shows, setShows] = useState([]);
  const [theatreLoading, setTheatreLoading] = useState(true);
  const [showsLoading, setShowsLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    getTheatres()
      .then((list) => setTheatre((list || []).find((item) => String(item.theatreId) === String(theatreId)) || null))
      .catch((requestError) => setError(requestError.message || 'Unable to load theatre.'))
      .finally(() => setTheatreLoading(false));
  }, [theatreId]);

  useEffect(() => {
    setShowsLoading(true);
    getTheatreShows(theatreId, selectedDate)
      .then((result) => setShows(Array.isArray(result) ? result : []))
      .catch((requestError) => setError(requestError.message || 'Unable to load shows.'))
      .finally(() => {
        setShowsLoading(false);
      });
  }, [selectedDate, theatreId]);

  const dates = useMemo(() => dateOptions(), []);
  const address = theatre && [theatre.buildingName, theatre.street, theatre.area, theatre.city, theatre.state]
    .filter(Boolean).join(', ');

  if (theatreLoading) return <div className="spinner" />;
  if (error && !theatre) return <div className="empty-state"><p className="empty-state__text">{error}</p></div>;
  if (!theatre) return <div className="empty-state"><p className="empty-state__text">Theatre not found.</p></div>;

  return (
    <div className="theatre-detail-page">
      <button type="button" className="theatre-detail-page__back" onClick={() => navigateTo('theatres')}>
        ← Back to Theatre Search
      </button>
      <section className="theatre-detail-page__header">
        <div className="theatre-detail-page__eyebrow">CINEMA HALL</div>
        <h1>{theatre.name} <span>ID: {theatre.theatreId}</span></h1>
        <p>📍 {address}{theatre.pinCode ? ` - ${theatre.pinCode}` : ''}</p>
      </section>

      <section className="theatre-detail-page__schedule">
        <div className="theatre-detail-page__section-title">▣ &nbsp; SELECT DATE <small>(DEFAULT: TODAY)</small></div>
        <div className="theatre-date-strip">
          {dates.map((date) => {
            const value = formatLocalDate(date);
            const active = value === selectedDate;
            return (
              <button type="button" key={value} className={`theatre-date ${active ? 'active' : ''}`} onClick={() => setSelectedDate(value)}>
                <strong>{date.toLocaleDateString('en-US', { weekday: 'short' })}</strong>
                <span>{date.getDate()}</span>
                <small>{date.toLocaleDateString('en-US', { month: 'short' })}</small>
              </button>
            );
          })}
        </div>
      </section>

      <div className="theatre-shows-heading">
        <h2>Running Shows <span>{shows.length} Available</span></h2>
        <time>Date: {selectedDate}</time>
      </div>

      {showsLoading ? <div className="spinner" /> : shows.length === 0 ? (
        <div className="empty-state"><p className="empty-state__text">No shows scheduled for this date.</p></div>
      ) : (
        <div className="theatre-show-list">
          {shows.map((show) => (
            <article className="theatre-show-card" key={show.showId}>
              <div className="theatre-show-card__details">
                <h3>{show.movieTitle || 'Movie'}</h3>
                <div className="theatre-show-card__tags">
                  <span>{show.language || 'Language'}</span>
                  <span>{formatScreenType(show.screenType)}</span>
                </div>
                <div className="theatre-show-card__meta">
                  <span>♟ Available Seats: <b>{show.availableSeats ?? 0}</b></span>
                  <span>▣ Price: <b>₹{show.ticketPrice ?? 0}</b></span>
                  <span>Show ID: #{show.showId}</span>
                </div>
              </div>
              <div className="theatre-show-card__time">
                <small>SHOWTIME</small>
                <strong>{formatTime(show.showTime)}</strong>
              </div>
              <button type="button" className="theatre-show-card__book" onClick={() => navigateTo(`shows/${show.showId}/seats`)}>
                Book {formatTime(show.showTime)} <span>▣</span>
              </button>
            </article>
          ))}
        </div>
      )}
    </div>
  );
}
