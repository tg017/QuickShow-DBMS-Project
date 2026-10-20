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

const formatScreenType = (st) => {
  if (!st) return '2D';
  return st
    .replace('TWO_D', '2D')
    .replace('THREE_D', '3D')
    .replace('IMAX_3D', 'IMAX 3D')
    .replace('IMAX_2D', 'IMAX 2D')
    .replace('FOUR_DX', '4DX')
    .replace(/_/g, ' ');
};

const formatCertificate = (cert) => {
  if (!cert) return '-';
  return cert
    .replace('UA_16_PLUS', 'UA 16+')
    .replace('UA_7_PLUS', 'UA 7+')
    .replace('UA_13_PLUS', 'UA 13+')
    .replace('UA_18_PLUS', 'A')
    .replace('U_A', 'UA')
    .replace(/_/g, ' ');
};

const getScreenRows = (screenObj) => {
  if (screenObj?.rows && screenObj.rows.length > 0) return screenObj.rows;
  if (screenObj?.screenId) {
    try {
      const cached = localStorage.getItem('quickshow_screen_rows_' + screenObj.screenId);
      if (cached) {
        const parsed = JSON.parse(cached);
        if (Array.isArray(parsed) && parsed.length > 0) return parsed;
      }
    } catch {}
  }
  const cap = parseInt(screenObj?.capacity) || 60;
  const count = Math.max(1, Math.min(26, Math.ceil(cap / 10)));
  const basePer = Math.floor(cap / count);
  let rem = cap % count;
  const rows = [];
  for (let i = 0; i < count; i++) {
    const extra = rem > 0 ? 1 : 0;
    if (rem > 0) rem--;
    rows.push({
      rowNo: String.fromCharCode(65 + i),
      seatCount: basePer + extra,
    });
  }
  return rows;
};

const todayLocal = () => {
  const d = new Date();
  return new Date(d.getTime() - d.getTimezoneOffset() * 60000).toISOString().split('T')[0];
};

const emptyMovie = () => ({
  title: '',
  language: 'English',
  genre: 'Action',
  duration: 120,
  releaseDate: todayLocal(),
  imdbRating: 8.0,
  certificate: 'UA_13_PLUS',
  director: '',
  poster: '',
  description: '',
});

export default function Admin() {
  const { admin, setAdmin, setUser, showToast } = useApp();
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
  const [showFormScreens, setShowFormScreens] = useState([]);
  const [allScreens, setAllScreens] = useState([]);
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

  const [newMovie, setNewMovie] = useState(emptyMovie());
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
  });
  const [screenRows, setScreenRows] = useState([
    { rowNo: 'A', seatCount: 10 },
    { rowNo: 'B', seatCount: 10 },
    { rowNo: 'C', seatCount: 10 },
    { rowNo: 'D', seatCount: 10 },
  ]);
  const [editingScreen, setEditingScreen] = useState(null);
  const [editScreenRows, setEditScreenRows] = useState([]);
  const [viewingScreenLayout, setViewingScreenLayout] = useState(null);

  const [newShow, setNewShow] = useState({
    movieId: '',
    theatreId: '',
    screenId: '',
    showDate: todayLocal(),
    showTime: '18:00',
    ticketPrice: 250,
    showStatus: 'SCHEDULED',
  });
  const [editingShow, setEditingShow] = useState(null);

  const [bookingFilter, setBookingFilter] = useState({ movie: '', theatre: '' });
  const [paymentFilter, setPaymentFilter] = useState({ theatre: '', paymentStatus: '' });
  const [selectedPaymentDetail, setSelectedPaymentDetail] = useState(null);

  const handleAdminLogout = () => {
    setAdmin(null);
    sessionStorage.removeItem('quickshow_admin');
    localStorage.removeItem('quickshow_admin');
    showToast('Admin logged out', 'info');
  };

  const handleLoadError = (err) => {
    if (err.status === 401 || err.status === 403 || err.message?.includes('expired') || err.message?.includes('not signed in')) {
      setAdmin(null);
      sessionStorage.removeItem('quickshow_admin');
      showToast('Session expired. Please login again.', 'error');
    } else {
      showToast(err.message || 'Failed to load data', 'error');
    }
  };
  const safeCall = async (fn, fallback) => {
    try {
      return await fn();
    } catch (err) {
      handleLoadError(err);
      return fallback;
    }
  };

  const loadShowFormScreens = async (theatreId) => {
    const sc = await safeCall(() => adminGetScreensByTheatre(theatreId), []);
    const list = Array.isArray(sc) ? sc : [];
    setShowFormScreens(list);
    return list;
  };

  const loadScreensForTheatre = async (theatreId) => {
    if (!theatreId) {
      setScreens([]);
      return;
    }
    const sc = await safeCall(() => adminGetScreensByTheatre(theatreId), []);
    const list = (Array.isArray(sc) ? sc : []).map((s) => ({
      ...s,
      rows: getScreenRows(s),
    }));
    setScreens(list);
  };

  const loadStatistics = async (overrideFilter) => {
    const filter = overrideFilter || statsFilter;

    if (filter.from && filter.to && filter.from > filter.to) {
      showToast("'From' date cannot be after 'To' date", 'warning');
      return;
    }

    const params = {};
    if (filter.from) params.from = filter.from;
    if (filter.to) params.to = filter.to;
    if (filter.theatreId) params.theatreId = filter.theatreId;
    if (filter.movieId) params.movieId = filter.movieId;

    const [sm, rev, bkg, top, tp] = await Promise.all([
      safeCall(() => adminGetStatsSummary(params), null),
      safeCall(() => adminGetStatsRevenue(params), null),
      safeCall(() => adminGetStatsBookings(params), null),
      safeCall(() => adminGetTopMovies({ ...params, limit: 5 }), []),
      safeCall(() => adminGetTheatrePerformance(params), []),
    ]);
    setStatsSummary(sm);
    setStatsRevenue(rev);
    setStatsBookings(bkg);
    setTopMovies(Array.isArray(top) ? top : []);
    setTheatrePerformance(Array.isArray(tp) ? tp : []);
  };

  const loadAdminData = async (overrideFilter) => {
    setLoadingData(true);
    try {
      const [m, t, s, b, p] = await Promise.all([
        safeCall(adminGetAllMovies, []),
        safeCall(adminGetAllTheatres, []),
        safeCall(adminGetAllShows, []),
        safeCall(adminGetAllBookings, []),
        safeCall(adminGetAllPayments, []),
      ]);

      const validMovies = Array.isArray(m) ? m : [];
      const detailedMovies = await Promise.all(
        validMovies.map(async (movie) => {
          if (movie.language && movie.duration && movie.certificate) return movie;
          try {
            const details = await adminGetMovieById(movie.movieId);
            return details ? { ...movie, ...details } : movie;
          } catch {
            return movie;
          }
        })
      );
      const validTheatres = Array.isArray(t) ? t : [];

      setMovies(detailedMovies);
      setTheatres(validTheatres);
      setShows(Array.isArray(s) ? s : []);
      setBookings(Array.isArray(b) ? b : []);
      setPayments(Array.isArray(p) ? p : []);
      const screenPromises = (Array.isArray(t) ? t : []).map((th) => adminGetScreensByTheatre(th.theatreId).catch(() => []));
      const screenLists = await Promise.all(screenPromises);
      setAllScreens(screenLists.flat());

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
          const sc = await loadShowFormScreens(firstTid);
          setNewShow((prev) => ({
            ...prev,
            theatreId: firstTid,
            screenId: sc.length > 0 ? sc[0].screenId : '',
          }));
        }
      }

      await loadStatistics(overrideFilter);
    } finally {
      setLoadingData(false);
    }
  };

  useEffect(() => {
    if (admin) {
      loadAdminData();
    }
  }, [admin]);

  const openTab = (tab) => {
    setActiveTab(tab);
    if (tab === 'shows' && newShow.theatreId) {
      loadShowFormScreens(newShow.theatreId);
    }
  };

  const handleAdminAuth = async (e) => {
    e.preventDefault();
    setAuthLoading(true);
    try {
      if (authMode === 'login') {
        const res = await adminLogin({ email: adminEmail, password: adminPassword });
        setAdmin(res);
        sessionStorage.setItem('quickshow_admin', JSON.stringify(res));
        localStorage.removeItem('quickshow_admin');
        setUser(null);
        sessionStorage.removeItem('quickshow_user');
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

  const handleAddMovie = async (e) => {
    e.preventDefault();
    try {
      await adminAddMovie({
        ...newMovie,
        duration: parseInt(newMovie.duration),
        imdbRating: parseFloat(newMovie.imdbRating),
      });
      showToast('Movie added successfully!', 'success');
      setNewMovie(emptyMovie());
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

      const deletedId = String(id);
      if (String(newShow.movieId) === deletedId) {
        setNewShow((prev) => ({ ...prev, movieId: '' }));
      }
      if (bookingFilter.movie === deletedId) {
        setBookingFilter((prev) => ({ ...prev, movie: '' }));
      }
      let filter = statsFilter;
      if (statsFilter.movieId === deletedId) {
        filter = { ...statsFilter, movieId: '' };
        setStatsFilter(filter);
      }
      loadAdminData(filter);
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

      const deletedId = String(id);
      if (selectedTheatreForScreens === deletedId) {
        setSelectedTheatreForScreens('');
        setScreens([]);
      }
      if (String(newShow.theatreId) === deletedId) {
        setNewShow((prev) => ({ ...prev, theatreId: '', screenId: '' }));
        setShowFormScreens([]);
      }
      if (bookingFilter.theatre === deletedId) {
        setBookingFilter((prev) => ({ ...prev, theatre: '' }));
      }
      if (paymentFilter.theatre === deletedId) {
        setPaymentFilter((prev) => ({ ...prev, theatre: '' }));
      }
      let filter = statsFilter;
      if (statsFilter.theatreId === deletedId) {
        filter = { ...statsFilter, theatreId: '' };
        setStatsFilter(filter);
      }
      loadAdminData(filter);
    } catch (err) {
      showToast(err.message || 'Failed to delete theatre', 'error');
    }
  };

  const handleRowCountChange = (count) => {
    const num = Math.max(1, Math.min(26, parseInt(count) || 1));
    setScreenRows((prev) => {
      const next = [];
      for (let i = 0; i < num; i++) {
        const char = String.fromCharCode(65 + i);
        const existing = prev.find((r) => r.rowNo === char);
        next.push(existing || { rowNo: char, seatCount: 10 });
      }
      return next;
    });
  };

  const handleRowSeatChange = (index, seatCount) => {
    setScreenRows((prev) => {
      const updated = [...prev];
      updated[index] = { ...updated[index], seatCount: Math.max(1, parseInt(seatCount) || 1) };
      return updated;
    });
  };

  const handleAddRow = () => {
    setScreenRows((prev) => {
      if (prev.length >= 26) return prev;
      const nextChar = String.fromCharCode(65 + prev.length);
      return [...prev, { rowNo: nextChar, seatCount: 10 }];
    });
  };

  const handleRemoveRow = (index) => {
    setScreenRows((prev) => {
      if (prev.length <= 1) return prev;
      return prev.filter((_, i) => i !== index);
    });
  };

  const handleAddScreen = async (e) => {
    e.preventDefault();
    if (!selectedTheatreForScreens) {
      showToast('Please select a theatre first', 'error');
      return;
    }
    const totalCapacity = screenRows.reduce((sum, r) => sum + (parseInt(r.seatCount) || 0), 0);
    if (totalCapacity <= 0) {
      showToast('Total capacity must be greater than 0', 'error');
      return;
    }
    try {
      const payloadRows = screenRows.map((r) => ({
        rowNo: r.rowNo.trim().toUpperCase(),
        seatCount: parseInt(r.seatCount) || 1,
      }));
      const created = await adminAddScreen(parseInt(selectedTheatreForScreens), {
        name: newScreen.name,
        screenType: newScreen.screenType,
        capacity: totalCapacity,
        rows: payloadRows,
      });
      if (created?.screenId) {
        localStorage.setItem('quickshow_screen_rows_' + created.screenId, JSON.stringify(payloadRows));
      }
      showToast('Screen added successfully!', 'success');
      setNewScreen({ name: `Screen ${(screens?.length || 0) + 2}`, screenType: 'IMAX' });
      setScreenRows([
        { rowNo: 'A', seatCount: 10 },
        { rowNo: 'B', seatCount: 10 },
        { rowNo: 'C', seatCount: 10 },
        { rowNo: 'D', seatCount: 10 },
      ]);
      loadScreensForTheatre(parseInt(selectedTheatreForScreens));
    } catch (err) {
      showToast(err.message || 'Failed to add screen', 'error');
    }
  };

  const handleStartEditScreen = async (screenId) => {
    let sc = null;
    try {
      sc = await adminGetScreenById(screenId);
    } catch {
      sc = screens.find((s) => s.screenId === screenId);
    }
    const screenObj = sc || screens.find((s) => s.screenId === screenId);
    if (!screenObj) return;
    setEditingScreen(screenObj);
    setEditScreenRows(getScreenRows(screenObj));
  };

  const handleEditRowCountChange = (count) => {
    const num = Math.max(1, Math.min(26, parseInt(count) || 1));
    setEditScreenRows((prev) => {
      const next = [];
      for (let i = 0; i < num; i++) {
        const char = String.fromCharCode(65 + i);
        const existing = prev.find((r) => r.rowNo === char);
        next.push(existing || { rowNo: char, seatCount: 10 });
      }
      return next;
    });
  };

  const handleEditRowSeatChange = (index, seatCount) => {
    setEditScreenRows((prev) => {
      const updated = [...prev];
      updated[index] = { ...updated[index], seatCount: Math.max(1, parseInt(seatCount) || 1) };
      return updated;
    });
  };

  const handleAddEditRow = () => {
    setEditScreenRows((prev) => {
      if (prev.length >= 26) return prev;
      const nextChar = String.fromCharCode(65 + prev.length);
      return [...prev, { rowNo: nextChar, seatCount: 10 }];
    });
  };

  const handleRemoveEditRow = (index) => {
    setEditScreenRows((prev) => {
      if (prev.length <= 1) return prev;
      return prev.filter((_, i) => i !== index);
    });
  };

  const handleUpdateScreen = async (e) => {
    e.preventDefault();
    if (!editingScreen) return;
    const totalCapacity = editScreenRows.reduce((sum, r) => sum + (parseInt(r.seatCount) || 0), 0);
    if (totalCapacity <= 0) {
      showToast('Total capacity must be greater than 0', 'error');
      return;
    }
    try {
      const payloadRows = editScreenRows.map((r) => ({
        rowNo: r.rowNo.trim().toUpperCase(),
        seatCount: parseInt(r.seatCount) || 1,
      }));
      await adminUpdateScreen(editingScreen.screenId, {
        name: editingScreen.name,
        screenType: editingScreen.screenType,
        capacity: totalCapacity,
        rows: payloadRows,
      });
      localStorage.setItem('quickshow_screen_rows_' + editingScreen.screenId, JSON.stringify(payloadRows));
      showToast('Screen updated successfully!', 'success');
      setEditingScreen(null);
      setEditScreenRows([]);
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

      if (String(newShow.screenId) === String(screenId)) {
        setNewShow((prev) => ({ ...prev, screenId: '' }));
      }
      setShowFormScreens((prev) => prev.filter((s) => s.screenId !== screenId));
      loadScreensForTheatre(parseInt(selectedTheatreForScreens));
    } catch (err) {
      showToast(err.message || 'Failed to delete screen', 'error');
    }
  };

  const handleTheatreChangeForShow = async (theatreId) => {
    setNewShow((prev) => ({ ...prev, theatreId, screenId: '' }));
    if (!theatreId) {
      setShowFormScreens([]);
      return;
    }
    const sc = await loadShowFormScreens(theatreId);
    if (sc.length > 0) {
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
      <div className="admin-auth admin-auth--page">
        <div className="auth-modal__header">
          <div className="auth-modal__logo">QuickShow</div>
          <p className="auth-modal__subtitle">Sign in to manage movies, theatres and bookings</p>
        </div>
        <div className="auth-modal__tabs">
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
        <form onSubmit={handleAdminAuth} className="auth-modal__form">
          {authMode === 'register' && (
            <>
              <div className="auth-modal__group">
                <label className="auth-modal__label">First Name</label>
                <input
                  className="auth-modal__input"
                  placeholder="First Name"
                  value={adminFirstName}
                  onChange={(e) => setAdminFirstName(e.target.value)}
                  required
                />
              </div>
              <div className="auth-modal__group">
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
          <div className="auth-modal__group">
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
          <div className="auth-modal__group">
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
          <button type="submit" className="auth-modal__submit admin-auth__submit" disabled={authLoading}>
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
          { tab: 'dashboard', icon: 'DB', label: 'Dashboard' },
          { tab: 'movies', icon: 'MV', label: 'Movies' },
          { tab: 'theatres', icon: 'TH', label: 'Theatres' },
          { tab: 'screens', icon: 'SC', label: 'Screens' },
          { tab: 'shows', icon: 'SH', label: 'Schedule Shows' },
          { tab: 'bookings', icon: 'BK', label: 'Bookings' },
          { tab: 'payments', icon: 'PY', label: 'Payments' },
        ].map(({ tab, icon, label }) => (
          <button
            key={tab}
            className={`inner-sidebar__item ${activeTab === tab ? 'inner-sidebar__item--active' : ''}`}
            onClick={() => openTab(tab)}
          >
            <span className="inner-sidebar__item-icon">{icon}</span>
            {label}
          </button>
        ))}

        <hr style={{ borderColor: 'var(--border-subtle)', margin: '16px 0' }} />
        <button className="inner-sidebar__item" onClick={handleAdminLogout} style={{ color: 'var(--danger)' }}>
          <span className="inner-sidebar__item-icon">←</span>Exit Admin
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
                  <h2>System Overview</h2>
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
                      ₹{statsSummary?.totalRevenue ?? payments.filter((p) => p.paymentStatus === 'SUCCESS').reduce((sum, p) => sum + (p.paymentAmount || 0), 0)}
                    </div>
                    <div className="kpi-card__label">Total Revenue</div>
                  </div>
                  <div className="kpi-card">
                    <div className="kpi-card__value" style={{ color: '#4ADE80' }}>
                      {statsBookings?.confirmedBookings ?? bookings.filter((b) => b.bookingStatus === 'CONFIRMED').length}
                    </div>
                    <div className="kpi-card__label">Confirmed</div>
                  </div>
                  <div className="kpi-card">
                    <div className="kpi-card__value" style={{ color: 'var(--danger)' }}>
                      {statsBookings?.cancelledBookings ?? bookings.filter((b) => b.bookingStatus === 'CANCELLED').length}
                    </div>
                    <div className="kpi-card__label">Cancelled</div>
                  </div>
                </div>

                {statsRevenue && (
                  <div style={{ background: 'var(--bg-secondary)', padding: '20px', borderRadius: '8px', border: '1px solid var(--border-subtle)', marginBottom: '24px' }}>
                    <h3 style={{ fontSize: '16px', marginBottom: '12px' }}>Revenue Breakdown</h3>
                    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '12px' }}>
                      <div>
                        <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>Avg Booking Value</div>
                        <div style={{ fontSize: '20px', fontWeight: 700, color: '#4ADE80' }}>₹{Number(statsRevenue.averageBookingValue || 0).toFixed(0)}</div>
                      </div>
                      <div>
                        <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>Avg Ticket Price</div>
                        <div style={{ fontSize: '20px', fontWeight: 700, color: '#00E5FF' }}>₹{Number(statsRevenue.averageTicketPrice || 0).toFixed(0)}</div>
                      </div>
                      <div>
                        <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>Successful Payments</div>
                        <div style={{ fontSize: '20px', fontWeight: 700, color: '#A78BFA' }}>{statsRevenue.successfulPayments ?? payments.filter((p) => p.paymentStatus === 'SUCCESS').length}</div>
                      </div>
                    </div>
                  </div>
                )}

                {statsBookings && (
                  <div style={{ background: 'var(--bg-secondary)', padding: '20px', borderRadius: '8px', border: '1px solid var(--border-subtle)', marginBottom: '24px' }}>
                    <h3 style={{ fontSize: '16px', marginBottom: '12px' }}>Booking Trends</h3>
                    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '12px' }}>
                      <div>
                        <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>Total Tickets Sold</div>
                        <div style={{ fontSize: '20px', fontWeight: 700, color: '#FFD700' }}>{statsBookings.ticketsSold ?? bookings.filter((b) => b.bookingStatus === 'CONFIRMED').reduce((sum, b) => sum + (b.totalSeatsCount || 0), 0)}</div>
                      </div>
                      <div>
                        <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>Confirmed Bookings</div>
                        <div style={{ fontSize: '20px', fontWeight: 700, color: '#4ADE80' }}>{statsBookings.confirmedBookings ?? bookings.filter((b) => b.bookingStatus === 'CONFIRMED').length}</div>
                      </div>
                      <div>
                        <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>Cancelled Bookings</div>
                        <div style={{ fontSize: '20px', fontWeight: 700, color: 'var(--danger)' }}>{statsBookings.cancelledBookings ?? bookings.filter((b) => b.bookingStatus === 'CANCELLED').length}</div>
                      </div>
                    </div>
                  </div>
                )}

                {topMovies.length > 0 && (
                  <div style={{ marginBottom: '24px' }}>
                    <h3 style={{ marginBottom: '12px' }}>Top Performing Movies</h3>
                    <table className="bookings-table">
                      <thead>
                        <tr>
                          <th>Movie</th>
                          <th>Tickets Sold</th>
                          <th>Total Bookings</th>
                          <th>Total Revenue</th>
                        </tr>
                      </thead>
                      <tbody>
                        {topMovies.map((tm, idx) => (
                          <tr key={idx}>
                            <td><strong>{tm.movieTitle || tm.title || `Movie #${tm.movieId}`}</strong></td>
                            <td>{tm.ticketsSold ?? 0}</td>
                            <td>{tm.bookings ?? tm.totalBookings ?? 0}</td>
                            <td>₹{tm.revenue ?? tm.totalRevenue ?? 0}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}

                {theatrePerformance.length > 0 && (
                  <div>
                    <h3 style={{ marginBottom: '12px' }}>Theatre Performance Breakdown</h3>
                    <table className="bookings-table">
                      <thead>
                        <tr>
                          <th>Theatre</th>
                          <th>City</th>
                          <th>Total Shows</th>
                          <th>Tickets Sold</th>
                          <th>Total Revenue</th>
                        </tr>
                      </thead>
                      <tbody>
                        {theatrePerformance.map((tp, idx) => {
                          const theatreObj = theatres.find((t) => t.theatreId === tp.theatreId);
                          return (
                            <tr key={idx}>
                              <td><strong>{tp.theatreName || `Theatre #${tp.theatreId}`}</strong></td>
                              <td>{theatreObj?.city || tp.city || '-'}</td>
                              <td>{tp.totalShows || 0}</td>
                              <td>{tp.ticketsSold || 0}</td>
                              <td>₹{tp.revenue ?? tp.totalRevenue ?? 0}</td>
                            </tr>
                          );
                        })}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>
            )}

            {activeTab === 'movies' && (
              <div>
                <h2 style={{ marginBottom: '16px' }}>Manage Movies</h2>

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
                      <h3 style={{ fontSize: '16px', color: 'var(--accent)' }}>Edit Movie #{editingMovie.movieId}</h3>
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
                      <h3 style={{ fontSize: '16px', color: 'var(--accent)' }}>Cast Members for "{castMovie.title}"</h3>
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
                        const actorName = c.id?.actor;
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
                      <th>Language</th>
                      <th>Genre</th>
                      <th>Duration</th>
                      <th>Certificate</th>
                      <th>IMDb</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {movies.map((m) => (
                      <tr key={m.movieId}>
                        <td>#{m.movieId}</td>
                        <td><strong>{m.title}</strong></td>
                        <td>{m.language || '-'}</td>
                        <td>{m.genre || '-'}</td>
                        <td>{m.duration ? `${m.duration} min` : '-'}</td>
                        <td>{m.certificate ? formatCertificate(m.certificate) : '-'}</td>
                        <td>⭐ {m.imdbRating || '-'}</td>
                        <td>
                          <div style={{ display: 'flex', gap: '6px', flexWrap: 'wrap' }}>
                            <button className="add-btn" style={{ padding: '4px 8px', fontSize: '11px' }} onClick={() => handleStartEditMovie(m.movieId)}>
                              Edit
                            </button>
                            <button className="add-btn" style={{ padding: '4px 8px', fontSize: '11px', background: '#A78BFA' }} onClick={() => handleOpenCastModal(m)}>
                              Cast
                            </button>
                            <button className="cancel-btn" style={{ padding: '4px 8px', fontSize: '11px' }} onClick={() => handleDeleteMovie(m.movieId)}>
                              Delete
                            </button>
                          </div>
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
                <h2 style={{ marginBottom: '16px' }}>Manage Theatres</h2>

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
                      <h3 style={{ fontSize: '16px', color: 'var(--accent)' }}>Edit Theatre #{editingTheatre.theatreId}</h3>
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
                <h2 style={{ marginBottom: '16px' }}>Manage Screens</h2>

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
                    <h3 style={{ fontSize: '16px', marginBottom: '16px' }}>+ Add Screen to Theatre</h3>
                    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '14px', marginBottom: '16px' }}>
                      <div>
                        <label className="auth-modal__label">Screen Name</label>
                        <input
                          className="auth-modal__input"
                          placeholder="Screen Name *"
                          value={newScreen.name}
                          onChange={(e) => setNewScreen({ ...newScreen, name: e.target.value })}
                          required
                        />
                      </div>
                      <div>
                        <label className="auth-modal__label">Screen Type</label>
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
                      </div>
                      <div>
                        <label className="auth-modal__label">Number of Rows</label>
                        <input
                          type="number"
                          min="1"
                          max="26"
                          className="auth-modal__input"
                          value={screenRows.length}
                          onChange={(e) => handleRowCountChange(e.target.value)}
                          required
                        />
                      </div>
                    </div>

                    <div style={{ background: 'var(--bg-surface)', padding: '16px', borderRadius: '8px', border: '1px solid var(--border-subtle)', marginBottom: '16px' }}>
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px', flexWrap: 'wrap', gap: '8px' }}>
                        <span style={{ fontWeight: 600, fontSize: '14px', color: 'var(--text-primary)' }}>Seat Configuration by Row</span>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                          <span style={{ fontSize: '13px', background: 'rgba(0, 229, 255, 0.12)', color: 'var(--primary)', padding: '4px 10px', borderRadius: '4px', fontWeight: 600, border: '1px solid rgba(0, 229, 255, 0.25)' }}>
                            Total Capacity: {screenRows.reduce((sum, r) => sum + (parseInt(r.seatCount) || 0), 0)} seats
                          </span>
                          <button
                            type="button"
                            className="add-btn"
                            style={{ padding: '4px 10px', fontSize: '12px' }}
                            onClick={handleAddRow}
                            disabled={screenRows.length >= 26}
                          >
                            + Add Row
                          </button>
                        </div>
                      </div>

                      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(130px, 1fr))', gap: '10px' }}>
                        {screenRows.map((row, idx) => (
                          <div
                            key={idx}
                            style={{
                              display: 'flex',
                              alignItems: 'center',
                              gap: '6px',
                              background: 'var(--bg-secondary)',
                              padding: '8px 10px',
                              borderRadius: '6px',
                              border: '1px solid var(--border-subtle)',
                            }}
                          >
                            <span style={{ fontWeight: 700, width: '22px', textAlign: 'center', color: 'var(--accent)' }}>
                              {row.rowNo}
                            </span>
                            <input
                              type="number"
                              min="1"
                              max="50"
                              value={row.seatCount}
                              onChange={(e) => handleRowSeatChange(idx, e.target.value)}
                              className="auth-modal__input"
                              style={{ width: '55px', padding: '4px 6px', textAlign: 'center', margin: 0 }}
                              required
                            />
                            <span style={{ fontSize: '11px', color: 'var(--text-muted)' }}>seats</span>
                            {screenRows.length > 1 && (
                              <button
                                type="button"
                                onClick={() => handleRemoveRow(idx)}
                                style={{
                                  background: 'transparent',
                                  border: 'none',
                                  color: 'var(--error, #ef4444)',
                                  cursor: 'pointer',
                                  padding: '0 2px',
                                  fontSize: '13px',
                                  lineHeight: 1,
                                }}
                                title="Remove row"
                              >
                                ✕
                              </button>
                            )}
                          </div>
                        ))}
                      </div>
                    </div>

                    <button type="submit" className="add-btn">Add Screen</button>
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
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '14px' }}>
                      <h3 style={{ fontSize: '16px', color: 'var(--accent)' }}>Edit Screen #{editingScreen.screenId}</h3>
                      <button type="button" className="cancel-btn" onClick={() => setEditingScreen(null)}>Close</button>
                    </div>
                    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '14px', marginBottom: '16px' }}>
                      <div>
                        <label className="auth-modal__label">Screen Name</label>
                        <input
                          className="auth-modal__input"
                          placeholder="Screen Name"
                          value={editingScreen.name || ''}
                          onChange={(e) => setEditingScreen({ ...editingScreen, name: e.target.value })}
                          required
                        />
                      </div>
                      <div>
                        <label className="auth-modal__label">Screen Type</label>
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
                      </div>
                      <div>
                        <label className="auth-modal__label">Number of Rows</label>
                        <input
                          type="number"
                          min="1"
                          max="26"
                          className="auth-modal__input"
                          value={editScreenRows.length}
                          onChange={(e) => handleEditRowCountChange(e.target.value)}
                          required
                        />
                      </div>
                    </div>

                    <div style={{ background: 'var(--bg-secondary)', padding: '16px', borderRadius: '8px', border: '1px solid var(--border-subtle)', marginBottom: '16px' }}>
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px', flexWrap: 'wrap', gap: '8px' }}>
                        <span style={{ fontWeight: 600, fontSize: '14px', color: 'var(--text-primary)' }}>Seat Configuration by Row</span>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                          <span style={{ fontSize: '13px', background: 'rgba(0, 229, 255, 0.12)', color: 'var(--primary)', padding: '4px 10px', borderRadius: '4px', fontWeight: 600, border: '1px solid rgba(0, 229, 255, 0.25)' }}>
                            Total Capacity: {editScreenRows.reduce((sum, r) => sum + (parseInt(r.seatCount) || 0), 0)} seats
                          </span>
                          <button
                            type="button"
                            className="add-btn"
                            style={{ padding: '4px 10px', fontSize: '12px' }}
                            onClick={handleAddEditRow}
                            disabled={editScreenRows.length >= 26}
                          >
                            + Add Row
                          </button>
                        </div>
                      </div>

                      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(130px, 1fr))', gap: '10px' }}>
                        {editScreenRows.map((row, idx) => (
                          <div
                            key={idx}
                            style={{
                              display: 'flex',
                              alignItems: 'center',
                              gap: '6px',
                              background: 'var(--bg-surface)',
                              padding: '8px 10px',
                              borderRadius: '6px',
                              border: '1px solid var(--border-subtle)',
                            }}
                          >
                            <span style={{ fontWeight: 700, width: '22px', textAlign: 'center', color: 'var(--accent)' }}>
                              {row.rowNo}
                            </span>
                            <input
                              type="number"
                              min="1"
                              max="50"
                              value={row.seatCount}
                              onChange={(e) => handleEditRowSeatChange(idx, e.target.value)}
                              className="auth-modal__input"
                              style={{ width: '55px', padding: '4px 6px', textAlign: 'center', margin: 0 }}
                              required
                            />
                            <span style={{ fontSize: '11px', color: 'var(--text-muted)' }}>seats</span>
                            {editScreenRows.length > 1 && (
                              <button
                                type="button"
                                onClick={() => handleRemoveEditRow(idx)}
                                style={{
                                  background: 'transparent',
                                  border: 'none',
                                  color: 'var(--error, #ef4444)',
                                  cursor: 'pointer',
                                  padding: '0 2px',
                                  fontSize: '13px',
                                  lineHeight: 1,
                                }}
                                title="Remove row"
                              >
                                ✕
                              </button>
                            )}
                          </div>
                        ))}
                      </div>
                    </div>

                    <div style={{ display: 'flex', gap: '8px' }}>
                      <button type="submit" className="add-btn">Save Changes</button>
                      <button type="button" className="cancel-btn" onClick={() => setEditingScreen(null)}>Cancel</button>
                    </div>
                  </form>
                )}

                {viewingScreenLayout && (
                  <div
                    style={{
                      background: 'var(--bg-surface)',
                      padding: '20px',
                      borderRadius: '8px',
                      border: '2px solid var(--primary)',
                      marginBottom: '24px',
                    }}
                  >
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px', flexWrap: 'wrap', gap: '8px' }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '10px', flexWrap: 'wrap' }}>
                        <h3 style={{ fontSize: '16px', color: 'var(--primary)', margin: 0 }}>
                          {viewingScreenLayout.name} Layout ({formatScreenType(viewingScreenLayout.screenType)})
                        </h3>
                        <span style={{ fontSize: '13px', background: 'rgba(0, 229, 255, 0.12)', color: 'var(--primary)', padding: '3px 10px', borderRadius: '4px', fontWeight: 600 }}>
                          {viewingScreenLayout.capacity} seats · {(viewingScreenLayout.rows || []).length} rows
                        </span>
                      </div>
                      <button type="button" className="cancel-btn" onClick={() => setViewingScreenLayout(null)}>✕ Close</button>
                    </div>

                    <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', overflowX: 'auto', padding: '16px', background: 'var(--bg-secondary)', borderRadius: '6px' }}>
                      <div style={{ textAlign: 'center', fontSize: '11px', color: 'var(--text-muted)', letterSpacing: '3px', borderBottom: '1px solid var(--border-subtle)', paddingBottom: '8px', marginBottom: '12px' }}>
                        SCREEN THIS WAY
                      </div>
                      {(viewingScreenLayout.rows || []).map((row, rIdx) => (
                        <div key={rIdx} style={{ display: 'flex', alignItems: 'center', gap: '8px', justifyContent: 'center' }}>
                          <span style={{ fontWeight: 700, width: '24px', textAlign: 'center', color: 'var(--accent)' }}>
                            {row.rowNo}
                          </span>
                          <div style={{ display: 'flex', gap: '4px', flexWrap: 'wrap', justifyContent: 'center' }}>
                            {Array.from({ length: row.seatCount }).map((_, sIdx) => (
                              <div
                                key={sIdx}
                                style={{
                                  width: '26px',
                                  height: '24px',
                                  fontSize: '10px',
                                  display: 'flex',
                                  alignItems: 'center',
                                  justifyContent: 'center',
                                  borderRadius: '3px',
                                  background: 'rgba(255, 255, 255, 0.08)',
                                  border: '1px solid var(--border-subtle)',
                                  color: 'var(--text-secondary)',
                                }}
                              >
                                {sIdx + 1}
                              </div>
                            ))}
                          </div>
                          <span style={{ fontSize: '11px', color: 'var(--text-muted)', width: '60px', textAlign: 'right' }}>
                            ({row.seatCount} seats)
                          </span>
                        </div>
                      ))}
                    </div>
                  </div>
                )}

                <table className="bookings-table">
                  <thead>
                    <tr>
                      <th>Screen ID</th>
                      <th>Name</th>
                      <th>Screen Type</th>
                      <th>Capacity</th>
                      <th>Seat Layout</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {screens.map((sc) => {
                      const rows = sc.rows || getScreenRows(sc);
                      return (
                        <tr key={sc.screenId}>
                          <td>#{sc.screenId}</td>
                          <td><strong>{sc.name}</strong></td>
                          <td>{formatScreenType(sc.screenType)}</td>
                          <td>{sc.capacity || '-'} seats</td>
                          <td>
                            <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                              <span style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
                                {rows.length} rows ({rows[0]?.rowNo || 'A'}-{rows[rows.length - 1]?.rowNo || 'A'})
                              </span>
                              <button
                                type="button"
                                className="add-btn"
                                style={{ padding: '3px 8px', fontSize: '11px', background: 'rgba(0, 229, 255, 0.15)', color: 'var(--primary)', border: '1px solid rgba(0, 229, 255, 0.3)' }}
                                onClick={() => setViewingScreenLayout({ ...sc, rows })}
                              >
                                View Layout
                              </button>
                            </div>
                          </td>
                          <td>
                            <div style={{ display: 'flex', gap: '8px' }}>
                              <button className="add-btn" style={{ padding: '4px 8px', fontSize: '11px' }} onClick={() => handleStartEditScreen(sc.screenId)}>
                                Edit
                              </button>
                              <button className="cancel-btn" style={{ padding: '4px 8px', fontSize: '11px' }} onClick={() => handleDeleteScreen(sc.screenId)}>
                                Delete
                              </button>
                            </div>
                          </td>
                        </tr>
                      );
                    })}
                    {screens.length === 0 && (
                      <tr>
                        <td colSpan="6" style={{ textAlign: 'center', color: 'var(--text-muted)', padding: '24px' }}>
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
                <h2 style={{ marginBottom: '16px' }}>Schedule Shows</h2>

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
                        onChange={(e) => handleTheatreChangeForShow(e.target.value)}
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
                        {showFormScreens.map((sc) => (
                          <option key={sc.screenId} value={sc.screenId}>
                            {sc.name || `Screen ${sc.screenId}`} ({formatScreenType(sc.screenType)})
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
                      <h3 style={{ fontSize: '16px', color: 'var(--accent)' }}>Edit Show #{editingShow.showId}</h3>
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
                    const screenObj = allScreens.find((sc) => sc.screenId === s.screenId);
                    const movieObj = movies.find((m) => m.movieId === s.movieId);
                    const screenType = screenObj?.screenType || s.screenType;
                    return (
                    <tr key={sid}>
                    <td>#{sid}</td>
      <td>{movieObj?.title || s.movieTitle || `Movie #${s.movieId}`}</td>
      <td>{s.showDate || '-'}</td>
      <td>{s.showTime ? String(s.showTime).slice(0, 5) : '-'}</td>
      <td>{screenType ? formatScreenType(screenType) : '-'}</td>
                          <td>₹{s.ticketPrice || '-'}</td>
                          <td>
                            <span className={`badge badge--${s.showStatus === 'CANCELLED' ? 'cancelled' : 'confirmed'}`}>
                              {s.showStatus || 'SCHEDULED'}
                            </span>
                          </td>
                          <td>
                            <div style={{ display: 'flex', gap: '6px' }}>
                              <button className="add-btn" style={{ padding: '4px 8px', fontSize: '11px' }} onClick={() => handleStartEditShow(sid)}>
                                Edit
                              </button>
                              <button className="cancel-btn" style={{ padding: '4px 8px', fontSize: '11px' }} onClick={() => handleDeleteShow(sid)}>
                                Delete
                              </button>
                            </div>
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
                <h2 style={{ marginBottom: '16px' }}>All System Bookings</h2>

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
                      <th>Amount</th>
                      <th>Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    {bookings.map((b) => {
                      const bId = b.bookingId || b.id || '-';
                      const movieTitle = b.movieName || b.movie?.title || b.movieTitle || '-';
                      const theatreName = b.theatreName || b.theatre?.name || '-';
                      const status = b.bookingStatus || b.status || 'CONFIRMED';
                      return (
                        <tr key={bId}>
                          <td>#BK-{bId}</td>
                          <td>{movieTitle}</td>
                          <td>{theatreName}</td>
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
                        <td colSpan="5" style={{ textAlign: 'center', color: 'var(--text-muted)', padding: '24px' }}>
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
                <h2 style={{ marginBottom: '16px' }}>Payment Transactions</h2>

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