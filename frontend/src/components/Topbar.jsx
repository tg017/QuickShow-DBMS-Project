import { useState } from 'react';
import { useApp } from '../context/AppContext';

const CITIES = ['Varanasi', 'Bengaluru', 'Mumbai', 'Delhi-NCR', 'Hyderabad', 'Pune', 'Chennai', 'Kolkata'];

export default function Topbar() {
  const { user, logoutUser, currentRoute, navigateTo, setAuthModalOpen, setAuthTab, selectedCity, changeCity } = useApp();
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const [cityDropdownOpen, setCityDropdownOpen] = useState(false);
  const [mobileOpen, setMobileOpen] = useState(false);

  const navLinks = [
    { label: 'HOME', route: 'home' },
    { label: 'MOVIES', route: 'movies' },
    { label: 'THEATRES', route: 'theatres' },
    { label: 'PROFILE', route: 'profile' },
  ];

  const initials = user
    ? (((user.firstName?.[0] || '') + (user.lastName?.[0] || '')).toUpperCase() || 'U')
    : '';

  const isAdminRoute = currentRoute === 'admin' || currentRoute.startsWith('admin');
  const openProfile = (event) => {
    event.preventDefault();
    if (user) {
      navigateTo('profile');
      return;
    }
    setAuthTab('login');
    setAuthModalOpen(true);
  };

  return (
    <>
      <nav className="topbar">
        <a href="#home" className="topbar__brand" onClick={(e) => { e.preventDefault(); navigateTo('home'); }}>
          <div className="topbar__logo">Q</div>
          <div>
            <div className="topbar__title">QuickShow</div>
            <div className="topbar__subtitle">by Galaxy Cinemas</div>
          </div>
        </a>

        <div className="topbar__nav">
          {navLinks.map((link) => (
            <a
              key={link.route}
              href={`#${link.route}`}
              className={`topbar__link ${currentRoute === link.route ? 'active' : ''}`}
              onClick={link.route === 'profile' ? openProfile : (e) => { e.preventDefault(); navigateTo(link.route); }}
            >
              {link.label}
            </a>
          ))}
          <a
            href="#admin"
            className={`topbar__link ${isAdminRoute ? 'active' : ''}`}
            onClick={(e) => { e.preventDefault(); navigateTo('admin'); }}
          >
            ADMIN
          </a>
        </div>

        <div className="topbar__actions">
          {!isAdminRoute && (
            <>
              <div className="topbar__dropdown" style={{ position: 'relative' }}>
                <button
                  className="topbar__city-btn"
                  onClick={() => {
                    setCityDropdownOpen(!cityDropdownOpen);
                    setDropdownOpen(false);
                  }}
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: '6px',
                    background: 'rgba(255, 255, 255, 0.06)',
                    border: '1px solid var(--border-subtle)',
                    borderRadius: '20px',
                    padding: '6px 14px',
                    color: 'var(--text-primary)',
                    cursor: 'pointer',
                    fontSize: '13px',
                    fontWeight: 500,
                  }}
                >
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="var(--primary)" strokeWidth="2">
                    <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" />
                    <circle cx="12" cy="10" r="3" />
                  </svg>
                  <span>{selectedCity}</span>
                  <svg width="10" height="10" fill="currentColor" viewBox="0 0 16 16" style={{ opacity: 0.6, marginLeft: '2px' }}>
                    <path d="M8 11L3 6h10z" />
                  </svg>
                </button>

                {cityDropdownOpen && (
                  <div
                    className="topbar__dropdown-menu open"
                    style={{
                      position: 'absolute',
                      top: 'calc(100% + 8px)',
                      right: 0,
                      minWidth: '160px',
                      background: 'var(--bg-surface)',
                      border: '1px solid var(--border)',
                      borderRadius: '8px',
                      padding: '6px 0',
                      boxShadow: 'var(--shadow-lg)',
                      zIndex: 1002,
                    }}
                  >
                    <div style={{ padding: '6px 14px', fontSize: '11px', color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '1px' }}>
                      Select City
                    </div>
                    {CITIES.map((city) => (
                      <button
                        key={city}
                        className="topbar__dropdown-item"
                        style={{
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'space-between',
                          background: city === selectedCity ? 'rgba(0, 229, 255, 0.1)' : 'transparent',
                          color: city === selectedCity ? 'var(--primary)' : 'var(--text-secondary)',
                          fontWeight: city === selectedCity ? 600 : 400,
                        }}
                        onClick={() => {
                          changeCity(city);
                          setCityDropdownOpen(false);
                        }}
                      >
                        <span>{city}</span>
                        {city === selectedCity && <span style={{ fontSize: '12px' }}>✓</span>}
                      </button>
                    ))}
                  </div>
                )}
              </div>

              {!user ? (
                <button
                  className="topbar__auth-btn"
                  onClick={() => { setAuthTab('login'); setAuthModalOpen(true); }}
                >
                  Login / Register
                </button>
              ) : (
                <div className="topbar__dropdown">
                  <button
                    className="topbar__user"
                    onClick={() => {
                      setDropdownOpen(!dropdownOpen);
                      setCityDropdownOpen(false);
                    }}
                  >
                    <div className="topbar__avatar">{initials}</div>
                    <span className="topbar__user-name">{user.firstName || 'User'}</span>
                    <svg width="12" height="12" fill="currentColor" viewBox="0 0 16 16"><path d="M8 11L3 6h10z" /></svg>
                  </button>

                  {dropdownOpen && (
                    <div className="topbar__dropdown-menu open">
                      <a href="#profile" className="topbar__dropdown-item" onClick={(e) => { e.preventDefault(); setDropdownOpen(false); navigateTo('profile'); }}>My Profile</a>
                      <a href="#bookings" className="topbar__dropdown-item" onClick={(e) => { e.preventDefault(); setDropdownOpen(false); navigateTo('bookings'); }}>My Bookings</a>
                      <hr style={{ borderColor: 'var(--border-subtle)', margin: '4px 0' }} />
                      <button className="topbar__dropdown-item" onClick={() => { setDropdownOpen(false); logoutUser(); }}>Sign Out</button>
                    </div>
                  )}
                </div>
              )}
            </>
          )}

          <button className="topbar__mobile-toggle" onClick={() => setMobileOpen(!mobileOpen)} aria-label="Menu">
            <svg width="24" height="24" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24"><path d="M3 12h18M3 6h18M3 18h18" /></svg>
          </button>
        </div>
      </nav>

      <div className={`mobile-nav ${mobileOpen ? 'open' : ''}`}>
        <div style={{ padding: '12px 20px', borderBottom: '1px solid var(--border-subtle)' }}>
          <div style={{ fontSize: '11px', color: 'var(--text-muted)', textTransform: 'uppercase', marginBottom: '8px' }}>City</div>
          <select
            value={selectedCity}
            onChange={(e) => changeCity(e.target.value)}
            className="sidebar__select"
            style={{ width: '100%' }}
          >
            {CITIES.map((c) => (
              <option key={c} value={c}>{c}</option>
            ))}
          </select>
        </div>
        {navLinks.map((link) => (
          <a
            key={link.route}
            href={`#${link.route}`}
            className="mobile-nav__link"
            onClick={link.route === 'profile'
              ? (e) => { setMobileOpen(false); openProfile(e); }
              : (e) => { e.preventDefault(); setMobileOpen(false); navigateTo(link.route); }}
          >
            {link.label}
          </a>
        ))}
        <a href="#bookings" className="mobile-nav__link" onClick={(e) => { e.preventDefault(); setMobileOpen(false); navigateTo('bookings'); }}>MY BOOKINGS</a>
        <a href="#admin" className="mobile-nav__link" onClick={(e) => { e.preventDefault(); setMobileOpen(false); navigateTo('admin'); }}>ADMIN PORTAL</a>
      </div>
    </>
  );
}
