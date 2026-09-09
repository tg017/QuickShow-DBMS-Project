import { useState } from 'react';
import { useApp } from '../context/AppContext';

export default function Topbar() {
  const { user, logoutUser, currentRoute, navigateTo, setAuthModalOpen, setAuthTab } = useApp();
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const [mobileOpen, setMobileOpen] = useState(false);

  const navLinks = [
    { label: 'HOME', route: 'home' },
    { label: 'MOVIES', route: 'movies' },
    { label: 'THEATRES', route: 'theatres' },
    { label: 'SHOWTIMES', route: 'showtimes' },
    { label: 'PROFILE', route: 'profile' },
  ];

  const initials = user 
    ? (((user.firstName?.[0] || '') + (user.lastName?.[0] || '')).toUpperCase() || 'U')
    : '';

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
              onClick={(e) => { e.preventDefault(); navigateTo(link.route); }}
            >
              {link.label}
            </a>
          ))}
        </div>

        <div className="topbar__actions">
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
                onClick={() => setDropdownOpen(!dropdownOpen)}
              >
                <div className="topbar__avatar">{initials}</div>
                <span className="topbar__user-name">{user.firstName || 'User'}</span>
                <svg width="12" height="12" fill="currentColor" viewBox="0 0 16 16"><path d="M8 11L3 6h10z"/></svg>
              </button>

              {dropdownOpen && (
                <div className="topbar__dropdown-menu open">
                  <a href="#profile" className="topbar__dropdown-item" onClick={(e) => { e.preventDefault(); setDropdownOpen(false); navigateTo('profile'); }}>👤 My Profile</a>
                  <a href="#bookings" className="topbar__dropdown-item" onClick={(e) => { e.preventDefault(); setDropdownOpen(false); navigateTo('bookings'); }}>🎫 My Bookings</a>
                  <hr style={{ borderColor: 'var(--border-subtle)', margin: '4px 0' }} />
                  <button className="topbar__dropdown-item" onClick={() => { setDropdownOpen(false); logoutUser(); }}>🚪 Sign Out</button>
                </div>
              )}
            </div>
          )}

          <button className="topbar__mobile-toggle" onClick={() => setMobileOpen(!mobileOpen)} aria-label="Menu">
            <svg width="24" height="24" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24"><path d="M3 12h18M3 6h18M3 18h18"/></svg>
          </button>
        </div>
      </nav>

      <div className={`mobile-nav ${mobileOpen ? 'open' : ''}`}>
        {navLinks.map((link) => (
          <a
            key={link.route}
            href={`#${link.route}`}
            className="mobile-nav__link"
            onClick={(e) => { e.preventDefault(); setMobileOpen(false); navigateTo(link.route); }}
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
