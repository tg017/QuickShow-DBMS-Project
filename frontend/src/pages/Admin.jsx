import { useState, useEffect } from 'react';
import { useApp } from '../context/AppContext';
import {
  adminLogin, adminRegister,
  adminGetAllMovies,
  adminGetAllTheatres, adminAddTheatre, adminDeleteTheatre,
  adminGetAllShows, adminAddShow, adminDeleteShow, adminGetScreensByTheatre,
  adminGetAllBookings,
  adminGetStatsSummary,
} from '../api/api';

export default function Admin() {
  const { admin, setAdmin, showToast } = useApp();
  const [activeTab, setActiveTab] = useState('dashboard');

  const [authMode, setAuthMode] = useState('login');
  const [adminEmail, setAdminEmail] = useState('admin@galaxycinemas.com');
  const [adminPassword, setAdminPassword] = useState('Admin@123');
  const [adminFirstName, setAdminFirstName] = useState('');
  const [adminLastName, setAdminLastName] = useState('');
  const [authLoading, setAuthLoading] = useState(false);

  const [movies, setMovies] = useState([]);
  const [theatres, setTheatres] = useState([]);
  const [shows, setShows] = useState([]);
  const [bookings, setBookings] = useState([]);
  const [stats, setStats] = useState(null);
  const [loadingData, setLoadingData] = useState(false);

  const [screens, setScreens] = useState([]);

  const [newTheatre, setNewTheatre] = useState({
    name: '', city: 'Varanasi', state: 'Uttar Pradesh', area: '', buildingName: '', street: '', pinCode: '',
  });

  const [newShow, setNewShow] = useState({
    movieId: '', theatreId: '', screenId: '', showDate: new Date().toISOString().split('T')[0],
    showTime: '18:00', ticketPrice: 250, showStatus: 'SCHEDULED',
  });

  const loadAdminData = async () => {
    setLoadingData(true);
    try {
      const [m, t, s, b, st] = await Promise.all([
        adminGetAllMovies().catch(() => []),
        adminGetAllTheatres().catch(() => []),
        adminGetAllShows().catch(() => []),
        adminGetAllBookings().catch(() => []),
        adminGetStatsSummary().catch(() => null),
      ]);
      setMovies(m || []);
      setTheatres(t || []);
      setShows(Array.isArray(s) ? s : []);
      setBookings(Array.isArray(b) ? b : []);
      setStats(st);

      if (m && m.length > 0 && !newShow.movieId) {
        setNewShow(prev => ({ ...prev, movieId: m[0].movieId }));
      }
      if (t && t.length > 0 && !newShow.theatreId) {
        const firstTheatre = t[0];
        setNewShow(prev => ({ ...prev, theatreId: firstTheatre.theatreId }));

        const sc = await adminGetScreensByTheatre(firstTheatre.theatreId).catch(() => []);
        setScreens(sc || []);
        if (sc && sc.length > 0) setNewShow(prev => ({ ...prev, screenId: sc[0].screenId }));
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoadingData(false);
    }
  };

  useEffect(() => {
    if (admin) loadAdminData();
  }, [admin]);

  const handleTheatreChangeForShow = async (theatreId) => {
    setNewShow(prev => ({ ...prev, theatreId, screenId: '' }));
    const sc = await adminGetScreensByTheatre(theatreId).catch(() => []);
    setScreens(sc || []);
    if (sc && sc.length > 0) setNewShow(prev => ({ ...prev, screenId: sc[0].screenId }));
  };

  const handleAdminAuth = async (e) => {
    e.preventDefault();
    setAuthLoading(true);
    try {
      if (authMode === 'login') {

        const res = await adminLogin({ email: adminEmail, password: adminPassword });
        setAdmin(res);
        localStorage.setItem('quickshow_admin', JSON.stringify(res));
        showToast(`Welcome, ${res.firstName || 'Admin'}!`, 'success');
      } else {
        await adminRegister({
          firstName: adminFirstName,
          lastName: adminLastName,
          email: adminEmail,
          password: adminPassword,
          role: 'ADMIN',
        });
        showToast('Admin registered! Please login.', 'success');
        setAuthMode('login');
      }
    } catch (err) {
      showToast(err.message || 'Authentication failed', 'error');
    } finally {
      setAuthLoading(false);
    }
  };

  const handleAdminLogout = () => {
    setAdmin(null);
    localStorage.removeItem('quickshow_admin');
    showToast('Admin logged out', 'info');
  };

  const handleAddTheatre = async (e) => {
    e.preventDefault();
    try {
      await adminAddTheatre(newTheatre);
      showToast('Theatre added successfully!', 'success');
      setNewTheatre({ name: '', city: 'Varanasi', state: 'Uttar Pradesh', area: '', buildingName: '', street: '', pinCode: '' });
      loadAdminData();
    } catch (err) {
      showToast(err.message || 'Failed to add theatre', 'error');
    }
  };

  const handleDeleteTheatre = async (id) => {
    if (!window.confirm('Delete this theatre?')) return;
    try {
      await adminDeleteTheatre(id);
      showToast('Theatre deleted', 'info');
      loadAdminData();
    } catch (err) {
      showToast(err.message || 'Failed to delete theatre', 'error');
    }
  };

  const handleAddShow = async (e) => {
    e.preventDefault();
    try {
      await adminAddShow({
        movieId: parseInt(newShow.movieId),
        screenId: parseInt(newShow.screenId),   
        showDate: newShow.showDate,
        showTime: newShow.showTime,
        ticketPrice: parseFloat(newShow.ticketPrice),
        showStatus: newShow.showStatus,
      });
      showToast('Show scheduled successfully!', 'success');
      loadAdminData();
    } catch (err) {
      showToast(err.message || 'Failed to schedule show', 'error');
    }
  };

  const handleDeleteShow = async (id) => {
    if (!window.confirm('Delete this show?')) return;
    try {
      await adminDeleteShow(id);
      showToast('Show deleted', 'info');
      loadAdminData();
    } catch (err) {
      showToast(err.message || 'Failed to delete show', 'error');
    }
  };

  if (!admin) {
    return (
      <div className="admin-auth" style={{ maxWidth: '420px', margin: '40px auto', padding: '24px', background: 'var(--bg-secondary)', borderRadius: '12px', border: '1px solid var(--border-subtle)' }}>
        <h2 style={{ textAlign: 'center', marginBottom: '16px' }}>🎬 Admin Portal</h2>

        <div className="auth-modal__tabs" style={{ marginBottom: '20px' }}>
          <button className={`auth-modal__tab ${authMode === 'login' ? 'auth-modal__tab--active' : ''}`} onClick={() => setAuthMode('login')}>Admin Login</button>
          <button className={`auth-modal__tab ${authMode === 'register' ? 'auth-modal__tab--active' : ''}`} onClick={() => setAuthMode('register')}>Register Admin</button>
        </div>

        <form onSubmit={handleAdminAuth} className="admin-form">
          {authMode === 'register' && (
            <>
              <div style={{ marginBottom: '12px' }}>
                <label className="auth-modal__label">First Name</label>
                <input className="auth-modal__input" placeholder="First Name"
                  value={adminFirstName} onChange={(e) => setAdminFirstName(e.target.value)} required />
              </div>
              <div style={{ marginBottom: '12px' }}>
                <label className="auth-modal__label">Last Name</label>
                <input className="auth-modal__input" placeholder="Last Name"
                  value={adminLastName} onChange={(e) => setAdminLastName(e.target.value)} required />
              </div>
            </>
          )}
          <div style={{ marginBottom: '12px' }}>
            <label className="auth-modal__label">Admin Email</label>
            <input type="email" className="auth-modal__input" placeholder="admin@galaxycinemas.com"
              value={adminEmail} onChange={(e) => setAdminEmail(e.target.value)} required />
          </div>
          <div style={{ marginBottom: '16px' }}>
            <label className="auth-modal__label">Password</label>
            <input type="password" className="auth-modal__input" placeholder="Admin Password"
              value={adminPassword} onChange={(e) => setAdminPassword(e.target.value)} required />
          </div>
          <button type="submit" className="auth-modal__submit" disabled={authLoading}>
            {authLoading ? 'Verifying...' : authMode === 'login' ? 'Sign In as Admin' : 'Register Admin'}
          </button>
        </form>
      </div>
    );
  }

  return (
    <div className="admin-layout">

      <div className="inner-sidebar">
        {[
          { tab: 'dashboard', icon: '📊', label: 'Dashboard' },
          { tab: 'movies',    icon: '🎬', label: 'Movies'    },
          { tab: 'theatres',  icon: '🏢', label: 'Theatres'  },
          { tab: 'shows',     icon: '🎭', label: 'Schedule Shows' },
          { tab: 'bookings',  icon: '🎫', label: 'Bookings'  },
        ].map(({ tab, icon, label }) => (
          <button
            key={tab}
            className={`inner-sidebar__item ${activeTab === tab ? 'inner-sidebar__item--active' : ''}`}
            onClick={() => setActiveTab(tab)}
          >
            <span className="inner-sidebar__item-icon">{icon}</span>{label}
          </button>
        ))}

        <hr style={{ borderColor: 'var(--border-subtle)', margin: '16px 0' }} />
        <button className="inner-sidebar__item" onClick={handleAdminLogout} style={{ color: 'var(--danger)' }}>
          <span className="inner-sidebar__item-icon">🚪</span>Exit Admin
        </button>
      </div>

      <div className="admin-content" style={{ padding: '24px' }}>
        {loadingData ? (
          <div className="spinner" style={{ margin: '40px auto' }}></div>
        ) : (
          <>

            {activeTab === 'dashboard' && (
              <div>
                <h2 style={{ marginBottom: '20px' }}>System Overview 📊</h2>
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '16px', marginBottom: '24px' }}>
                  {[
                    { label: 'Total Movies',   value: movies.length,   color: 'var(--primary)' },
                    { label: 'Active Theatres', value: theatres.length, color: '#00E5FF' },
                    { label: 'Scheduled Shows', value: shows.length,   color: '#A78BFA' },
                    { label: 'Total Bookings',  value: bookings.length, color: '#FFD700' },
                    ...(stats ? [
                      { label: 'Total Revenue', value: `₹${stats.totalRevenue || 0}`, color: '#4ADE80' },
                      { label: 'Confirmed',     value: stats.confirmedBookings || 0,   color: '#4ADE80' },
                      { label: 'Cancelled',     value: stats.cancelledBookings || 0,   color: 'var(--danger)' },
                    ] : []),
                  ].map(({ label, value, color }) => (
                    <div key={label} style={{ background: 'var(--bg-secondary)', padding: '20px', borderRadius: '8px', border: '1px solid var(--border-subtle)' }}>
                      <div style={{ fontSize: '13px', color: 'var(--text-muted)' }}>{label}</div>
                      <div style={{ fontSize: '28px', fontWeight: 700, color, marginTop: '4px' }}>{value}</div>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {activeTab === 'movies' && (
              <div>
                <h2 style={{ marginBottom: '16px' }}>Movies 🎬</h2>
                <p style={{ color: 'var(--text-muted)', marginBottom: '16px', fontSize: '14px' }}>
                  ℹ️ Movie creation/deletion is managed directly in the database by the backend team. This view is read-only.
                </p>
                <table className="bookings-table">
                  <thead>
                    <tr>
                      <th>ID</th><th>Title</th><th>Genre</th><th>Language</th><th>IMDb</th>
                    </tr>
                  </thead>
                  <tbody>
                    {movies.map((m) => (
                      <tr key={m.movieId}>
                        <td>{m.movieId}</td>
                        <td><strong>{m.title}</strong></td>
                        <td>{m.genre}</td>
                        <td>{m.language}</td>
                        <td>⭐ {m.imdbRating || '-'}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}

            {activeTab === 'theatres' && (
              <div>
                <h2 style={{ marginBottom: '16px' }}>Manage Theatres 🏢</h2>

                <form onSubmit={handleAddTheatre} style={{ background: 'var(--bg-secondary)', padding: '20px', borderRadius: '8px', border: '1px solid var(--border-subtle)', marginBottom: '24px' }}>
                  <h3 style={{ fontSize: '16px', marginBottom: '12px' }}>+ Add New Theatre</h3>
                  <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '12px' }}>
                    <input className="auth-modal__input" placeholder="Theatre Name *" value={newTheatre.name}
                      onChange={(e) => setNewTheatre({ ...newTheatre, name: e.target.value })} required />
                    <input className="auth-modal__input" placeholder="Building Name" value={newTheatre.buildingName}
                      onChange={(e) => setNewTheatre({ ...newTheatre, buildingName: e.target.value })} />
                    <input className="auth-modal__input" placeholder="Street *" value={newTheatre.street}
                      onChange={(e) => setNewTheatre({ ...newTheatre, street: e.target.value })} required />
                    <input className="auth-modal__input" placeholder="Area *" value={newTheatre.area}
                      onChange={(e) => setNewTheatre({ ...newTheatre, area: e.target.value })} required />
                    <input className="auth-modal__input" placeholder="City *" value={newTheatre.city}
                      onChange={(e) => setNewTheatre({ ...newTheatre, city: e.target.value })} required />
                    <input className="auth-modal__input" placeholder="State *" value={newTheatre.state}
                      onChange={(e) => setNewTheatre({ ...newTheatre, state: e.target.value })} required />
                    <input className="auth-modal__input" placeholder="Pin Code *" value={newTheatre.pinCode}
                      onChange={(e) => setNewTheatre({ ...newTheatre, pinCode: e.target.value })} required />
                  </div>
                  <button type="submit" className="add-btn" style={{ marginTop: '12px' }}>Add Theatre</button>
                </form>

                <div className="theatre-grid">
                  {theatres.map((t) => (
                    <div key={t.theatreId} className="theatre-card">
                      <div className="theatre-card__name">{t.name}</div>
                      <div className="theatre-card__city">📍 {t.area ? `${t.area}, ` : ''}{t.city}, {t.state}</div>
                      {t.pinCode && <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>PIN: {t.pinCode}</div>}
                      <button className="cancel-btn" style={{ marginTop: '8px' }} onClick={() => handleDeleteTheatre(t.theatreId)}>
                        Delete
                      </button>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {activeTab === 'shows' && (
              <div>
                <h2 style={{ marginBottom: '16px' }}>Schedule Shows 🎭</h2>

                <form onSubmit={handleAddShow} style={{ background: 'var(--bg-secondary)', padding: '20px', borderRadius: '8px', border: '1px solid var(--border-subtle)', marginBottom: '24px' }}>
                  <h3 style={{ fontSize: '16px', marginBottom: '12px' }}>+ Schedule a New Show</h3>
                  <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '12px' }}>

                    <div>
                      <label className="auth-modal__label">Select Movie *</label>
                      <select className="sidebar__select" value={newShow.movieId}
                        onChange={(e) => setNewShow({ ...newShow, movieId: e.target.value })} required>
                        <option value="">-- Select Movie --</option>
                        {movies.map((m) => <option key={m.movieId} value={m.movieId}>{m.title}</option>)}
                      </select>
                    </div>

                    <div>
                      <label className="auth-modal__label">Select Theatre *</label>
                      <select className="sidebar__select" value={newShow.theatreId}
                        onChange={(e) => handleTheatreChangeForShow(parseInt(e.target.value))} required>
                        <option value="">-- Select Theatre --</option>
                        {theatres.map((t) => <option key={t.theatreId} value={t.theatreId}>{t.name} ({t.city})</option>)}
                      </select>
                    </div>

                    <div>
                      <label className="auth-modal__label">Select Screen *</label>
                      <select className="sidebar__select" value={newShow.screenId}
                        onChange={(e) => setNewShow({ ...newShow, screenId: e.target.value })} required>
                        <option value="">-- Select Screen --</option>
                        {screens.map((sc) => <option key={sc.screenId} value={sc.screenId}>{sc.name || `Screen ${sc.screenId}`} ({sc.screenType})</option>)}
                      </select>
                    </div>

                    <div>
                      <label className="auth-modal__label">Show Date *</label>
                      <input type="date" className="auth-modal__input" value={newShow.showDate}
                        onChange={(e) => setNewShow({ ...newShow, showDate: e.target.value })} required />
                    </div>

                    <div>
                      <label className="auth-modal__label">Show Time *</label>
                      <input type="time" className="auth-modal__input" value={newShow.showTime}
                        onChange={(e) => setNewShow({ ...newShow, showTime: e.target.value })} required />
                    </div>

                    <div>
                      <label className="auth-modal__label">Ticket Price (₹) *</label>
                      <input type="number" className="auth-modal__input" value={newShow.ticketPrice}
                        onChange={(e) => setNewShow({ ...newShow, ticketPrice: e.target.value })} required />
                    </div>
                  </div>
                  <button type="submit" className="add-btn" style={{ marginTop: '16px' }}>Schedule Show</button>
                </form>

                <h3 style={{ marginBottom: '12px' }}>Existing Shows</h3>
                <table className="bookings-table">
                  <thead>
                    <tr><th>Show ID</th><th>Movie</th><th>Time</th><th>Type</th><th>Price</th><th>Status</th><th>Action</th></tr>
                  </thead>
                  <tbody>
                    {shows.map((s) => {
                      const sid = s.showId || s.id;
                      return (
                        <tr key={sid}>
                          <td>#{sid}</td>
                          <td>{s.movieTitle || s.movie?.title || '-'}</td>
                          <td>{s.showTime ? String(s.showTime).slice(0, 5) : '-'}</td>
                          <td>{s.screenType || s.screen?.screenType || '-'}</td>
                          <td>₹{s.ticketPrice || '-'}</td>
                          <td>
                            <span className={`badge badge--${s.showStatus === 'CANCELLED' ? 'cancelled' : 'confirmed'}`}>
                              {s.showStatus || 'SCHEDULED'}
                            </span>
                          </td>
                          <td>
                            <button className="cancel-btn" onClick={() => handleDeleteShow(sid)}>Delete</button>
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              </div>
            )}

            {activeTab === 'bookings' && (
              <div>
                <h2 style={{ marginBottom: '16px' }}>All System Bookings 🎫</h2>
                <table className="bookings-table">
                  <thead>
                    <tr><th>Booking ID</th><th>Movie</th><th>Theatre</th><th>Date</th><th>Seats</th><th>Amount</th><th>Status</th></tr>
                  </thead>
                  <tbody>
                    {bookings.map((b) => {
                      const bId = b.bookingId || b.id || '-';
                      const movieTitle = b.movie?.title || b.movieTitle || '-';
                      const theatreName = b.theatre?.name || b.theatreName || '-';
                      const showDate = b.show?.showDate || b.showDate || '';
                      const seatCount = b.totalSeatCount || b.seats?.length || b.seatIds?.length || 0;
                      const status = b.bookingStatus || b.status || 'CONFIRMED';
                      return (
                        <tr key={bId}>
                          <td>#BK-{bId}</td>
                          <td>{movieTitle}</td>
                          <td>{theatreName}</td>
                          <td>{showDate}</td>
                          <td>{seatCount} seat(s)</td>
                          <td>₹{b.totalAmount || 0}</td>
                          <td>
                            <span className={`badge badge--${status === 'CANCELLED' ? 'cancelled' : 'confirmed'}`}>{status}</span>
                          </td>
                        </tr>
                      );
                    })}
                    {bookings.length === 0 && (
                      <tr><td colSpan="7" style={{ textAlign: 'center', color: 'var(--text-muted)', padding: '24px' }}>No bookings yet.</td></tr>
                    )}
                  </tbody>
                </table>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}
