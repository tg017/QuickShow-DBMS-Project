import { useState, useEffect } from 'react';
import { useApp } from '../context/AppContext';
import { getTheatres, getTheatreShows } from '../api/api';
import { formatLocalDate } from '../api/data';

export default function Theatres() {
  const { selectedCity, navigateTo } = useApp();
  const [theatres, setTheatres] = useState([]);
  const [theatreShows, setTheatreShows] = useState({});
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    getTheatres()
      .then(async (all) => {
        let list = all || [];
        if (selectedCity && list.length > 0) {
          const filtered = list.filter((t) => (t.city || '').toLowerCase() === selectedCity.toLowerCase());
          list = filtered.length > 0 ? filtered : list;
        }
        setTheatres(list);
        setLoading(false);

        const today = formatLocalDate(new Date());
        const showsMap = {};
        for (const theatre of list) {
          const tid = theatre.theatreId || theatre.id;
          try {
            const shows = await getTheatreShows(tid, today);
            showsMap[tid] = shows || [];
          } catch {
            showsMap[tid] = [];
          }
        }
        setTheatreShows(showsMap);
      })
      .catch((err) => {
        console.error(err);
        setTheatres([]);
        setLoading(false);
      });
  }, [selectedCity]);

  if (loading) {
    return <div className="spinner" style={{ margin: '60px auto' }}></div>;
  }

  return (
    <div style={{ padding: '24px' }}>
      <h2 className="section-header__title" style={{ marginBottom: '24px' }}>
        Theatres {selectedCity ? `in ${selectedCity}` : ''} 🏢
      </h2>

      {theatres.length === 0 ? (
        <div className="empty-state" style={{ padding: '40px', textAlign: 'center', color: 'var(--text-secondary)' }}>
          No theatres found {selectedCity ? `in ${selectedCity}` : ''}.
        </div>
      ) : (
        <div className="theatre-grid">
          {theatres.map((theatre) => {
            const tid = theatre.theatreId || theatre.id;
            const shows = theatreShows[tid];

            return (
              <div key={tid} className="theatre-card">
                <div className="theatre-card__name">{theatre.name || 'Galaxy Cinema'}</div>
                <div className="theatre-card__location">
                  {theatre.buildingName ? `${theatre.buildingName}, ` : ''}
                  {theatre.street ? `${theatre.street}, ` : ''}
                  {theatre.area || ''}
                </div>
                <div className="theatre-card__city">
                  📍 {theatre.city || ''}, {theatre.state || ''} {theatre.pinCode ? `- ${theatre.pinCode}` : ''}
                </div>

                <div className="theatre-card__shows">
                  {shows === undefined ? (
                    <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>Loading shows...</div>
                  ) : shows.length === 0 ? (
                    <span style={{ fontSize: '12px', color: 'var(--text-muted)' }}>No shows scheduled today</span>
                  ) : (
                    shows.map((s) => {
                      const sid = s.showId || s.id;
                      const screenType = s.screen?.screenType || s.screenType || '2D';
                      const timeStr = s.showTime ? s.showTime.slice(0, 5) : '18:00';
                      return (
                        <button
                          key={sid}
                          className="theatre-card__show-time"
                          onClick={() => navigateTo(`shows/${sid}/seats`)}
                          title={`${s.movieTitle ? s.movieTitle + ' · ' : ''}${timeStr} (${screenType}) - Click to select seats`}
                        >
                          {timeStr} ({screenType})
                        </button>
                      );
                    })
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
