import { useState, useEffect } from 'react';
import { useApp } from '../context/AppContext';
import {
  adminLogin,
  adminRegister,
  adminGetAllTheatres,
  adminGetTheatreById,
  adminAddTheatre,
  adminUpdateTheatre,
  adminDeleteTheatre,
  adminGetScreensByTheatre,
  adminGetScreenById,
  adminAddScreen,
  adminUpdateScreen,
  adminDeleteScreen,
  adminGetAllShows,
  adminGetShowById,
  adminAddShow,
  adminUpdateShow,
  adminDeleteShow,
  adminGetAllMovies,
  adminGetMovieById,
  adminAddMovie,
  adminUpdateMovie,
  adminDeleteMovie,
  adminGetMovieCast,
  adminAddActor,
  adminDeleteActor,
  adminGetAllBookings,
  adminSearchBookings,
  adminGetAllPayments,
  adminGetPaymentById,
  adminSearchPayments,
  adminGetStatsSummary,
  adminGetStatsRevenue,
  adminGetStatsBookings,
  adminGetTopMovies,
  adminGetTheatrePerformance,
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
  const [payments, setPayments] = useState([]);
  const [screens, setScreens] = useState([]);
  const [selectedTheatreForScreens, setSelectedTheatreForScreens] = useState('');
  const [loadingData, setLoadingData] = useState(false);

  const [statsSummary, setStatsSummary] = useState(null);
  const [statsRevenue, setStatsRevenue] = useState(null);
  const [statsBookings, setStatsBookings] = useState(null);
  const [topMovies, setTopMovies] = useState([]);
  const [theatrePerformance, setTheatrePerformance] = useState([]);
  const [statsFilter, setStatsFilter] = useState({
    from: '',
    to: '',
    theatreId: '',
    movieId: '',
  });

  const [newMovie, setNewMovie] = useState({
    title: '',
    language: 'English',
    genre: 'Action',
    duration: 120,
    releaseDate: new Date().toISOString().split('T')[0],
    imdbRating: 8.0,
    certificate: 'UA_13_PLUS',
    director: '',
    poster: '',
    description: '',
  });
  const [editingMovie, setEditingMovie] = useState(null);

  const [castMovie, setCastMovie] = useState(null);
  const [castList, setCastList] = useState([]);
  const [newActorName, setNewActorName] = useState('');

  const [newTheatre, setNewTheatre] = useState({
    name: '',
    city: 'Varanasi',
    state: 'Uttar Pradesh',
    area: '',
    buildingName: '',
    street: '',
    pinCode: '',
  });
  const [editingTheatre, setEditingTheatre] = useState(null);

  const [newScreen, setNewScreen] = useState({
    name: 'Screen 1',
    screenType: 'IMAX',
    capacity: 60,
  });
  const [editingScreen, setEditingScreen] = useState(null);

  const [newShow, setNewShow] = useState({
    movieId: '',
    theatreId: '',
    screenId: '',
    showDate: new Date().toISOString().split('T')[0],
    showTime: '18:00',
    ticketPrice: 250,
    showStatus: 'SCHEDULED',
  });
  const [editingShow, setEditingShow] = useState(null);

  const [bookingFilter, setBookingFilter] = useState({ movie: '', theatre: '' });
  const [paymentFilter, setPaymentFilter] = useState({ theatre: '', paymentStatus: '' });
  const [selectedPaymentDetail, setSelectedPaymentDetail] = useState(null);

  const loadAdminData = async () => {
    setLoadingData(true);
    try {
      const [m, t, s, b, p] = await Promise.all([
        adminGetAllMovies().catch(() => []),
        adminGetAllTheatres().catch(() => []),
        adminGetAllShows().catch(() => []),
        adminGetAllBookings().catch(() => []),
        adminGetAllPayments().catch(() => []),
      ]);

      const validMovies = Array.isArray(m) ? m : [];
      const validTheatres = Array.isArray(t) ? t : [];
      const validShows = Array.isArray(s) ? s : [];
      const validBookings = Array.isArray(b) ? b : [];
      const validPayments = Array.isArray(p) ? p : [];

      setMovies(validMovies);
      setTheatres(validTheatres);
      setShows(validShows);
      setBookings(validBookings);
      setPayments(validPayments);

      if (validMovies.length > 0 && !newShow.movieId) {
        setNewShow((prev) => ({ ...prev, movieId: validMovies[0].movieId }));
      }
      if (validTheatres.length > 0) {
        const firstTid = validTheatres[0].theatreId;
        if (!selectedTheatreForScreens) {
          setSelectedTheatreForScreens(String(firstTid));
          loadScreensForTheatre(firstTid);
        }
        if (!newShow.theatreId) {
          setNewShow((prev) => ({ ...prev, theatreId: firstTid }));
          const sc = await adminGetScreensByTheatre(firstTid).catch(() => []);
          if (sc && sc.length > 0) {
            setNewShow((prev) => ({ ...prev, screenId: sc[0].screenId }));
          }
        }
      }

      await loadStatistics();
    } catch (err) {
      console.error(err);
    } finally {
      setLoadingData(false);
    }
  };

  const loadStatistics = async (overrideFilter) => {
    const filter = overrideFilter || statsFilter;
    const params = {};
    if (filter.from) params.from = filter.from;
    if (filter.to) params.to = filter.to;
    if (filter.theatreId) params.theatreId = filter.theatreId;
    if (filter.movieId) params.movieId = filter.movieId;

    try {
      const [sm, rev, bkg, top, tp] = await Promise.all([
        adminGetStatsSummary(params).catch(() => null),
        adminGetStatsRevenue(params).catch(() => null),
        adminGetStatsBookings(params).catch(() => null),
        adminGetTopMovies({ ...params, limit: 5 }).catch(() => []),
        adminGetTheatrePerformance(params).catch(() => []),
      ]);
      setStatsSummary(sm);
      setStatsRevenue(rev);
      setStatsBookings(bkg);
      setTopMovies(Array.isArray(top) ? top : []);
      setTheatrePerformance(Array.isArray(tp) ? tp : []);
    } catch (err) {
      console.error(err);
    }
  };

  const loadScreensForTheatre = async (theatreId) => {
    if (!theatreId) {
      setScreens([]);
      return;
    }
    try {
      const sc = await adminGetScreensByTheatre(theatreId).catch(() => []);
      setScreens(Array.isArray(sc) ? sc : []);
    } catch (err) {
      showToast(err.message || 'Failed to fetch screens', 'error');
    }
  };

  useEffect(() => {
    if (admin) {
      loadAdminData();
    }
  }, [admin]);

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

  const handleAddMovie = async (e) => {
    e.preventDefault();
    try {
      await adminAddMovie({
        ...newMovie,
        duration: parseInt(newMovie.duration),
        imdbRating: parseFloat(newMovie.imdbRating),
      });
      showToast('Movie added successfully!', 'success');
      setNewMovie({
        title: '',
        language: 'English',
        genre: 'Action',
        duration: 120,
        releaseDate: new Date().toISOString().split('T')[0],
        imdbRating: 8.0,
        certificate: 'UA_13_PLUS',
        director: '',
        poster: '',
        description: '',
      });
      loadAdminData();
    } catch (err) {
      showToast(err.message || 'Failed to add movie', 'error');
    }
  };

  const handleStartEditMovie = async (movieId) => {
    try {
      const m = await adminGetMovieById(movieId);
      setEditingMovie(m || movies.find((x) => x.movieId === movieId));
    } catch {
      setEditingMovie(movies.find((x) => x.movieId === movieId));
    }
  };

  const handleUpdateMovie = async (e) => {
    e.preventDefault();
    if (!editingMovie) return;
    try {
      await adminUpdateMovie(editingMovie.movieId, {
        ...editingMovie,
        duration: parseInt(editingMovie.duration),
        imdbRating: parseFloat(editingMovie.imdbRating),
      });
      showToast('Movie updated successfully!', 'success');
      setEditingMovie(null);
      loadAdminData();
    } catch (err) {
      showToast(err.message || 'Failed to update movie', 'error');
    }
  };

  const handleDeleteMovie = async (id) => {
    if (!window.confirm('Delete this movie?')) return;
    try {
      await adminDeleteMovie(id);
      showToast('Movie deleted', 'info');
      loadAdminData();
    } catch (err) {
      showToast(err.message || 'Failed to delete movie', 'error');
    }
  };

  const handleOpenCastModal = async (movie) => {
    setCastMovie(movie);
    try {
      const cast = await adminGetMovieCast(movie.movieId);
      setCastList(Array.isArray(cast) ? cast : []);
    } catch {
      setCastList([]);
    }
  };

  const handleAddActor = async (e) => {
    e.preventDefault();
    if (!castMovie || !newActorName.trim()) return;
    try {
      await adminAddActor(castMovie.movieId, { actor: newActorName.trim() });
      showToast('Actor added to cast!', 'success');
      setNewActorName('');
      const updated = await adminGetMovieCast(castMovie.movieId);
      setCastList(Array.isArray(updated) ? updated : []);
    } catch (err) {
      showToast(err.message || 'Failed to add actor', 'error');
    }
  };

  const handleDeleteActor = async (actor) => {
    if (!castMovie) return;
    try {
      await adminDeleteActor(castMovie.movieId, actor);
      showToast('Actor removed from cast', 'info');
      const updated = await adminGetMovieCast(castMovie.movieId);
      setCastList(Array.isArray(updated) ? updated : []);
    } catch (err) {
      showToast(err.message || 'Failed to remove actor', 'error');
    }
  };

  const handleAddTheatre = async (e) => {
    e.preventDefault();
    try {
      await adminAddTheatre(newTheatre);
      showToast('Theatre added successfully!', 'success');
      setNewTheatre({
        name: '',
        city: 'Varanasi',
        state: 'Uttar Pradesh',
        area: '',
        buildingName: '',
        street: '',
        pinCode: '',
      });
      loadAdminData();
    } catch (err) {
      showToast(err.message || 'Failed to add theatre', 'error');
    }
  };

  const handleStartEditTheatre = async (theatreId) => {
    try {
      const th = await adminGetTheatreById(theatreId);
      setEditingTheatre(th || theatres.find((t) => t.theatreId === theatreId));
    } catch {
      setEditingTheatre(theatres.find((t) => t.theatreId === theatreId));
    }
  };

  const handleUpdateTheatre = async (e) => {
    e.preventDefault();
    if (!editingTheatre) return;
    try {
      await adminUpdateTheatre(editingTheatre.theatreId, editingTheatre);
      showToast('Theatre updated successfully!', 'success');
      setEditingTheatre(null);
      loadAdminData();
    } catch (err) {
      showToast(err.message || 'Failed to update theatre', 'error');
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

  const handleAddScreen = async (e) => {
    e.preventDefault();
    if (!selectedTheatreForScreens) {
      showToast('Please select a theatre first', 'error');
      return;
    }
    try {
      await adminAddScreen(parseInt(selectedTheatreForScreens), {
        ...newScreen,
        capacity: parseInt(newScreen.capacity),
      });
      showToast('Screen added successfully!', 'success');
      setNewScreen({ name: 'Screen 1', screenType: 'IMAX', capacity: 60 });
      loadScreensForTheatre(parseInt(selectedTheatreForScreens));
    } catch (err) {
      showToast(err.message || 'Failed to add screen', 'error');
    }
  };

  const handleStartEditScreen = async (screenId) => {
    try {
      const sc = await adminGetScreenById(screenId);
      setEditingScreen(sc || screens.find((s) => s.screenId === screenId));
    } catch {
      setEditingScreen(screens.find((s) => s.screenId === screenId));
    }
  };

  const handleUpdateScreen = async (e) => {
    e.preventDefault();
    if (!editingScreen) return;
    try {
      await adminUpdateScreen(editingScreen.screenId, {
        ...editingScreen,
        capacity: parseInt(editingScreen.capacity),
      });
      showToast('Screen updated successfully!', 'success');
      setEditingScreen(null);
      loadScreensForTheatre(parseInt(selectedTheatreForScreens));
    } catch (err) {
      showToast(err.message || 'Failed to update screen', 'error');
    }
  };

  const handleDeleteScreen = async (screenId) => {
    if (!window.confirm('Delete this screen?')) return;
    try {
      await adminDeleteScreen(screenId);
      showToast('Screen deleted', 'info');
      loadScreensForTheatre(parseInt(selectedTheatreForScreens));
    } catch (err) {
      showToast(err.message || 'Failed to delete screen', 'error');
    }
  };

  const handleTheatreChangeForShow = async (theatreId) => {
    setNewShow((prev) => ({ ...prev, theatreId, screenId: '' }));
    const sc = await adminGetScreensByTheatre(theatreId).catch(() => []);
    if (sc && sc.length > 0) {
      setNewShow((prev) => ({ ...prev, screenId: sc[0].screenId }));
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
        ticketPrice: parseInt(newShow.ticketPrice),
        showStatus: newShow.showStatus,
      });
      showToast('Show scheduled successfully!', 'success');
      loadAdminData();
    } catch (err) {
      showToast(err.message || 'Failed to schedule show', 'error');
    }
  };

  const handleStartEditShow = async (showId) => {
    try {
      const s = await adminGetShowById(showId);
      setEditingShow(s || shows.find((x) => x.showId === showId));
    } catch {
      setEditingShow(shows.find((x) => x.showId === showId));
    }
  };

  const handleUpdateShow = async (e) => {
    e.preventDefault();
    if (!editingShow) return;
    try {
      await adminUpdateShow(editingShow.showId, {
        ...editingShow,
        ticketPrice: parseInt(editingShow.ticketPrice),
      });
      showToast('Show updated successfully!', 'success');
      setEditingShow(null);
      loadAdminData();
    } catch (err) {
      showToast(err.message || 'Failed to update show', 'error');
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

  const handleSearchBookings = async (e) => {
    e.preventDefault();
    try {
      const params = {};
      if (bookingFilter.movie) params.movie = bookingFilter.movie;
      if (bookingFilter.theatre) params.theatre = bookingFilter.theatre;
      const res = await adminSearchBookings(params);
      setBookings(Array.isArray(res) ? res : []);
    } catch (err) {
      showToast(err.message || 'Search bookings failed', 'error');
    }
  };

  const handleResetBookings = async () => {
    setBookingFilter({ movie: '', theatre: '' });
    try {
      const res = await adminGetAllBookings();
      setBookings(Array.isArray(res) ? res : []);
    } catch (err) {
      showToast(err.message || 'Failed to reset bookings', 'error');
    }
  };

  const handleSearchPayments = async (e) => {
    e.preventDefault();
    try {
      const params = {};
      if (paymentFilter.theatre) params.theatre = paymentFilter.theatre;
      if (paymentFilter.paymentStatus) params.paymentStatus = paymentFilter.paymentStatus;
      const res = await adminSearchPayments(params);
      setPayments(Array.isArray(res) ? res : []);
    } catch (err) {
      showToast(err.message || 'Search payments failed', 'error');
    }
  };

  const handleResetPayments = async () => {
    setPaymentFilter({ theatre: '', paymentStatus: '' });
    try {
      const res = await adminGetAllPayments();
      setPayments(Array.isArray(res) ? res : []);
    } catch (err) {
      showToast(err.message || 'Failed to reset payments', 'error');
    }
  };

  const handleViewPaymentDetails = async (paymentId) => {
    try {
      const p = await adminGetPaymentById(paymentId);
      setSelectedPaymentDetail(p || payments.find((x) => x.paymentId === paymentId));
    } catch {
      setSelectedPaymentDetail(payments.find((x) => x.paymentId === paymentId));
    }
  };

  if (!admin) {
    return (
      <div
        className="admin-auth"
        style={{
          maxWidth: '440px',
          margin: '40px auto',
          padding: '28px',
          background: 'var(--bg-secondary)',
          borderRadius: '12px',
          border: '1px solid var(--border-subtle)',
        }}
      >
        <h2 style={{ textAlign: 'center', marginBottom: '16px' }}>🎬 Admin Portal</h2>
        <div className="auth-modal__tabs" style={{ marginBottom: '20px' }}>
          <button
            className={`auth-modal__tab ${authMode === 'login' ? 'auth-modal__tab--active' : ''}`}
            onClick={() => setAuthMode('login')}
          >
            Admin Login
          </button>
          <button
            className={`auth-modal__tab ${authMode === 'register' ? 'auth-modal__tab--active' : ''}`}
            onClick={() => setAuthMode('register')}
          >
            Register Admin
          </button>
        </div>
        <form onSubmit={handleAdminAuth} className="admin-form">
          {authMode === 'register' && (
            <>
              <div style={{ marginBottom: '12px' }}>
                <label className="auth-modal__label">First Name</label>
                <input
                  className="auth-modal__input"
                  placeholder="First Name"
                  value={adminFirstName}
                  onChange={(e) => setAdminFirstName(e.target.value)}
                  required
                />
              </div>
              <div style={{ marginBottom: '12px' }}>
                <label className="auth-modal__label">Last Name</label>
                <input
                  className="auth-modal__input"
                  placeholder="Last Name"
                  value={adminLastName}
                  onChange={(e) => setAdminLastName(e.target.value)}
                  required
                />
              </div>
            </>
          )}
          <div style={{ marginBottom: '12px' }}>
            <label className="auth-modal__label">Admin Email</label>
            <input
              type="email"
              className="auth-modal__input"
              placeholder="admin@galaxycinemas.com"
              value={adminEmail}
              onChange={(e) => setAdminEmail(e.target.value)}
              required
            />
          </div>
          <div style={{ marginBottom: '16px' }}>
            <label className="auth-modal__label">Password</label>
            <input
              type="password"
              className="auth-modal__input"
              placeholder="Admin Password"
              value={adminPassword}
              onChange={(e) => setAdminPassword(e.target.value)}
              required
            />
          </div>
          <button type="submit" className="auth-modal__submit" disabled={authLoading}>
            {authLoading ? 'Verifying...' : authMode === 'login' ? 'Sign In as Admin' : 'Register Admin'}
          </button>
        </form>
      </div>
    );
  }

  return (
    <div className="admin-layout" style={{ maxWidth: '1400px', margin: '0 auto', padding: '24px 16px' }}>
      <div className="inner-sidebar">
        {[
          { tab: 'dashboard', icon: '📊', label: 'Dashboard' },
          { tab: 'movies', icon: '🎬', label: 'Movies' },
          { tab: 'theatres', icon: '🏢', label: 'Theatres' },
          { tab: 'screens', icon: '🖥️', label: 'Screens' },
          { tab: 'shows', icon: '🎭', label: 'Schedule Shows' },
          { tab: 'bookings', icon: '🎫', label: 'Bookings' },
          { tab: 'payments', icon: '💳', label: 'Payments' },
        ].map(({ tab, icon, label }) => (
          <button
            key={tab}
            className={`inner-sidebar__item ${activeTab === tab ? 'inner-sidebar__item--active' : ''}`}
            onClick={() => setActiveTab(tab)}
          >
            <span className="inner-sidebar__item-icon">{icon}</span>
            {label}
          </button>
        ))}

        <hr style={{ borderColor: 'var(--border-subtle)', margin: '16px 0' }} />
        <button className="inner-sidebar__item" onClick={handleAdminLogout} style={{ color: 'var(--danger)' }}>
          <span className="inner-sidebar__item-icon">🚪</span>Exit Admin
        </button>
      </div>

      <div className="admin-content" style={{ padding: '0 16px', minWidth: 0 }}>
        {loadingData ? (
          <div className="spinner" style={{ margin: '60px auto' }}></div>
        ) : (
          <>
            {activeTab === 'dashboard' && (
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px', flexWrap: 'wrap', gap: '12px' }}>
                  <h2>System Overview 📊</h2>
                  <button className="add-btn" onClick={() => loadAdminData()}>Refresh All Data</button>
                </div>

                <div style={{ background: 'var(--bg-secondary)', padding: '16px', borderRadius: '8px', border: '1px solid var(--border-subtle)', marginBottom: '24px' }}>
                  <div style={{ fontSize: '14px', fontWeight: 600, marginBottom: '10px' }}>Filter Statistics</div>
                  <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(160px, 1fr))', gap: '10px', alignItems: 'flex-end' }}>
                    <div>
                      <label className="auth-modal__label">From Date</label>
                      <input
                        type="date"
                        className="auth-modal__input"
                        value={statsFilter.from}
                        onChange={(e) => setStatsFilter({ ...statsFilter, from: e.target.value })}
                      />
                    </div>
                    <div>
                      <label className="auth-modal__label">To Date</label>
                      <input
                        type="date"
                        className="auth-modal__input"
                        value={statsFilter.to}
                        onChange={(e) => setStatsFilter({ ...statsFilter, to: e.target.value })}
                      />
                    </div>
                    <div>
                      <label className="auth-modal__label">Theatre</label>
                      <select
                        className="sidebar__select"
                        value={statsFilter.theatreId}
                        onChange={(e) => setStatsFilter({ ...statsFilter, theatreId: e.target.value })}
                      >
                        <option value="">All Theatres</option>
                        {theatres.map((t) => (
                          <option key={t.theatreId} value={t.theatreId}>{t.name}</option>
                        ))}
                      </select>
                    </div>
                    <div>
                      <label className="auth-modal__label">Movie</label>
                      <select
                        className="sidebar__select"
                        value={statsFilter.movieId}
                        onChange={(e) => setStatsFilter({ ...statsFilter, movieId: e.target.value })}
                      >
                        <option value="">All Movies</option>
                        {movies.map((m) => (
                          <option key={m.movieId} value={m.movieId}>{m.title}</option>
                        ))}
                      </select>
                    </div>
                    <div style={{ display: 'flex', gap: '8px' }}>
                      <button className="add-btn" style={{ flex: 1 }} onClick={() => loadStatistics()}>Apply</button>
                      <button
                        className="cancel-btn"
                        style={{ flex: 1 }}
                        onClick={() => {
                          const reset = { from: '', to: '', theatreId: '', movieId: '' };
                          setStatsFilter(reset);
                          loadStatistics(reset);
                        }}
                      >
                        Reset
                      </button>
                    </div>
                  </div>
                </div>

                <div className="kpi-grid" style={{ marginBottom: '24px' }}>
                  <div className="kpi-card">
                    <div className="kpi-card__value" style={{ color: 'var(--primary)' }}>{movies.length}</div>
                    <div className="kpi-card__label">Total Movies</div>
                  </div>
                  <div className="kpi-card">
                    <div className="kpi-card__value" style={{ color: '#00E5FF' }}>{theatres.length}</div>
                    <div className="kpi-card__label">Active Theatres</div>
                  </div>
                  <div className="kpi-card">
                    <div className="kpi-card__value" style={{ color: '#A78BFA' }}>{shows.length}</div>
                    <div className="kpi-card__label">Scheduled Shows</div>
                  </div>
                  <div className="kpi-card">
                    <div className="kpi-card__value" style={{ color: '#FFD700' }}>
                      {statsSummary?.totalBookings ?? bookings.length}
                    </div>
                    <div className="kpi-card__label">Total Bookings</div>
                  </div>
                  <div className="kpi-card">
                    <div className="kpi-card__value" style={{ color: '#4ADE80' }}>
                      ₹{statsSummary?.totalRevenue ?? 0}
                    </div>
                    <div className="kpi-card__label">Total Revenue</div>
                  </div>
                  <div className="kpi-card">
                    <div className="kpi-card__value" style={{ color: '#4ADE80' }}>
                      {statsSummary?.confirmedBookings ?? 0}
                    </div>
                    <div className="kpi-card__label">Confirmed</div>
                  </div>
                  <div className="kpi-card">
                    <div className="kpi-card__value" style={{ color: 'var(--danger)' }}>
                      {statsSummary?.cancelledBookings ?? 0}
                    </div>
                    <div className="kpi-card__label">Cancelled</div>
                  </div>
                </div>

                {statsRevenue && (
                  <div style={{ background: 'var(--bg-secondary)', padding: '20px', borderRadius: '8px', border: '1px solid var(--border-subtle)', marginBottom: '24px' }}>
                    <h3 style={{ fontSize: '16px', marginBottom: '12px' }}>Revenue Breakdown 💵</h3>
                    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '12px' }}>
                      <div>
                        <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>Online Revenue</div>
                        <div style={{ fontSize: '20px', fontWeight: 700, color: '#4ADE80' }}>₹{statsRevenue.onlineRevenue || 0}</div>
                      </div>
                      <div>
                        <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>Offline Revenue</div>
                        <div style={{ fontSize: '20px', fontWeight: 700, color: '#00E5FF' }}>₹{statsRevenue.offlineRevenue || 0}</div>
                      </div>
                      <div>
                        <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>Ticket Sales Count</div>
                        <div style={{ fontSize: '20px', fontWeight: 700, color: '#A78BFA' }}>{statsRevenue.ticketSalesCount || 0}</div>
                      </div>
                    </div>
                  </div>
                )}

                {statsBookings && (
                  <div style={{ background: 'var(--bg-secondary)', padding: '20px', borderRadius: '8px', border: '1px solid var(--border-subtle)', marginBottom: '24px' }}>
                    <h3 style={{ fontSize: '16px', marginBottom: '12px' }}>Booking Trends 📈</h3>
                    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '12px' }}>
                      <div>
                        <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>Total Tickets Booked</div>
                        <div style={{ fontSize: '20px', fontWeight: 700, color: '#FFD700' }}>{statsBookings.totalTicketsBooked || 0}</div>
                      </div>
                      <div>
                        <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>Completed Bookings</div>
                        <div style={{ fontSize: '20px', fontWeight: 700, color: '#4ADE80' }}>{statsBookings.completedBookings || 0}</div>
                      </div>
                      <div>
                        <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>Cancelled Bookings</div>
                        <div style={{ fontSize: '20px', fontWeight: 700, color: 'var(--danger)' }}>{statsBookings.cancelledBookings || 0}</div>
                      </div>
                    </div>
                  </div>
                )}

                {topMovies.length > 0 && (
                  <div style={{ marginBottom: '24px' }}>
                    <h3 style={{ marginBottom: '12px' }}>Top Performing Movies 🏆</h3>
                    <table className="bookings-table">
                      <thead>
                        <tr>
                          <th>Movie</th>
                          <th>Total Bookings</th>
                          <th>Total Revenue</th>
                        </tr>
                      </thead>
                      <tbody>
                        {topMovies.map((tm, idx) => (
                          <tr key={idx}>
                            <td><strong>{tm.movieTitle || tm.title || `Movie #${tm.movieId}`}</strong></td>
                            <td>{tm.totalBookings || 0}</td>
                            <td>₹{tm.totalRevenue || 0}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}

                {theatrePerformance.length > 0 && (
                  <div>
                    <h3 style={{ marginBottom: '12px' }}>Theatre Performance Breakdown 🏢</h3>
                    <table className="bookings-table">
                      <thead>
                        <tr>
                          <th>Theatre</th>
                          <th>City</th>
                          <th>Total Shows</th>
                          <th>Total Revenue</th>
                          <th>Occupancy Rate</th>
                        </tr>
                      </thead>
                      <tbody>
                        {theatrePerformance.map((tp, idx) => (
                          <tr key={idx}>
                            <td><strong>{tp.theatreName || `Theatre #${tp.theatreId}`}</strong></td>
                            <td>{tp.city || '-'}</td>
                            <td>{tp.totalShows || 0}</td>
                            <td>₹{tp.totalRevenue || 0}</td>
                            <td>{tp.occupancyRate != null ? `${tp.occupancyRate}%` : '-'}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>
            )}

            {activeTab === 'movies' && (
              <div>
                <h2 style={{ marginBottom: '16px' }}>Manage Movies 🎬</h2>

                <form
                  onSubmit={handleAddMovie}
                  style={{
                    background: 'var(--bg-secondary)',
                    padding: '20px',
                    borderRadius: '8px',
                    border: '1px solid var(--border-subtle)',
                    marginBottom: '24px',
                  }}
                >
                  <h3 style={{ fontSize: '16px', marginBottom: '12px' }}>+ Add New Movie</h3>
                  <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '12px' }}>
                    <input
                      className="auth-modal__input"
                      placeholder="Title *"
                      value={newMovie.title}
                      onChange={(e) => setNewMovie({ ...newMovie, title: e.target.value })}
                      required
                    />
                    <input
                      className="auth-modal__input"
                      placeholder="Language *"
                      value={newMovie.language}
                      onChange={(e) => setNewMovie({ ...newMovie, language: e.target.value })}
                      required
                    />
                    <input
                      className="auth-modal__input"
                      placeholder="Genre *"
                      value={newMovie.genre}
                      onChange={(e) => setNewMovie({ ...newMovie, genre: e.target.value })}
                      required
                    />
                    <input
                      type="number"
                      className="auth-modal__input"
                      placeholder="Duration (mins) *"
                      value={newMovie.duration}
                      onChange={(e) => setNewMovie({ ...newMovie, duration: e.target.value })}
                      required
                    />
                    <input
                      type="date"
                      className="auth-modal__input"
                      value={newMovie.releaseDate}
                      onChange={(e) => setNewMovie({ ...newMovie, releaseDate: e.target.value })}
                      required
                    />
                    <input
                      type="number"
                      step="0.1"
                      className="auth-modal__input"
                      placeholder="IMDb Rating"
                      value={newMovie.imdbRating}
                      onChange={(e) => setNewMovie({ ...newMovie, imdbRating: e.target.value })}
                    />
                    <select
                      className="sidebar__select"
                      value={newMovie.certificate}
                      onChange={(e) => setNewMovie({ ...newMovie, certificate: e.target.value })}
                    >
                      <option value="U">U</option>
                      <option value="UA_7_PLUS">UA 7+</option>
                      <option value="UA_13_PLUS">UA 13+</option>
                      <option value="UA_16_PLUS">UA 16+</option>
                      <option value="A">A</option>
                    </select>
                    <input
                      className="auth-modal__input"
                      placeholder="Director"
                      value={newMovie.director}
                      onChange={(e) => setNewMovie({ ...newMovie, director: e.target.value })}
                    />
                    <input
                      className="auth-modal__input"
                      placeholder="Poster URL"
                      value={newMovie.poster}
                      onChange={(e) => setNewMovie({ ...newMovie, poster: e.target.value })}
                    />
                  </div>
                  <textarea
                    className="auth-modal__input"
                    style={{ width: '100%', marginTop: '12px', minHeight: '60px' }}
                    placeholder="Description / Synopsis"
                    value={newMovie.description}
                    onChange={(e) => setNewMovie({ ...newMovie, description: e.target.value })}
                  />
                  <button type="submit" className="add-btn" style={{ marginTop: '12px' }}>Add Movie</button>
                </form>

                {editingMovie && (
                  <form
                    onSubmit={handleUpdateMovie}
                    style={{
                      background: 'var(--bg-surface)',
                      padding: '20px',
                      borderRadius: '8px',
                      border: '2px solid var(--accent)',
                      marginBottom: '24px',
                    }}
                  >
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
                      <h3 style={{ fontSize: '16px', color: 'var(--accent)' }}>✏️ Edit Movie #{editingMovie.movieId}</h3>
                      <button type="button" className="cancel-btn" onClick={() => setEditingMovie(null)}>Close</button>
                    </div>
                    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '12px' }}>
                      <input
                        className="auth-modal__input"
                        placeholder="Title"
                        value={editingMovie.title || ''}
                        onChange={(e) => setEditingMovie({ ...editingMovie, title: e.target.value })}
                        required
                      />
                      <input
                        className="auth-modal__input"
                        placeholder="Language"
                        value={editingMovie.language || ''}
                        onChange={(e) => setEditingMovie({ ...editingMovie, language: e.target.value })}
                        required
                      />
                      <input
                        className="auth-modal__input"
                        placeholder="Genre"
                        value={editingMovie.genre || ''}
                        onChange={(e) => setEditingMovie({ ...editingMovie, genre: e.target.value })}
                        required
                      />
                      <input
                        type="number"
                        className="auth-modal__input"
                        placeholder="Duration"
                        value={editingMovie.duration || ''}
                        onChange={(e) => setEditingMovie({ ...editingMovie, duration: e.target.value })}
                        required
                      />
                      <input
                        type="date"
                        className="auth-modal__input"
                        value={editingMovie.releaseDate || ''}
                        onChange={(e) => setEditingMovie({ ...editingMovie, releaseDate: e.target.value })}
                        required
                      />
                      <input
                        type="number"
                        step="0.1"
                        className="auth-modal__input"
                        placeholder="IMDb"
                        value={editingMovie.imdbRating || ''}
                        onChange={(e) => setEditingMovie({ ...editingMovie, imdbRating: e.target.value })}
                      />
                      <select
                        className="sidebar__select"
                        value={editingMovie.certificate || 'U'}
                        onChange={(e) => setEditingMovie({ ...editingMovie, certificate: e.target.value })}
                      >
                        <option value="U">U</option>
                        <option value="UA_7_PLUS">UA 7+</option>
                        <option value="UA_13_PLUS">UA 13+</option>
                        <option value="UA_16_PLUS">UA 16+</option>
                        <option value="A">A</option>
                      </select>
                      <input
                        className="auth-modal__input"
                        placeholder="Director"
                        value={editingMovie.director || ''}
                        onChange={(e) => setEditingMovie({ ...editingMovie, director: e.target.value })}
                      />
                      <input
                        className="auth-modal__input"
                        placeholder="Poster URL"
                        value={editingMovie.poster || ''}
                        onChange={(e) => setEditingMovie({ ...editingMovie, poster: e.target.value })}
                      />
                    </div>
                    <textarea
                      className="auth-modal__input"
                      style={{ width: '100%', marginTop: '12px', minHeight: '60px' }}
                      placeholder="Description"
                      value={editingMovie.description || ''}
                      onChange={(e) => setEditingMovie({ ...editingMovie, description: e.target.value })}
                    />
                    <div style={{ display: 'flex', gap: '8px', marginTop: '12px' }}>
                      <button type="submit" className="add-btn">Save Changes</button>
                      <button type="button" className="cancel-btn" onClick={() => setEditingMovie(null)}>Cancel</button>
                    </div>
                  </form>
                )}

                {castMovie && (
                  <div
                    style={{
                      background: 'var(--bg-surface)',
                      padding: '20px',
                      borderRadius: '8px',
                      border: '2px solid var(--accent)',
                      marginBottom: '24px',
                    }}
                  >
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
                      <h3 style={{ fontSize: '16px', color: 'var(--accent)' }}>🎭 Cast Members for "{castMovie.title}"</h3>
                      <button type="button" className="cancel-btn" onClick={() => setCastMovie(null)}>Close</button>
                    </div>

                    <form onSubmit={handleAddActor} style={{ display: 'flex', gap: '8px', marginBottom: '16px' }}>
                      <input
                        className="auth-modal__input"
                        placeholder="Actor Name *"
                        value={newActorName}
                        onChange={(e) => setNewActorName(e.target.value)}
                        required
                        style={{ flex: 1 }}
                      />
                      <button type="submit" className="add-btn">Add Actor</button>
                    </form>

                    <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px' }}>
                      {castList.map((c, idx) => {
                        const actorName = typeof c === 'string' ? c : c.actor || c.name || `Actor #${idx + 1}`;
                        return (
                          <div
                            key={idx}
                            style={{
                              background: 'var(--bg-secondary)',
                              padding: '6px 12px',
                              borderRadius: '20px',
                              display: 'flex',
                              alignItems: 'center',
                              gap: '8px',
                              border: '1px solid var(--border-subtle)',
                            }}
                          >
                            <span>{actorName}</span>
                            <button
                              type="button"
                              onClick={() => handleDeleteActor(actorName)}
                              style={{ background: 'none', color: 'var(--danger)', cursor: 'pointer', fontWeight: 700 }}
                            >
                              ✕
                            </button>
                          </div>
                        );
                      })}
                      {castList.length === 0 && (
                        <div style={{ color: 'var(--text-muted)', fontSize: '13px' }}>No cast members found for this movie.</div>
                      )}
                    </div>
                  </div>
                )}

                <table className="bookings-table">
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Title</th>
                      <th>Genre</th>
                      <th>Language</th>
                      <th>Certificate</th>
                      <th>Duration</th>
                      <th>IMDb</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {movies.map((m) => (
                      <tr key={m.movieId}>
                        <td>{m.movieId}</td>
                        <td><strong>{m.title}</strong></td>
                        <td>{m.genre}</td>
                        <td>{m.language}</td>
                        <td>{m.certificate || '-'}</td>
                        <td>{m.duration ? `${m.duration}m` : '-'}</td>
                        <td>⭐ {m.imdbRating || '-'}</td>
                        <td style={{ display: 'flex', gap: '6px', flexWrap: 'wrap' }}>
                          <button className="add-btn" style={{ padding: '4px 8px', fontSize: '11px' }} onClick={() => handleStartEditMovie(m.movieId)}>
                            Edit
                          </button>
                          <button className="add-btn" style={{ padding: '4px 8px', fontSize: '11px', background: '#A78BFA' }} onClick={() => handleOpenCastModal(m)}>
                            Cast
                          </button>
                          <button className="cancel-btn" style={{ padding: '4px 8px', fontSize: '11px' }} onClick={() => handleDeleteMovie(m.movieId)}>
                            Delete
                          </button>
                        </td>
                      </tr>
                    ))}
                    {movies.length === 0 && (
                      <tr>
                        <td colSpan="8" style={{ textAlign: 'center', color: 'var(--text-muted)', padding: '24px' }}>No movies found.</td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            )}

            {activeTab === 'theatres' && (
              <div>
                <h2 style={{ marginBottom: '16px' }}>Manage Theatres 🏢</h2>

                <form
                  onSubmit={handleAddTheatre}
                  style={{
                    background: 'var(--bg-secondary)',
                    padding: '20px',
                    borderRadius: '8px',
                    border: '1px solid var(--border-subtle)',
                    marginBottom: '24px',
                  }}
                >
                  <h3 style={{ fontSize: '16px', marginBottom: '12px' }}>+ Add New Theatre</h3>
                  <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '12px' }}>
                    <input
                      className="auth-modal__input"
                      placeholder="Theatre Name *"
                      value={newTheatre.name}
                      onChange={(e) => setNewTheatre({ ...newTheatre, name: e.target.value })}
                      required
                    />
                    <input
                      className="auth-modal__input"
                      placeholder="Building Name"
                      value={newTheatre.buildingName}
                      onChange={(e) => setNewTheatre({ ...newTheatre, buildingName: e.target.value })}
                    />
                    <input
                      className="auth-modal__input"
                      placeholder="Street *"
                      value={newTheatre.street}
                      onChange={(e) => setNewTheatre({ ...newTheatre, street: e.target.value })}
                      required
                    />
                    <input
                      className="auth-modal__input"
                      placeholder="Area *"
                      value={newTheatre.area}
                      onChange={(e) => setNewTheatre({ ...newTheatre, area: e.target.value })}
                      required
                    />
                    <input
                      className="auth-modal__input"
                      placeholder="City *"
                      value={newTheatre.city}
                      onChange={(e) => setNewTheatre({ ...newTheatre, city: e.target.value })}
                      required
                    />
                    <input
                      className="auth-modal__input"
                      placeholder="State *"
                      value={newTheatre.state}
                      onChange={(e) => setNewTheatre({ ...newTheatre, state: e.target.value })}
                      required
                    />
                    <input
                      className="auth-modal__input"
                      placeholder="Pin Code *"
                      value={newTheatre.pinCode}
                      onChange={(e) => setNewTheatre({ ...newTheatre, pinCode: e.target.value })}
                      required
                    />
                  </div>
                  <button type="submit" className="add-btn" style={{ marginTop: '12px' }}>Add Theatre</button>
                </form>

                {editingTheatre && (
                  <form
                    onSubmit={handleUpdateTheatre}
                    style={{
                      background: 'var(--bg-surface)',
                      padding: '20px',
                      borderRadius: '8px',
                      border: '2px solid var(--accent)',
                      marginBottom: '24px',
                    }}
                  >
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
                      <h3 style={{ fontSize: '16px', color: 'var(--accent)' }}>✏️ Edit Theatre #{editingTheatre.theatreId}</h3>
                      <button type="button" className="cancel-btn" onClick={() => setEditingTheatre(null)}>Close</button>
                    </div>
                    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '12px' }}>
                      <input
                        className="auth-modal__input"
                        placeholder="Theatre Name *"
                        value={editingTheatre.name || ''}
                        onChange={(e) => setEditingTheatre({ ...editingTheatre, name: e.target.value })}
                        required
                      />
                      <input
                        className="auth-modal__input"
                        placeholder="Building Name"
                        value={editingTheatre.buildingName || ''}
                        onChange={(e) => setEditingTheatre({ ...editingTheatre, buildingName: e.target.value })}
                      />
                      <input
                        className="auth-modal__input"
                        placeholder="Street *"
                        value={editingTheatre.street || ''}
                        onChange={(e) => setEditingTheatre({ ...editingTheatre, street: e.target.value })}
                        required
                      />
                      <input
                        className="auth-modal__input"
                        placeholder="Area *"
                        value={editingTheatre.area || ''}
                        onChange={(e) => setEditingTheatre({ ...editingTheatre, area: e.target.value })}
                        required
                      />
                      <input
                        className="auth-modal__input"
                        placeholder="City *"
                        value={editingTheatre.city || ''}
                        onChange={(e) => setEditingTheatre({ ...editingTheatre, city: e.target.value })}
                        required
                      />
                      <input
                        className="auth-modal__input"
                        placeholder="State *"
                        value={editingTheatre.state || ''}
                        onChange={(e) => setEditingTheatre({ ...editingTheatre, state: e.target.value })}
                        required
                      />
                      <input
                        className="auth-modal__input"
                        placeholder="Pin Code *"
                        value={editingTheatre.pinCode || ''}
                        onChange={(e) => setEditingTheatre({ ...editingTheatre, pinCode: e.target.value })}
                        required
                      />
                    </div>
                    <div style={{ display: 'flex', gap: '8px', marginTop: '12px' }}>
                      <button type="submit" className="add-btn">Save Changes</button>
                      <button type="button" className="cancel-btn" onClick={() => setEditingTheatre(null)}>Cancel</button>
                    </div>
                  </form>
                )}

                <div className="theatre-grid">
                  {theatres.map((t) => (
                    <div key={t.theatreId} className="theatre-card">
                      <div className="theatre-card__name">{t.name}</div>
                      <div className="theatre-card__city">
                        📍 {t.area ? `${t.area}, ` : ''}{t.city}, {t.state}
                      </div>
                      {t.buildingName && <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>{t.buildingName}, {t.street}</div>}
                      {t.pinCode && <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>PIN: {t.pinCode}</div>}
                      <div style={{ display: 'flex', gap: '8px', marginTop: '12px' }}>
                        <button className="add-btn" style={{ padding: '6px 12px', fontSize: '12px' }} onClick={() => handleStartEditTheatre(t.theatreId)}>
                          Edit
                        </button>
                        <button className="cancel-btn" onClick={() => handleDeleteTheatre(t.theatreId)}>
                          Delete
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {activeTab === 'screens' && (
              <div>
                <h2 style={{ marginBottom: '16px' }}>Manage Screens 🖥️</h2>

                <div style={{ background: 'var(--bg-secondary)', padding: '16px', borderRadius: '8px', border: '1px solid var(--border-subtle)', marginBottom: '24px' }}>
                  <label className="auth-modal__label">Select Theatre to View Screens</label>
                  <select
                    className="sidebar__select"
                    value={selectedTheatreForScreens}
                    onChange={(e) => {
                      setSelectedTheatreForScreens(e.target.value);
                      loadScreensForTheatre(parseInt(e.target.value));
                    }}
                  >
                    <option value="">-- Select Theatre --</option>
                    {theatres.map((t) => (
                      <option key={t.theatreId} value={t.theatreId}>{t.name} ({t.city})</option>
                    ))}
                  </select>
                </div>

                {selectedTheatreForScreens && (
                  <form
                    onSubmit={handleAddScreen}
                    style={{
                      background: 'var(--bg-secondary)',
                      padding: '20px',
                      borderRadius: '8px',
                      border: '1px solid var(--border-subtle)',
                      marginBottom: '24px',
                    }}
                  >
                    <h3 style={{ fontSize: '16px', marginBottom: '12px' }}>+ Add Screen to Theatre</h3>
                    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '12px' }}>
                      <input
                        className="auth-modal__input"
                        placeholder="Screen Name *"
                        value={newScreen.name}
                        onChange={(e) => setNewScreen({ ...newScreen, name: e.target.value })}
                        required
                      />
                      <select
                        className="sidebar__select"
                        value={newScreen.screenType}
                        onChange={(e) => setNewScreen({ ...newScreen, screenType: e.target.value })}
                      >
                        <option value="TWO_D">2D</option>
                        <option value="THREE_D">3D</option>
                        <option value="IMAX">IMAX</option>
                        <option value="IMAX_3D">IMAX 3D</option>
                        <option value="FOUR_DX">4DX</option>
                      </select>
                      <input
                        type="number"
                        className="auth-modal__input"
                        placeholder="Capacity *"
                        value={newScreen.capacity}
                        onChange={(e) => setNewScreen({ ...newScreen, capacity: e.target.value })}
                        required
                      />
                    </div>
                    <button type="submit" className="add-btn" style={{ marginTop: '12px' }}>Add Screen</button>
                  </form>
                )}

                {editingScreen && (
                  <form
                    onSubmit={handleUpdateScreen}
                    style={{
                      background: 'var(--bg-surface)',
                      padding: '20px',
                      borderRadius: '8px',
                      border: '2px solid var(--accent)',
                      marginBottom: '24px',
                    }}
                  >
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
                      <h3 style={{ fontSize: '16px', color: 'var(--accent)' }}>✏️ Edit Screen #{editingScreen.screenId}</h3>
                      <button type="button" className="cancel-btn" onClick={() => setEditingScreen(null)}>Close</button>
                    </div>
                    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '12px' }}>
                      <input
                        className="auth-modal__input"
                        placeholder="Screen Name"
                        value={editingScreen.name || ''}
                        onChange={(e) => setEditingScreen({ ...editingScreen, name: e.target.value })}
                        required
                      />
                      <select
                        className="sidebar__select"
                        value={editingScreen.screenType || 'TWO_D'}
                        onChange={(e) => setEditingScreen({ ...editingScreen, screenType: e.target.value })}
                      >
                        <option value="TWO_D">2D</option>
                        <option value="THREE_D">3D</option>
                        <option value="IMAX">IMAX</option>
                        <option value="IMAX_3D">IMAX 3D</option>
                        <option value="FOUR_DX">4DX</option>
                      </select>
                      <input
                        type="number"
                        className="auth-modal__input"
                        placeholder="Capacity"
                        value={editingScreen.capacity || ''}
                        onChange={(e) => setEditingScreen({ ...editingScreen, capacity: e.target.value })}
                        required
                      />
                    </div>
                    <div style={{ display: 'flex', gap: '8px', marginTop: '12px' }}>
                      <button type="submit" className="add-btn">Save Changes</button>
                      <button type="button" className="cancel-btn" onClick={() => setEditingScreen(null)}>Cancel</button>
                    </div>
                  </form>
                )}

                <table className="bookings-table">
                  <thead>
                    <tr>
                      <th>Screen ID</th>
                      <th>Name</th>
                      <th>Screen Type</th>
                      <th>Capacity</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {screens.map((sc) => (
                      <tr key={sc.screenId}>
                        <td>#{sc.screenId}</td>
                        <td><strong>{sc.name}</strong></td>
                        <td>{sc.screenType}</td>
                        <td>{sc.capacity || '-'} seats</td>
                        <td style={{ display: 'flex', gap: '8px' }}>
                          <button className="add-btn" style={{ padding: '4px 8px', fontSize: '11px' }} onClick={() => handleStartEditScreen(sc.screenId)}>
                            Edit
                          </button>
                          <button className="cancel-btn" style={{ padding: '4px 8px', fontSize: '11px' }} onClick={() => handleDeleteScreen(sc.screenId)}>
                            Delete
                          </button>
                        </td>
                      </tr>
                    ))}
                    {screens.length === 0 && (
                      <tr>
                        <td colSpan="5" style={{ textAlign: 'center', color: 'var(--text-muted)', padding: '24px' }}>
                          {selectedTheatreForScreens ? 'No screens found for this theatre.' : 'Please select a theatre above.'}
                        </td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            )}

            {activeTab === 'shows' && (
              <div>
                <h2 style={{ marginBottom: '16px' }}>Schedule Shows 🎭</h2>

                <form
                  onSubmit={handleAddShow}
                  style={{
                    background: 'var(--bg-secondary)',
                    padding: '20px',
                    borderRadius: '8px',
                    border: '1px solid var(--border-subtle)',
                    marginBottom: '24px',
                  }}
                >
                  <h3 style={{ fontSize: '16px', marginBottom: '12px' }}>+ Schedule a New Show</h3>
                  <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '12px' }}>
                    <div>
                      <label className="auth-modal__label">Select Movie *</label>
                      <select
                        className="sidebar__select"
                        value={newShow.movieId}
                        onChange={(e) => setNewShow({ ...newShow, movieId: e.target.value })}
                        required
                      >
                        <option value="">-- Select Movie --</option>
                        {movies.map((m) => (
                          <option key={m.movieId} value={m.movieId}>{m.title}</option>
                        ))}
                      </select>
                    </div>

                    <div>
                      <label className="auth-modal__label">Select Theatre *</label>
                      <select
                        className="sidebar__select"
                        value={newShow.theatreId}
                        onChange={(e) => handleTheatreChangeForShow(parseInt(e.target.value))}
                        required
                      >
                        <option value="">-- Select Theatre --</option>
                        {theatres.map((t) => (
                          <option key={t.theatreId} value={t.theatreId}>{t.name} ({t.city})</option>
                        ))}
                      </select>
                    </div>

                    <div>
                      <label className="auth-modal__label">Select Screen *</label>
                      <select
                        className="sidebar__select"
                        value={newShow.screenId}
                        onChange={(e) => setNewShow({ ...newShow, screenId: e.target.value })}
                        required
                      >
                        <option value="">-- Select Screen --</option>
                        {screens.map((sc) => (
                          <option key={sc.screenId} value={sc.screenId}>
                            {sc.name || `Screen ${sc.screenId}`} ({sc.screenType})
                          </option>
                        ))}
                      </select>
                    </div>

                    <div>
                      <label className="auth-modal__label">Show Date *</label>
                      <input
                        type="date"
                        className="auth-modal__input"
                        value={newShow.showDate}
                        onChange={(e) => setNewShow({ ...newShow, showDate: e.target.value })}
                        required
                      />
                    </div>

                    <div>
                      <label className="auth-modal__label">Show Time *</label>
                      <input
                        type="time"
                        className="auth-modal__input"
                        value={newShow.showTime}
                        onChange={(e) => setNewShow({ ...newShow, showTime: e.target.value })}
                        required
                      />
                    </div>

                    <div>
                      <label className="auth-modal__label">Ticket Price (₹) *</label>
                      <input
                        type="number"
                        className="auth-modal__input"
                        value={newShow.ticketPrice}
                        onChange={(e) => setNewShow({ ...newShow, ticketPrice: e.target.value })}
                        required
                      />
                    </div>

                    <div>
                      <label className="auth-modal__label">Status</label>
                      <select
                        className="sidebar__select"
                        value={newShow.showStatus}
                        onChange={(e) => setNewShow({ ...newShow, showStatus: e.target.value })}
                      >
                        <option value="SCHEDULED">SCHEDULED</option>
                        <option value="ONGOING">ONGOING</option>
                        <option value="COMPLETED">COMPLETED</option>
                        <option value="CANCELLED">CANCELLED</option>
                      </select>
                    </div>
                  </div>
                  <button type="submit" className="add-btn" style={{ marginTop: '16px' }}>Schedule Show</button>
                </form>

                {editingShow && (
                  <form
                    onSubmit={handleUpdateShow}
                    style={{
                      background: 'var(--bg-surface)',
                      padding: '20px',
                      borderRadius: '8px',
                      border: '2px solid var(--accent)',
                      marginBottom: '24px',
                    }}
                  >
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
                      <h3 style={{ fontSize: '16px', color: 'var(--accent)' }}>✏️ Edit Show #{editingShow.showId}</h3>
                      <button type="button" className="cancel-btn" onClick={() => setEditingShow(null)}>Close</button>
                    </div>
                    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '12px' }}>
                      <div>
                        <label className="auth-modal__label">Show Date</label>
                        <input
                          type="date"
                          className="auth-modal__input"
                          value={editingShow.showDate || ''}
                          onChange={(e) => setEditingShow({ ...editingShow, showDate: e.target.value })}
                          required
                        />
                      </div>
                      <div>
                        <label className="auth-modal__label">Show Time</label>
                        <input
                          type="time"
                          className="auth-modal__input"
                          value={editingShow.showTime ? String(editingShow.showTime).slice(0, 5) : ''}
                          onChange={(e) => setEditingShow({ ...editingShow, showTime: e.target.value })}
                          required
                        />
                      </div>
                      <div>
                        <label className="auth-modal__label">Ticket Price (₹)</label>
                        <input
                          type="number"
                          className="auth-modal__input"
                          value={editingShow.ticketPrice || ''}
                          onChange={(e) => setEditingShow({ ...editingShow, ticketPrice: e.target.value })}
                          required
                        />
                      </div>
                      <div>
                        <label className="auth-modal__label">Status</label>
                        <select
                          className="sidebar__select"
                          value={editingShow.showStatus || 'SCHEDULED'}
                          onChange={(e) => setEditingShow({ ...editingShow, showStatus: e.target.value })}
                        >
                          <option value="SCHEDULED">SCHEDULED</option>
                          <option value="ONGOING">ONGOING</option>
                          <option value="COMPLETED">COMPLETED</option>
                          <option value="CANCELLED">CANCELLED</option>
                        </select>
                      </div>
                    </div>
                    <div style={{ display: 'flex', gap: '8px', marginTop: '12px' }}>
                      <button type="submit" className="add-btn">Save Changes</button>
                      <button type="button" className="cancel-btn" onClick={() => setEditingShow(null)}>Cancel</button>
                    </div>
                  </form>
                )}

                <h3 style={{ marginBottom: '12px' }}>Existing Shows</h3>
                <table className="bookings-table">
                  <thead>
                    <tr>
                      <th>Show ID</th>
                      <th>Movie</th>
                      <th>Date</th>
                      <th>Time</th>
                      <th>Type</th>
                      <th>Price</th>
                      <th>Status</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {shows.map((s) => {
                      const sid = s.showId || s.id;
                      return (
                        <tr key={sid}>
                          <td>#{sid}</td>
                          <td>{s.movieTitle || s.movie?.title || `Movie #${s.movieId}`}</td>
                          <td>{s.showDate || '-'}</td>
                          <td>{s.showTime ? String(s.showTime).slice(0, 5) : '-'}</td>
                          <td>{s.screenType || s.screen?.screenType || '-'}</td>
                          <td>₹{s.ticketPrice || '-'}</td>
                          <td>
                            <span className={`badge badge--${s.showStatus === 'CANCELLED' ? 'cancelled' : 'confirmed'}`}>
                              {s.showStatus || 'SCHEDULED'}
                            </span>
                          </td>
                          <td style={{ display: 'flex', gap: '6px' }}>
                            <button className="add-btn" style={{ padding: '4px 8px', fontSize: '11px' }} onClick={() => handleStartEditShow(sid)}>
                              Edit
                            </button>
                            <button className="cancel-btn" style={{ padding: '4px 8px', fontSize: '11px' }} onClick={() => handleDeleteShow(sid)}>
                              Delete
                            </button>
                          </td>
                        </tr>
                      );
                    })}
                    {shows.length === 0 && (
                      <tr>
                        <td colSpan="8" style={{ textAlign: 'center', color: 'var(--text-muted)', padding: '24px' }}>No shows found.</td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            )}

            {activeTab === 'bookings' && (
              <div>
                <h2 style={{ marginBottom: '16px' }}>All System Bookings 🎫</h2>

                <form
                  onSubmit={handleSearchBookings}
                  style={{
                    background: 'var(--bg-secondary)',
                    padding: '16px',
                    borderRadius: '8px',
                    border: '1px solid var(--border-subtle)',
                    marginBottom: '24px',
                    display: 'flex',
                    gap: '12px',
                    flexWrap: 'wrap',
                    alignItems: 'flex-end',
                  }}
                >
                  <div style={{ minWidth: '180px', flex: 1 }}>
                    <label className="auth-modal__label">Filter by Movie</label>
                    <select
                      className="sidebar__select"
                      value={bookingFilter.movie}
                      onChange={(e) => setBookingFilter({ ...bookingFilter, movie: e.target.value })}
                    >
                      <option value="">All Movies</option>
                      {movies.map((m) => (
                        <option key={m.movieId} value={m.movieId}>{m.title}</option>
                      ))}
                    </select>
                  </div>
                  <div style={{ minWidth: '180px', flex: 1 }}>
                    <label className="auth-modal__label">Filter by Theatre</label>
                    <select
                      className="sidebar__select"
                      value={bookingFilter.theatre}
                      onChange={(e) => setBookingFilter({ ...bookingFilter, theatre: e.target.value })}
                    >
                      <option value="">All Theatres</option>
                      {theatres.map((t) => (
                        <option key={t.theatreId} value={t.theatreId}>{t.name}</option>
                      ))}
                    </select>
                  </div>
                  <div style={{ display: 'flex', gap: '8px' }}>
                    <button type="submit" className="add-btn">Search Bookings</button>
                    <button type="button" className="cancel-btn" onClick={handleResetBookings}>Reset</button>
                  </div>
                </form>

                <table className="bookings-table">
                  <thead>
                    <tr>
                      <th>Booking ID</th>
                      <th>Movie</th>
                      <th>Theatre</th>
                      <th>Date</th>
                      <th>Seats</th>
                      <th>Amount</th>
                      <th>Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    {bookings.map((b) => {
                      const bId = b.bookingId || b.id || '-';
                      const movieTitle = b.movieName || b.movie?.title || b.movieTitle || '-';
                      const theatreName = b.theatreName || b.theatre?.name || '-';
                      const showDate = b.show?.showDate || b.showDate || '';
                      const seatCount = b.totalSeatCount || b.seats?.length || b.seatIds?.length || '-';
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
                            <span className={`badge badge--${status === 'CANCELLED' ? 'cancelled' : 'confirmed'}`}>
                              {status}
                            </span>
                          </td>
                        </tr>
                      );
                    })}
                    {bookings.length === 0 && (
                      <tr>
                        <td colSpan="7" style={{ textAlign: 'center', color: 'var(--text-muted)', padding: '24px' }}>
                          No bookings found.
                        </td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            )}

            {activeTab === 'payments' && (
              <div>
                <h2 style={{ marginBottom: '16px' }}>Payment Transactions 💳</h2>

                <form
                  onSubmit={handleSearchPayments}
                  style={{
                    background: 'var(--bg-secondary)',
                    padding: '16px',
                    borderRadius: '8px',
                    border: '1px solid var(--border-subtle)',
                    marginBottom: '24px',
                    display: 'flex',
                    gap: '12px',
                    flexWrap: 'wrap',
                    alignItems: 'flex-end',
                  }}
                >
                  <div style={{ minWidth: '180px', flex: 1 }}>
                    <label className="auth-modal__label">Filter by Theatre</label>
                    <select
                      className="sidebar__select"
                      value={paymentFilter.theatre}
                      onChange={(e) => setPaymentFilter({ ...paymentFilter, theatre: e.target.value })}
                    >
                      <option value="">All Theatres</option>
                      {theatres.map((t) => (
                        <option key={t.theatreId} value={t.theatreId}>{t.name}</option>
                      ))}
                    </select>
                  </div>
                  <div style={{ minWidth: '180px', flex: 1 }}>
                    <label className="auth-modal__label">Payment Status</label>
                    <select
                      className="sidebar__select"
                      value={paymentFilter.paymentStatus}
                      onChange={(e) => setPaymentFilter({ ...paymentFilter, paymentStatus: e.target.value })}
                    >
                      <option value="">All Statuses</option>
                      <option value="SUCCESS">SUCCESS</option>
                      <option value="PENDING">PENDING</option>
                      <option value="FAILED">FAILED</option>
                      <option value="REFUNDED">REFUNDED</option>
                    </select>
                  </div>
                  <div style={{ display: 'flex', gap: '8px' }}>
                    <button type="submit" className="add-btn">Filter Payments</button>
                    <button type="button" className="cancel-btn" onClick={handleResetPayments}>Reset</button>
                  </div>
                </form>

                {selectedPaymentDetail && (
                  <div
                    style={{
                      background: 'var(--bg-surface)',
                      padding: '20px',
                      borderRadius: '8px',
                      border: '2px solid var(--accent)',
                      marginBottom: '24px',
                    }}
                  >
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
                      <h3 style={{ fontSize: '16px', color: 'var(--accent)' }}>Payment Details #{selectedPaymentDetail.paymentId}</h3>
                      <button type="button" className="cancel-btn" onClick={() => setSelectedPaymentDetail(null)}>Close</button>
                    </div>
                    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '12px' }}>
                      <div>
                        <div style={{ fontSize: '11px', color: 'var(--text-muted)' }}>Transaction ID</div>
                        <div style={{ fontWeight: 600 }}>{selectedPaymentDetail.transactionId || '-'}</div>
                      </div>
                      <div>
                        <div style={{ fontSize: '11px', color: 'var(--text-muted)' }}>Method</div>
                        <div style={{ fontWeight: 600 }}>{selectedPaymentDetail.paymentMethod || '-'}</div>
                      </div>
                      <div>
                        <div style={{ fontSize: '11px', color: 'var(--text-muted)' }}>Amount</div>
                        <div style={{ fontWeight: 600, color: '#4ADE80' }}>₹{selectedPaymentDetail.paymentAmount || 0}</div>
                      </div>
                      <div>
                        <div style={{ fontSize: '11px', color: 'var(--text-muted)' }}>Date & Time</div>
                        <div style={{ fontWeight: 600 }}>
                          {selectedPaymentDetail.paymentDateTime ? String(selectedPaymentDetail.paymentDateTime).replace('T', ' ').slice(0, 19) : '-'}
                        </div>
                      </div>
                      <div>
                        <div style={{ fontSize: '11px', color: 'var(--text-muted)' }}>Status</div>
                        <div>
                          <span className={`badge badge--${selectedPaymentDetail.paymentStatus === 'SUCCESS' ? 'confirmed' : 'cancelled'}`}>
                            {selectedPaymentDetail.paymentStatus || 'SUCCESS'}
                          </span>
                        </div>
                      </div>
                    </div>
                  </div>
                )}

                <table className="bookings-table">
                  <thead>
                    <tr>
                      <th>Payment ID</th>
                      <th>Transaction ID</th>
                      <th>Method</th>
                      <th>Amount</th>
                      <th>Date / Time</th>
                      <th>Status</th>
                      <th>Action</th>
                    </tr>
                  </thead>
                  <tbody>
                    {payments.map((p) => (
                      <tr key={p.paymentId}>
                        <td>#{p.paymentId}</td>
                        <td>{p.transactionId || '-'}</td>
                        <td>{p.paymentMethod}</td>
                        <td>₹{p.paymentAmount || 0}</td>
                        <td>{p.paymentDateTime ? String(p.paymentDateTime).replace('T', ' ').slice(0, 19) : '-'}</td>
                        <td>
                          <span className={`badge badge--${p.paymentStatus === 'SUCCESS' ? 'confirmed' : 'cancelled'}`}>
                            {p.paymentStatus || 'SUCCESS'}
                          </span>
                        </td>
                        <td>
                          <button
                            className="add-btn"
                            style={{ padding: '4px 8px', fontSize: '11px' }}
                            onClick={() => handleViewPaymentDetails(p.paymentId)}
                          >
                            Details
                          </button>
                        </td>
                      </tr>
                    ))}
                    {payments.length === 0 && (
                      <tr>
                        <td colSpan="7" style={{ textAlign: 'center', color: 'var(--text-muted)', padding: '24px' }}>
                          No payment records found.
                        </td>
                      </tr>
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
