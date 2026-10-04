const BASE = '/api';

const getToken = () => {
  try {
    const u = JSON.parse(localStorage.getItem('quickshow_user') || 'null');
    if (u?.token) return u.token;
    const a = JSON.parse(localStorage.getItem('quickshow_admin') || 'null');
    if (a?.token) return a.token;
  } catch {}
  return null;
};

async function apiFetch(path, options = {}) {
  const token = getToken();
  const headers = { 'Content-Type': 'application/json', ...options.headers };
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const res = await fetch(`${BASE}${path}`, { ...options, headers });
  const contentType = res.headers.get('content-type') || '';

  if (!res.ok) {
    let errorMsg = `Request failed with status ${res.status}`;
    if (contentType.includes('application/json')) {
      try {
        const err = await res.json();
        errorMsg = err.message || err.error || errorMsg;
      } catch {}
    } else {
      try {
        const text = await res.text();
        if (text) errorMsg = text;
      } catch {}
    }
    throw new Error(errorMsg);
  }

  if (contentType.includes('application/json')) {
    const text = await res.text();
    return text ? JSON.parse(text) : null;
  }

  const text = await res.text();
  return text || null;
}

export const getMovies = () => apiFetch('/movies');

export const searchMovies = (query = {}) => {
  const params = new URLSearchParams();
  if (query.title && String(query.title).trim()) {
    params.append('title', String(query.title).trim());
  }
  if (query.language && String(query.language).trim()) {
    params.append('language', String(query.language).trim());
  }
  const cert = query.certificate || query.rating;
  if (cert && String(cert).trim()) {
    params.append('certificate', String(cert).trim());
  }
  if (query.genre && String(query.genre).trim()) {
    params.append('genre', String(query.genre).trim());
  }
  const qs = params.toString();
  return apiFetch(`/movies/search${qs ? `?${qs}` : ''}`);
};

export const getMovieById = (id) => apiFetch(`/movies/${id}`);

export const getTheatres = () => apiFetch('/theatres');

export const searchTheatresByCity = (city) => {
  if (!city || !String(city).trim()) {
    return getTheatres();
  }
  return apiFetch(`/theatres/search?city=${encodeURIComponent(String(city).trim())}`);
};

export const getTheatreShows = (theatreId, date) => {
  const q = date ? `?date=${encodeURIComponent(date)}` : '';
  return apiFetch(`/theatres/${theatreId}/shows/search${q}`);
};

export const getShowsByMovie = (movieId, date) => {
  const q = date ? `?date=${encodeURIComponent(date)}` : '';
  return apiFetch(`/shows/movie/${movieId}/search${q}`);
};

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
