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
    const lang = (url.searchParams.get('language') || '').toLowerCase();
    const genre = (url.searchParams.get('genre') || '').toLowerCase();
    const cert = (url.searchParams.get('certificate') || url.searchParams.get('rating') || '').toLowerCase();
    const all = [...SEED_MOVIES, ...getCustomMovies()];
    return all.filter(m => 
      (!title || (m.title || '').toLowerCase().includes(title)) &&
      (!lang || (m.language || '').toLowerCase() === lang) &&
      (!genre || (m.genre || '').toLowerCase().includes(genre)) &&
      (!cert || (m.certificate || '').toLowerCase() === cert)
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
    const mId = Math.floor((showId % 1000) / 10) || 1;
    const theatre = allTheatres.find(t => t.theatreId === tId) || allTheatres[0];
    const movie = allMovies.find(m => m.movieId === mId) || allMovies[0];

    const seatKey = 'qs_seats_' + showId;
    let showSeats = null;
    try {
      showSeats = JSON.parse(localStorage.getItem(seatKey) || 'null');
    } catch {}

    if (!showSeats || !Array.isArray(showSeats) || showSeats.length === 0) {
      showSeats = generateSeats(60, showId);
      try {
        localStorage.setItem(seatKey, JSON.stringify(showSeats));
      } catch {}
    }

    return {
      showId,
      movie: { movieId: movie.movieId, title: movie.title, poster: movie.poster },
      theatre: { theatreId: theatre.theatreId, name: theatre.name, city: theatre.city },
      screen: { screenId: tId * 3 + 1, name: 'Screen 1 (IMAX)', screenType: 'IMAX' },
      showDate: formatLocalDate(new Date()),
      showTime: '18:00',
      ticketPrice: 250,
      availableSeats: showSeats.filter(s => s.status !== 'BOOKED').length,
      showStatus: 'SCHEDULED',
      seats: showSeats
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
    const mId = Math.floor((showId % 1000) / 10) || 1;
    const theatre = allTheatres.find(t => t.theatreId === tId) || allTheatres[0];
    const movie = allMovies.find(m => m.movieId === mId) || allMovies[0];

    const seatKey = 'qs_seats_' + showId;
    let showSeats = [];
    try {
      showSeats = JSON.parse(localStorage.getItem(seatKey) || '[]');
    } catch {}

    let bookedSeatObjs = [];
    if (showSeats.length > 0) {
      bookedSeatObjs = showSeats
        .filter(s => (seatIds || []).includes(s.seatId))
        .map(s => ({ seatId: s.seatId, rowNo: s.rowNo, seatNo: s.seatNo }));
      showSeats.forEach(s => {
        if ((seatIds || []).includes(s.seatId)) s.status = 'BOOKED';
      });
      try {
        localStorage.setItem(seatKey, JSON.stringify(showSeats));
      } catch {}
    }

    if (bookedSeatObjs.length === 0) {
      const rows = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'];
      const cols = 8;
      bookedSeatObjs = (seatIds || []).map(id => ({
        seatId: id,
        rowNo: rows[Math.min(Math.floor((id - 1) / cols), rows.length - 1)] || 'A',
        seatNo: ((id - 1) % cols) + 1
      }));
    }

    const booking = {
      bookingId: Math.floor(100000 + Math.random() * 900000),
      userId: user?.userId || 101,
      showId,
      seatIds,
      totalAmount: (seatIds || []).length * 250,
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
      screen: { screenId: tId * 3 + 1, name: 'Screen 1', screenType: 'IMAX' },
      seats: bookedSeatObjs,
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
      if (found.showId && found.seatIds) {
        const seatKey = 'qs_seats_' + found.showId;
        try {
          const showSeats = JSON.parse(localStorage.getItem(seatKey) || '[]');
          if (showSeats.length > 0) {
            showSeats.forEach(s => {
              if (found.seatIds.includes(s.seatId)) s.status = 'AVAILABLE';
            });
            localStorage.setItem(seatKey, JSON.stringify(showSeats));
          }
        } catch {}
      }
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
  if (method === 'GET' && path.match(/^\/admin\/theatres\/(\d+)$/)) {
    const id = parseInt(path.match(/^\/admin\/theatres\/(\d+)$/)[1]);
    const all = [...SEED_THEATRES, ...getCustomTheatres()];
    const th = all.find(t => t.theatreId === id);
    if (!th) throw new Error('Theatre not found');
    return th;
  }
  if (method === 'POST' && path === '/admin/theatres') {
    const data = JSON.parse(options.body);
    const custom = getCustomTheatres();
    const newTh = { theatreId: Date.now(), ...data };
    custom.push(newTh);
    localStorage.setItem('qs_custom_theatres', JSON.stringify(custom));
    return newTh;
  }
  if (method === 'PUT' && path.match(/^\/admin\/theatres\/(\d+)$/)) {
    const id = parseInt(path.match(/^\/admin\/theatres\/(\d+)$/)[1]);
    const data = JSON.parse(options.body);
    const custom = getCustomTheatres();
    const idx = custom.findIndex(t => t.theatreId === id);
    if (idx >= 0) {
      custom[idx] = { ...custom[idx], ...data, theatreId: id };
      localStorage.setItem('qs_custom_theatres', JSON.stringify(custom));
      return custom[idx];
    }
    return { theatreId: id, ...data };
  }
  if (method === 'DELETE' && path.match(/^\/admin\/theatres\/(\d+)$/)) {
    const id = parseInt(path.match(/^\/admin\/theatres\/(\d+)$/)[1]);
    const custom = getCustomTheatres().filter(t => t.theatreId !== id);
    localStorage.setItem('qs_custom_theatres', JSON.stringify(custom));
    return 'Theatre deleted successfully.';
  }

  if (method === 'GET' && path.match(/^\/admin\/theatres\/(\d+)\/screens$/)) {
    const tId = parseInt(path.match(/^\/admin\/theatres\/(\d+)\/screens$/)[1]);
    const stored = JSON.parse(localStorage.getItem('qs_custom_screens_' + tId) || 'null');
    if (stored) return stored;
    return [
      { screenId: tId * 3 + 1, theatreId: tId, name: 'Screen 1 (IMAX)', screenType: 'IMAX', capacity: 60 },
      { screenId: tId * 3 + 2, theatreId: tId, name: 'Screen 2 (3D)', screenType: 'THREE_D', capacity: 60 },
      { screenId: tId * 3 + 3, theatreId: tId, name: 'Screen 3 (2D)', screenType: 'TWO_D', capacity: 60 }
    ];
  }
  if (method === 'POST' && path.match(/^\/admin\/theatres\/(\d+)\/screens$/)) {
    const tId = parseInt(path.match(/^\/admin\/theatres\/(\d+)\/screens$/)[1]);
    const data = JSON.parse(options.body);
    const stored = JSON.parse(localStorage.getItem('qs_custom_screens_' + tId) || '[]');
    const newSc = { screenId: Date.now(), theatreId: tId, ...data };
    stored.push(newSc);
    localStorage.setItem('qs_custom_screens_' + tId, JSON.stringify(stored));
    return newSc;
  }
  if (method === 'GET' && path.match(/^\/admin\/screens\/(\d+)$/)) {
    const scId = parseInt(path.match(/^\/admin\/screens\/(\d+)$/)[1]);
    return { screenId: scId, name: 'Screen ' + scId, screenType: 'IMAX', capacity: 60 };
  }
  if (method === 'PUT' && path.match(/^\/admin\/screens\/(\d+)$/)) {
    const scId = parseInt(path.match(/^\/admin\/screens\/(\d+)$/)[1]);
    const data = JSON.parse(options.body);
    return { screenId: scId, ...data };
  }
  if (method === 'DELETE' && path.match(/^\/admin\/screens\/(\d+)$/)) {
    return 'Screen deleted successfully.';
  }

  if (method === 'GET' && path === '/admin/shows') {
    return getShowsForTheatre(1, formatLocalDate(new Date()));
  }
  if (method === 'GET' && path.match(/^\/admin\/shows\/(\d+)$/)) {
    const sId = parseInt(path.match(/^\/admin\/shows\/(\d+)$/)[1]);
    return { showId: sId, movieId: 1, screenId: 1, showDate: formatLocalDate(new Date()), showTime: '18:00', ticketPrice: 250, availableSeats: 60, showStatus: 'SCHEDULED' };
  }
  if (method === 'POST' && path === '/admin/shows') {
    const data = JSON.parse(options.body);
    return { showId: Date.now(), ...data };
  }
  if (method === 'PUT' && path.match(/^\/admin\/shows\/(\d+)$/)) {
    const sId = parseInt(path.match(/^\/admin\/shows\/(\d+)$/)[1]);
    const data = JSON.parse(options.body);
    return { showId: sId, ...data };
  }
  if (method === 'DELETE' && path.match(/^\/admin\/shows\/(\d+)$/)) {
    return 'Show deleted successfully.';
  }

  if (method === 'GET' && path === '/admin/movies') {
    return [...SEED_MOVIES, ...getCustomMovies()];
  }
  if (method === 'GET' && path.match(/^\/admin\/movies\/(\d+)$/)) {
    const id = parseInt(path.match(/^\/admin\/movies\/(\d+)$/)[1]);
    const m = [...SEED_MOVIES, ...getCustomMovies()].find(x => x.movieId === id);
    if (!m) throw new Error('Movie not found');
    return m;
  }
  if (method === 'POST' && path === '/admin/movies') {
    const data = JSON.parse(options.body);
    const custom = getCustomMovies();
    const newM = { movieId: Date.now(), ...data, cast: [] };
    custom.push(newM);
    localStorage.setItem('qs_custom_movies', JSON.stringify(custom));
    return newM;
  }
  if (method === 'PUT' && path.match(/^\/admin\/movies\/(\d+)$/)) {
    const id = parseInt(path.match(/^\/admin\/movies\/(\d+)$/)[1]);
    const data = JSON.parse(options.body);
    const custom = getCustomMovies();
    const idx = custom.findIndex(x => x.movieId === id);
    if (idx >= 0) {
      custom[idx] = { ...custom[idx], ...data, movieId: id };
      localStorage.setItem('qs_custom_movies', JSON.stringify(custom));
      return custom[idx];
    }
    return { movieId: id, ...data };
  }
  if (method === 'DELETE' && path.match(/^\/admin\/movies\/(\d+)$/)) {
    const id = parseInt(path.match(/^\/admin\/movies\/(\d+)$/)[1]);
    const custom = getCustomMovies().filter(x => x.movieId !== id);
    localStorage.setItem('qs_custom_movies', JSON.stringify(custom));
    return 'Movie deleted successfully.';
  }

  if (method === 'GET' && path.match(/^\/admin\/movies\/(\d+)\/cast$/)) {
    const mId = parseInt(path.match(/^\/admin\/movies\/(\d+)\/cast$/)[1]);
    const stored = JSON.parse(localStorage.getItem('qs_cast_' + mId) || 'null');
    if (stored) return stored;
    const movie = [...SEED_MOVIES, ...getCustomMovies()].find(x => x.movieId === mId);
    return (movie?.cast || ['Lead Actor', 'Supporting Actor']).map((a, i) => ({ id: i + 1, actor: typeof a === 'string' ? a : a.actor || 'Actor' }));
  }
  if (method === 'POST' && path.match(/^\/admin\/movies\/(\d+)\/cast$/)) {
    const mId = parseInt(path.match(/^\/admin\/movies\/(\d+)\/cast$/)[1]);
    const { actor } = JSON.parse(options.body);
    const stored = JSON.parse(localStorage.getItem('qs_cast_' + mId) || '[]');
    stored.push({ id: Date.now(), actor });
    localStorage.setItem('qs_cast_' + mId, JSON.stringify(stored));
    return 'Actor added successfully.';
  }
  if (method === 'DELETE' && path.match(/^\/admin\/movies\/(\d+)\/cast\/(.+)$/)) {
    const mId = parseInt(path.match(/^\/admin\/movies\/(\d+)\/cast\/(.+)$/)[1]);
    const actorName = decodeURIComponent(path.match(/^\/admin\/movies\/(\d+)\/cast\/(.+)$/)[2]);
    const stored = JSON.parse(localStorage.getItem('qs_cast_' + mId) || '[]');
    const filtered = stored.filter(x => x.actor !== actorName);
    localStorage.setItem('qs_cast_' + mId, JSON.stringify(filtered));
    return null;
  }

  if (method === 'GET' && path === '/admin/bookings') {
    return getStoredBookings();
  }
  if (method === 'GET' && path.startsWith('/admin/bookings/search')) {
    const url = new URL('http://localhost' + path);
    const m = url.searchParams.get('movie');
    const t = url.searchParams.get('theatre');
    let all = getStoredBookings();
    if (m) all = all.filter(b => String(b.movieId || b.show?.movieId || '') === String(m));
    if (t) all = all.filter(b => String(b.theatreId || b.show?.theatreId || '') === String(t));
    return all;
  }

  if (method === 'GET' && path === '/admin/payments') {
    const allB = getStoredBookings();
    return allB.map((b, i) => ({
      paymentId: b.payment?.paymentId || (1000 + i),
      paymentMethod: b.paymentMethod || 'UPI',
      paymentAmount: b.totalAmount || 250,
      transactionId: 'TXN-' + (100000 + i),
      paymentDateTime: new Date().toISOString(),
      paymentStatus: 'SUCCESS'
    }));
  }
  if (method === 'GET' && path.match(/^\/admin\/payments\/(\d+)$/)) {
    const pid = parseInt(path.match(/^\/admin\/payments\/(\d+)$/)[1]);
    return {
      paymentId: pid,
      paymentMethod: 'UPI',
      paymentAmount: 250,
      transactionId: 'TXN-' + pid,
      paymentDateTime: new Date().toISOString(),
      paymentStatus: 'SUCCESS'
    };
  }
  if (method === 'GET' && path.startsWith('/admin/payments/search')) {
    const url = new URL('http://localhost' + path);
    const status = url.searchParams.get('paymentStatus');
    const allB = getStoredBookings();
    let res = allB.map((b, i) => ({
      paymentId: b.payment?.paymentId || (1000 + i),
      paymentMethod: b.paymentMethod || 'UPI',
      paymentAmount: b.totalAmount || 250,
      transactionId: 'TXN-' + (100000 + i),
      paymentDateTime: new Date().toISOString(),
      paymentStatus: b.bookingStatus === 'CANCELLED' ? 'REFUNDED' : 'SUCCESS'
    }));
    if (status) res = res.filter(p => p.paymentStatus.toLowerCase() === status.toLowerCase());
    return res;
  }

  if (method === 'GET' && path.startsWith('/admin/statistics/revenue')) {
    const allB = getStoredBookings();
    const total = allB.reduce((s, b) => s + (b.totalAmount || 0), 0);
    return {
      totalRevenue: total,
      onlineRevenue: Math.round(total * 0.8),
      offlineRevenue: Math.round(total * 0.2),
      ticketSalesCount: allB.length
    };
  }
  if (method === 'GET' && path.startsWith('/admin/statistics/bookings')) {
    const allB = getStoredBookings();
    return {
      totalBookings: allB.length,
      completedBookings: allB.filter(b => (b.bookingStatus || b.status) === 'CONFIRMED').length,
      cancelledBookings: allB.filter(b => (b.bookingStatus || b.status) === 'CANCELLED').length,
      totalTicketsBooked: allB.reduce((s, b) => s + (b.totalSeatCount || b.seats?.length || 1), 0)
    };
  }
  if (method === 'GET' && path.startsWith('/admin/statistics/movies/top')) {
    const allM = [...SEED_MOVIES, ...getCustomMovies()];
    return allM.slice(0, 5).map((m, i) => ({
      movieId: m.movieId,
      movieTitle: m.title,
      totalBookings: 120 - i * 15,
      totalRevenue: (120 - i * 15) * 250
    }));
  }
  if (method === 'GET' && path.startsWith('/admin/statistics/theatres')) {
    const allT = [...SEED_THEATRES, ...getCustomTheatres()];
    return allT.map((t, i) => ({
      theatreId: t.theatreId,
      theatreName: t.name,
      city: t.city,
      totalShows: 24,
      totalRevenue: 45000 - i * 5000,
      occupancyRate: 75.5
    }));
  }
  if (method === 'GET' && path.startsWith('/admin/statistics/summary')) {
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
export const searchMovies = (query = {}) => {
  const params = new URLSearchParams();
  Object.entries(query).forEach(([k, v]) => {
    if (v != null && String(v).trim() !== '') {
      params.append(k, String(v).trim());
    }
  });
  const qs = params.toString();
  return apiFetch(`/movies/search${qs ? `?${qs}` : ''}`);
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
export const adminGetTheatreById = (id) => apiFetch(`/admin/theatres/${id}`);
export const adminAddTheatre = (data) => apiFetch('/admin/theatres', { method: 'POST', body: JSON.stringify(data) });
export const adminUpdateTheatre = (id, data) => apiFetch(`/admin/theatres/${id}`, { method: 'PUT', body: JSON.stringify(data) });
export const adminDeleteTheatre = (id) => apiFetch(`/admin/theatres/${id}`, { method: 'DELETE' });

export const adminGetScreensByTheatre = (theatreId) => apiFetch(`/admin/theatres/${theatreId}/screens`);
export const adminGetScreenById = (id) => apiFetch(`/admin/screens/${id}`);
export const adminAddScreen = (theatreId, data) => apiFetch(`/admin/theatres/${theatreId}/screens`, { method: 'POST', body: JSON.stringify(data) });
export const adminUpdateScreen = (id, data) => apiFetch(`/admin/screens/${id}`, { method: 'PUT', body: JSON.stringify(data) });
export const adminDeleteScreen = (id) => apiFetch(`/admin/screens/${id}`, { method: 'DELETE' });

export const adminGetAllShows = () => apiFetch('/admin/shows');
export const adminGetShowById = (id) => apiFetch(`/admin/shows/${id}`);
export const adminAddShow = (data) => apiFetch('/admin/shows', { method: 'POST', body: JSON.stringify(data) });
export const adminUpdateShow = (id, data) => apiFetch(`/admin/shows/${id}`, { method: 'PUT', body: JSON.stringify(data) });
export const adminDeleteShow = (id) => apiFetch(`/admin/shows/${id}`, { method: 'DELETE' });

export const adminGetAllMovies = () => apiFetch('/admin/movies');
export const adminGetMovieById = (id) => apiFetch(`/admin/movies/${id}`);
export const adminAddMovie = (data) => apiFetch('/admin/movies', { method: 'POST', body: JSON.stringify(data) });
export const adminUpdateMovie = (id, data) => apiFetch(`/admin/movies/${id}`, { method: 'PUT', body: JSON.stringify(data) });
export const adminDeleteMovie = (id) => apiFetch(`/admin/movies/${id}`, { method: 'DELETE' });

export const adminGetMovieCast = (movieId) => apiFetch(`/admin/movies/${movieId}/cast`);
export const adminAddActor = (movieId, data) => apiFetch(`/admin/movies/${movieId}/cast`, { method: 'POST', body: JSON.stringify(data) });
export const adminDeleteActor = (movieId, actor) => apiFetch(`/admin/movies/${movieId}/cast/${encodeURIComponent(actor)}`, { method: 'DELETE' });

export const adminGetAllBookings = () => apiFetch('/admin/bookings');
export const adminSearchBookings = (params = {}) => {
  const q = new URLSearchParams(params);
  const s = q.toString();
  return apiFetch(`/admin/bookings/search${s ? `?${s}` : ''}`);
};

export const adminGetAllPayments = () => apiFetch('/admin/payments');
export const adminGetPaymentById = (id) => apiFetch(`/admin/payments/${id}`);
export const adminSearchPayments = (params = {}) => {
  const q = new URLSearchParams(params);
  const s = q.toString();
  return apiFetch(`/admin/payments/search${s ? `?${s}` : ''}`);
};

export const adminGetStatsSummary = (params = {}) => {
  const q = new URLSearchParams(params);
  const s = q.toString();
  return apiFetch(`/admin/statistics/summary${s ? `?${s}` : ''}`);
};
export const adminGetStatsRevenue = (params = {}) => {
  const q = new URLSearchParams(params);
  const s = q.toString();
  return apiFetch(`/admin/statistics/revenue${s ? `?${s}` : ''}`);
};
export const adminGetStatsBookings = (params = {}) => {
  const q = new URLSearchParams(params);
  const s = q.toString();
  return apiFetch(`/admin/statistics/bookings${s ? `?${s}` : ''}`);
};
export const adminGetTopMovies = (params = {}) => {
  const q = new URLSearchParams(params);
  const s = q.toString();
  return apiFetch(`/admin/statistics/movies/top${s ? `?${s}` : ''}`);
};
export const adminGetTheatrePerformance = (params = {}) => {
  const q = new URLSearchParams(params);
  const s = q.toString();
  return apiFetch(`/admin/statistics/theatres${s ? `?${s}` : ''}`);
};
