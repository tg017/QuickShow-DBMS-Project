import { 
  SEED_MOVIES, 
  SEED_THEATRES, 
  SEED_SCREENS, 
  generateSeats, 
  formatLocalDate 
} from './data.js';

const BASE = '/api';

const getCurrentUser = () => {
  try { return JSON.parse(localStorage.getItem('quickshow_user') || 'null'); } catch { return null; }
};

const getCurrentAdmin = () => {
  try { return JSON.parse(localStorage.getItem('quickshow_admin') || 'null'); } catch { return null; }
};

const getToken = () => {
  const u = getCurrentUser();
  if (u?.token) return u.token;
  const a = getCurrentAdmin();
  if (a?.token) return a.token;
  return null;
};

const getCustomMovies = () => {
  try { return JSON.parse(localStorage.getItem('qs_custom_movies') || '[]'); } catch { return []; }
};

const getCustomTheatres = () => {
  try { return JSON.parse(localStorage.getItem('qs_custom_theatres') || '[]'); } catch { return []; }
};

const getStoredUsers = () => {
  try {
    const list = JSON.parse(localStorage.getItem('qs_users') || '[]');
    const defaults = [
      { userId: 101, email: 'test.cse@gmail.com', password: 'Test@1234', firstName: 'Test', lastName: 'User', phoneNo: '9876543210' },
      { userId: 102, email: 'rahul.cse@gmail.com', password: '123456', firstName: 'Rahul', lastName: 'Sharma', phoneNo: '9876543211' }
    ];
    defaults.forEach(d => {
      if (!list.some(u => u.email.toLowerCase() === d.email.toLowerCase())) list.push(d);
    });
    const cur = getCurrentUser();
    if (cur && !list.some(u => u.email.toLowerCase() === cur.email?.toLowerCase())) list.push(cur);
    return list;
  } catch {
    return [];
  }
};

const saveStoredUsers = (users) => {
  try { localStorage.setItem('qs_users', JSON.stringify(users)); } catch {}
};

const getStoredBookings = () => {
  try { return JSON.parse(localStorage.getItem('qs_bookings') || '[]'); } catch { return []; }
};

const saveStoredBookings = (bookings) => {
  try { localStorage.setItem('qs_bookings', JSON.stringify(bookings)); } catch {}
};

function getShowsForMovie(movieId, dateStr) {
  const mId = parseInt(movieId);
  const movie = [...SEED_MOVIES, ...getCustomMovies()].find(m => m.movieId === mId) || SEED_MOVIES[0];
  const allTheatres = [...SEED_THEATRES, ...getCustomTheatres()];

  const showConfigs = [
    { time: '10:15:00', type: 'IMAX', price: 280 },
    { time: '13:45:00', type: '3D', price: 220 },
    { time: '17:15:00', type: '2D', price: 180 },
    { time: '20:45:00', type: 'IMAX', price: 320 }
  ];

  return allTheatres.map(theatre => ({
    theatreId: theatre.theatreId,
    theatreName: theatre.name,
    city: theatre.city,
    shows: showConfigs.map((cfg, idx) => ({
      showId: theatre.theatreId * 1000 + mId * 10 + idx + 1,
      movieId: mId,
      theatreId: theatre.theatreId,
      screenId: theatre.theatreId * 3 + idx + 1,
      showDate: dateStr || formatLocalDate(new Date()),
      showTime: cfg.time,
      ticketPrice: cfg.price,
      screenType: cfg.type,
      movieTitle: movie.title,
      language: movie.language,
      availableSeats: 60
    }))
  }));
}

function getShowsForTheatre(theatreId, dateStr) {
  const tId = parseInt(theatreId);
  const allMovies = [...SEED_MOVIES, ...getCustomMovies()];
  const showConfigs = [
    { time: '10:15:00', type: 'IMAX', price: 280 },
    { time: '13:45:00', type: '3D', price: 220 },
    { time: '17:15:00', type: '2D', price: 180 },
    { time: '20:45:00', type: 'IMAX', price: 320 }
  ];

  const result = [];
  allMovies.slice(0, 4).forEach((movie, mIdx) => {
    const cfg = showConfigs[mIdx % showConfigs.length];
    result.push({
      showId: tId * 1000 + movie.movieId * 10 + mIdx + 1,
      movieId: movie.movieId,
      theatreId: tId,
      screenId: tId * 3 + 1,
      showDate: dateStr || formatLocalDate(new Date()),
      showTime: cfg.time,
      ticketPrice: cfg.price,
      screenType: cfg.type,
      movieTitle: movie.title,
      language: movie.language,
      availableSeats: 60
    });
  });
  return result;
}

async function mockFetch(path, options = {}) {
  const method = options.method || 'GET';

  if (method === 'GET' && path === '/movies') {
    return [...SEED_MOVIES, ...getCustomMovies()];
  }

  if (method === 'GET' && path.startsWith('/movies/search')) {
    const url = new URL('http://localhost' + path);
    const title = (url.searchParams.get('title') || '').toLowerCase();
    const lang = url.searchParams.get('language') || '';
    const genre = url.searchParams.get('genre') || '';
    const all = [...SEED_MOVIES, ...getCustomMovies()];
    return all.filter(m => 
      m.title.toLowerCase().includes(title) &&
      (!lang || m.language === lang) &&
      (!genre || m.genre.toLowerCase().includes(genre.toLowerCase()))
    );
  }

  if (method === 'GET' && path.match(/^\/movies\/(\d+)$/)) {
    const id = parseInt(path.match(/^\/movies\/(\d+)$/)[1]);
    const all = [...SEED_MOVIES, ...getCustomMovies()];
    const m = all.find(x => x.movieId === id);
    if (!m) throw new Error('Movie not found');
    return m;
  }

  if (method === 'GET' && path === '/theatres') {
    return [...SEED_THEATRES, ...getCustomTheatres()];
  }

  if (method === 'GET' && path.startsWith('/theatres/search')) {
    const url = new URL('http://localhost' + path);
    const city = (url.searchParams.get('city') || '').toLowerCase();
    const all = [...SEED_THEATRES, ...getCustomTheatres()];
    if (!city) return all;
    const filtered = all.filter(t => (t.city || '').toLowerCase() === city);
    return filtered.length > 0 ? filtered : all;
  }

  if (method === 'GET' && path.match(/^\/theatres\/(\d+)\/shows\/search/)) {
    const theatreId = parseInt(path.match(/^\/theatres\/(\d+)\/shows\/search/)[1]);
    const url = new URL('http://localhost' + path);
    const date = url.searchParams.get('date');
    return getShowsForTheatre(theatreId, date);
  }

  if (method === 'GET' && path.match(/^\/shows\/movie\/(\d+)\/search/)) {
    const movieId = parseInt(path.match(/^\/shows\/movie\/(\d+)\/search/)[1]);
    const url = new URL('http://localhost' + path);
    const date = url.searchParams.get('date');
    return getShowsForMovie(movieId, date);
  }

  if (method === 'GET' && path.match(/^\/shows\/(\d+)\/seats$/)) {
    const showId = parseInt(path.match(/^\/shows\/(\d+)\/seats$/)[1]);
    const allTheatres = [...SEED_THEATRES, ...getCustomTheatres()];
    const allMovies = [...SEED_MOVIES, ...getCustomMovies()];

    const tId = Math.floor(showId / 1000) || 1;
    const theatre = allTheatres.find(t => t.theatreId === tId) || allTheatres[0];
    const movie = allMovies[0];

    return {
      showId,
      movie: { movieId: movie.movieId, title: movie.title, poster: movie.poster },
      theatre: { theatreId: theatre.theatreId, name: theatre.name, city: theatre.city },
      screen: { screenId: 1, name: 'Screen 1 (IMAX)', screenType: 'IMAX' },
      showDate: formatLocalDate(new Date()),
      showTime: '18:00',
      ticketPrice: 250,
      availableSeats: 60,
      showStatus: 'SCHEDULED',
      seats: generateSeats(60)
    };
  }

  if (method === 'POST' && path === '/auth/login') {
    const data = JSON.parse(options.body);
    const email = String(data.email || '').trim().toLowerCase();
    const password = String(data.password || '');
    const users = getStoredUsers();
    const u = users.find(x => String(x.email || '').trim().toLowerCase() === email);
    if (!u || (u.password && u.password !== password)) {
      throw new Error('Invalid email or password');
    }
    const token = u.token || 'mock-token-' + Date.now();
    return { userId: u.userId, firstName: u.firstName, lastName: u.lastName, email: u.email, token, message: 'Login successful' };
  }

  if (method === 'POST' && path === '/auth/register') {
    const data = JSON.parse(options.body);
    const email = String(data.email || '').trim().toLowerCase();
    const users = getStoredUsers();
    if (users.find(x => String(x.email || '').trim().toLowerCase() === email)) {
      throw new Error('Email is already registered. Please sign in!');
    }
    const newUser = { userId: Date.now(), ...data, email, token: 'mock-token-' + Date.now() };
    users.push(newUser);
    saveStoredUsers(users);
    return { userId: newUser.userId, firstName: newUser.firstName, lastName: newUser.lastName, email: newUser.email, message: 'Registered successfully' };
  }

  if (method === 'GET' && path === '/customer/profile') {
    const u = getCurrentUser();
    if (!u) throw new Error('Not logged in');
    const users = getStoredUsers();
    const found = users.find(x => x.userId === u.userId || x.email === u.email);
    return found || u;
  }

  if (method === 'POST' && path === '/bookings/checkout') {
    const user = getCurrentUser();
    const { showId, seatIds, paymentMethod } = JSON.parse(options.body);
    const allMovies = [...SEED_MOVIES, ...getCustomMovies()];
    const allTheatres = [...SEED_THEATRES, ...getCustomTheatres()];

    const tId = Math.floor(showId / 1000) || 1;
    const theatre = allTheatres.find(t => t.theatreId === tId) || allTheatres[0];
    const movie = allMovies[0];

    const booking = {
      bookingId: Math.floor(100000 + Math.random() * 900000),
      userId: user?.userId || 101,
      showId,
      seatIds,
      totalAmount: seatIds.length * 250,
      paymentMethod: paymentMethod || 'UPI',
      status: 'CONFIRMED',
      bookingStatus: 'CONFIRMED',
      movieTitle: movie.title,
      moviePoster: movie.poster,
      theatreName: theatre.name,
      theatreCity: theatre.city,
      showDate: formatLocalDate(new Date()),
      showTime: '18:00',
      show: { showId, showDate: formatLocalDate(new Date()), showTime: '18:00' },
      movie: { movieId: movie.movieId, title: movie.title, poster: movie.poster },
      theatre: { theatreId: theatre.theatreId, name: theatre.name, city: theatre.city },
      screen: { screenId: 1, name: 'Screen 1', screenType: 'IMAX' },
      seats: (seatIds || []).map((id, i) => ({ seatId: id, rowNo: 'A', seatNo: i + 1 })),
      totalSeatCount: (seatIds || []).length,
      payment: { paymentId: Date.now(), paymentMethod: paymentMethod || 'UPI', paymentAmount: (seatIds || []).length * 250, paymentStatus: 'SUCCESS' }
    };

    const bookings = getStoredBookings();
    bookings.unshift(booking);
    saveStoredBookings(bookings);
    return booking;
  }

  if (method === 'GET' && path === '/bookings') {
    const user = getCurrentUser();
    const all = getStoredBookings();
    if (!user) return all.slice(0, 5);
    return all.filter(b => b.userId === user.userId || b.userId == null);
  }

  if (method === 'GET' && path.match(/^\/bookings\/(\d+)$/)) {
    const bid = parseInt(path.match(/^\/bookings\/(\d+)$/)[1]);
    const all = getStoredBookings();
    const found = all.find(x => x.bookingId === bid);
    return found || {
      bookingId: bid,
      bookingStatus: 'CONFIRMED',
      movieTitle: 'Interstellar: Beyond Time',
      theatreName: 'Galaxy Cinemas – IP Sigra Mall',
      showDate: formatLocalDate(new Date()),
      showTime: '18:00',
      totalAmount: 250,
      paymentMethod: 'UPI',
      seats: [{ seatId: 1, rowNo: 'A', seatNo: 1 }]
    };
  }

  if (method === 'PUT' && path.match(/^\/bookings\/(\d+)\/cancel$/)) {
    const bid = parseInt(path.match(/^\/bookings\/(\d+)\/cancel$/)[1]);
    const all = getStoredBookings();
    const found = all.find(x => x.bookingId === bid);
    if (found) {
      found.status = 'CANCELLED';
      found.bookingStatus = 'CANCELLED';
      saveStoredBookings(all);
    }
    return 'Booking cancelled successfully';
  }

  if (method === 'POST' && path === '/admin/login') {
    const data = JSON.parse(options.body);
    const email = String(data.email || '').trim().toLowerCase();
    const password = String(data.password || '');
    if (password === 'Admin@123' || email.includes('admin')) {
      return { adminId: 1, firstName: 'System', lastName: 'Admin', email, role: 'ADMIN', token: 'mock-admin-token-' + Date.now(), message: 'Login successful' };
    }
    throw new Error('Invalid admin credentials. Use Admin@123');
  }

  if (method === 'POST' && path === '/admin/register') {
    return 'Admin registered successfully';
  }

  if (method === 'GET' && path === '/admin/theatres') {
    return [...SEED_THEATRES, ...getCustomTheatres()];
  }
  if (method === 'POST' && path === '/admin/theatres') {
    const data = JSON.parse(options.body);
    const custom = getCustomTheatres();
    const newTh = { theatreId: Date.now(), ...data };
    custom.push(newTh);
    localStorage.setItem('qs_custom_theatres', JSON.stringify(custom));
    return newTh;
  }
  if (method === 'DELETE' && path.match(/^\/admin\/theatres\/(\d+)$/)) {
    const id = parseInt(path.match(/^\/admin\/theatres\/(\d+)$/)[1]);
    const custom = getCustomTheatres().filter(t => t.theatreId !== id);
    localStorage.setItem('qs_custom_theatres', JSON.stringify(custom));
    return 'Theatre deleted successfully.';
  }

  if (method === 'GET' && path.match(/^\/admin\/theatres\/(\d+)\/screens$/)) {
    const tId = parseInt(path.match(/^\/admin\/theatres\/(\d+)\/screens$/)[1]);
    return [
      { screenId: tId * 3 + 1, name: 'Screen 1 (IMAX)', screenType: 'IMAX' },
      { screenId: tId * 3 + 2, name: 'Screen 2 (3D)', screenType: '3D' },
      { screenId: tId * 3 + 3, name: 'Screen 3 (2D)', screenType: '2D' }
    ];
  }

  if (method === 'GET' && path === '/admin/shows') {
    return getShowsForTheatre(1, formatLocalDate(new Date()));
  }
  if (method === 'POST' && path === '/admin/shows') {
    const data = JSON.parse(options.body);
    return { showId: Date.now(), ...data };
  }
  if (method === 'DELETE' && path.match(/^\/admin\/shows\/(\d+)$/)) {
    return 'Show deleted successfully.';
  }

  if (method === 'GET' && path === '/admin/bookings') {
    return getStoredBookings();
  }

  if (method === 'GET' && path.startsWith('/admin/statistics')) {
    const allB = getStoredBookings();
    return {
      totalBookings: allB.length,
      totalRevenue: allB.reduce((s, b) => s + (b.totalAmount || 0), 0),
      confirmedBookings: allB.filter(b => (b.bookingStatus || b.status) === 'CONFIRMED').length,
      cancelledBookings: allB.filter(b => (b.bookingStatus || b.status) === 'CANCELLED').length
    };
  }

  throw new Error(`Mock endpoint not handled: ${method} ${path}`);
}

async function apiFetch(path, options = {}) {
  const token = getToken();
  const headers = { 'Content-Type': 'application/json', ...options.headers };
  if (token) headers['Authorization'] = `Bearer ${token}`;

  try {
    const res = await fetch(`${BASE}${path}`, { ...options, headers });
    const contentType = res.headers.get('content-type') || '';

    if (res.ok && contentType.includes('application/json')) {
      const text = await res.text();
      const parsed = text ? JSON.parse(text) : null;

      if (Array.isArray(parsed) && parsed.length === 0 && (path === '/movies' || path === '/theatres' || path.startsWith('/shows/movie/'))) {
        return mockFetch(path, options);
      }
      return parsed;
    }

    return mockFetch(path, options);
  } catch {

    return mockFetch(path, options);
  }
}

export const getMovies = () => apiFetch('/movies');
export const searchMovies = (query) => {
  const params = new URLSearchParams(query);
  return apiFetch(`/movies/search?${params.toString()}`);
};
export const getMovieById = (id) => apiFetch(`/movies/${id}`);

export const getTheatres = () => apiFetch('/theatres');
export const searchTheatresByCity = (city) => apiFetch(`/theatres/search?city=${encodeURIComponent(city)}`);
export const getTheatreShows = (theatreId, date) => apiFetch(`/theatres/${theatreId}/shows/search?date=${date}`);

export const getShowsByMovie = (movieId, date) => apiFetch(`/shows/movie/${movieId}/search?date=${date}`);
export const getShowSeats = (showId) => apiFetch(`/shows/${showId}/seats`);

export const login = (creds) => apiFetch('/auth/login', { method: 'POST', body: JSON.stringify(creds) });
export const register = (fields) => apiFetch('/auth/register', { method: 'POST', body: JSON.stringify(fields) });
export const getMe = () => apiFetch('/auth/me');
export const getProfile = () => apiFetch('/customer/profile');

export const checkout = (data) => apiFetch('/bookings/checkout', { method: 'POST', body: JSON.stringify(data) });
export const getBookings = () => apiFetch('/bookings');
export const getBookingById = (id) => apiFetch(`/bookings/${id}`);
export const cancelBooking = (id) => apiFetch(`/bookings/${id}/cancel`, { method: 'PUT' });

export const adminLogin = (creds) => apiFetch('/admin/login', { method: 'POST', body: JSON.stringify(creds) });
export const adminRegister = (fields) => apiFetch('/admin/register', { method: 'POST', body: JSON.stringify(fields) });

export const adminGetAllTheatres = () => apiFetch('/admin/theatres');
export const adminAddTheatre = (data) => apiFetch('/admin/theatres', { method: 'POST', body: JSON.stringify(data) });
export const adminDeleteTheatre = (id) => apiFetch(`/admin/theatres/${id}`, { method: 'DELETE' });
export const adminGetScreensByTheatre = (theatreId) => apiFetch(`/admin/theatres/${theatreId}/screens`);

export const adminGetAllShows = () => apiFetch('/admin/shows');
export const adminAddShow = (data) => apiFetch('/admin/shows', { method: 'POST', body: JSON.stringify(data) });
export const adminDeleteShow = (id) => apiFetch(`/admin/shows/${id}`, { method: 'DELETE' });

export const adminGetAllMovies = () => apiFetch('/movies');
export const adminGetAllBookings = () => apiFetch('/admin/bookings');

export const adminGetStatsSummary = () => apiFetch('/admin/statistics/summary');
export const adminGetTopMovies = () => apiFetch('/admin/statistics/movies/top');
export const adminGetTheatrePerformance = () => apiFetch('/admin/statistics/theatres');
