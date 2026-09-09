import { useState, useEffect } from 'react';
import { useApp } from '../context/AppContext';
import { getMovies } from '../api/api';

const CITIES = ['Varanasi', 'Bengaluru', 'Mumbai', 'Delhi-NCR', 'Hyderabad', 'Pune', 'Chennai', 'Kolkata'];

export default function Sidebar() {
  const { selectedCity, changeCity, selectedDate, setSelectedDate, filters, setFilters, navigateTo } = useApp();
  const [cityDropdownOpen, setCityDropdownOpen] = useState(false);
  const [recMovies, setRecMovies] = useState([]);

  useEffect(() => {
    getMovies().then((movies) => {
      if (movies && movies.length > 0) {
        setRecMovies(movies.slice(0, 3));
      }
    }).catch(() => {});
  }, []);

  return (
    <aside className="sidebar">

      <div className="sidebar__section">
        <div className="sidebar__city-label">City Name</div>
        <div 
          className="sidebar__city-value" 
          onClick={() => setCityDropdownOpen(!cityDropdownOpen)}
          style={{ cursor: 'pointer' }}
        >
          <svg className="sidebar__city-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/>
          </svg>
          <span className="sidebar__city-name">{selectedCity}</span>
        </div>

        {cityDropdownOpen && (
          <div className="sidebar__city-dropdown open">
            {CITIES.map((city) => (
              <div 
                key={city} 
                className="city-option"
                onClick={() => { changeCity(city); setCityDropdownOpen(false); }}
              >
                {city}
              </div>
            ))}
          </div>
        )}

        <div style={{ marginTop: '12px' }}>
          <input 
            type="date" 
            className="sidebar__date-input" 
            style={{ display: 'block', width: '100%' }}
            value={selectedDate}
            onChange={(e) => setSelectedDate(e.target.value)}
          />
        </div>
      </div>

      <div className="sidebar__filters">
        <select 
          className="sidebar__select" 
          value={filters.language} 
          onChange={(e) => setFilters({ ...filters, language: e.target.value })}
        >
          <option value="">By Language</option>
          <option value="English">English</option>
          <option value="Hindi">Hindi</option>
          <option value="Telugu">Telugu</option>
          <option value="Tamil">Tamil</option>
          <option value="Kannada">Kannada</option>
          <option value="Malayalam">Malayalam</option>
        </select>

        <select 
          className="sidebar__select" 
          value={filters.genre} 
          onChange={(e) => setFilters({ ...filters, genre: e.target.value })}
        >
          <option value="">By Genre</option>
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

        <select 
          className="sidebar__select" 
          value={filters.rating} 
          onChange={(e) => setFilters({ ...filters, rating: e.target.value })}
        >
          <option value="">By Rating</option>
          <option value="U">U – Universal</option>
          <option value="UA_7_PLUS">UA 7+</option>
          <option value="UA_13_PLUS">UA 13+</option>
          <option value="UA_16_PLUS">UA 16+</option>
          <option value="A">A – Adults Only</option>
        </select>
      </div>

      <div className="sidebar__recommended">
        <h3 className="sidebar__rec-title">RECOMMENDED FOR YOU</h3>
        <p className="sidebar__rec-subtitle">Dynamic suggestions based on recent watches.</p>
        <div>
          {recMovies.map((m) => (
            <div 
              key={m.movieId} 
              className="sidebar__rec-card"
              onClick={() => navigateTo(`movie/${m.movieId}`)}
              style={{ cursor: 'pointer' }}
            >
              <img 
                className="sidebar__rec-poster" 
                src={m.poster || m.posterUrl || 'https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg'} 
                alt={m.title} 
              />
              <div className="sidebar__rec-info">
                <div className="sidebar__rec-name">{m.title}</div>
                <div className="sidebar__rec-genre">{m.genre || 'General'}</div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </aside>
  );
}
