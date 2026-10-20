import { useEffect, useMemo, useState } from 'react';
import { useApp } from '../context/AppContext';
import { getTheatres, searchTheatresByCity } from '../api/api';

const address = (theatre) => [
  theatre.buildingName,
  theatre.street,
  theatre.area,
].filter(Boolean).join(', ');

export default function Theatres() {
  const { selectedCity, navigateTo } = useApp();
  const [theatres, setTheatres] = useState([]);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let active = true;
    setLoading(true);
    setError('');
    const request = selectedCity?.trim()
      ? searchTheatresByCity(selectedCity.trim())
      : getTheatres();
    request
      .then((result) => {
        if (active) setTheatres(Array.isArray(result) ? result : []);
      })
      .catch((requestError) => {
        if (active) setError(requestError.message || 'Unable to load theatres.');
      })
      .finally(() => {
        if (active) setLoading(false);
      });
    return () => { active = false; };
  }, [selectedCity]);

  const visibleTheatres = useMemo(() => {
    const query = search.trim().toLowerCase();
    if (!query) return theatres;
    return theatres.filter((theatre) => [
      theatre.name, theatre.city, theatre.area, theatre.buildingName,
    ].some((value) => String(value || '').toLowerCase().includes(query)));
  }, [search, theatres]);

  return (
    <div className="theatres-page">
      <section className="theatres-page__hero">
        <div>
          <div className="theatres-page__eyebrow">GALAXY CINEMAS</div>
          <h1>Theatres <span>{selectedCity ? `in ${selectedCity}` : 'in All Cities'}</span></h1>
          <p>Select a cinema hall to browse schedules and showtimes.</p>
        </div>
        <label className="theatres-page__search">
          <span aria-hidden="true">⌕</span>
          <input
            type="search"
            placeholder="Filter by city or theatre..."
            value={search}
            onChange={(event) => setSearch(event.target.value)}
            aria-label="Filter theatres"
          />
        </label>
      </section>

      {loading ? (
        <div className="spinner" />
      ) : error ? (
        <div className="empty-state"><p className="empty-state__text">{error}</p></div>
      ) : visibleTheatres.length === 0 ? (
        <div className="empty-state">
          <p className="empty-state__text">No theatres found.</p>
          <p className="empty-state__sub">Try another city or search term.</p>
        </div>
      ) : (
        <div className="theatre-grid">
          {visibleTheatres.map((theatre) => {
            const theatreId = theatre.theatreId;
            return (
              <article key={theatreId} className="theatre-card theatre-card--directory">
                <div className="theatre-card__topline">
                  <span className="theatre-card__pin">◉</span>
                  {theatre.city && <span className="theatre-card__id">{theatre.city}</span>}
                </div>
                <h2 className="theatre-card__name">{theatre.name || 'Galaxy Cinema'}</h2>
                <p className="theatre-card__location">{address(theatre) || 'Cinema hall'}</p>
                <p className="theatre-card__city">
                  <span aria-hidden="true">◉</span> {theatre.city || '—'}, {theatre.state || ''} {theatre.pinCode ? `- ${theatre.pinCode}` : ''}
                </p>
                <button
                  type="button"
                  className="theatre-card__browse-btn"
                  onClick={() => navigateTo(`theatre/${theatreId}`)}
                >
                  View Shows &amp; Book Tickets <span>›</span>
                </button>
              </article>
            );
          })}
        </div>
      )}
    </div>
  );
}
