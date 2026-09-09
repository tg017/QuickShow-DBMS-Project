import { useState, useEffect } from 'react';
import { useApp } from '../context/AppContext';
import { getBookingById } from '../api/api';

export default function Confirmation({ bookingId }) {
  const { navigateTo } = useApp();
  const [booking, setBooking] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    getBookingById(bookingId)
      .then((data) => {
        setBooking(data);
        setLoading(false);
      })
      .catch((err) => {
        console.error(err);
        setLoading(false);
      });
  }, [bookingId]);

  if (loading) {
    return <div className="spinner" style={{ margin: '60px auto' }}></div>;
  }

  if (!booking) {
    return (
      <div style={{ padding: '40px', textAlign: 'center', color: 'var(--text-secondary)' }}>
        <h2>Booking details not found</h2>
        <button className="movie-card__book-btn" style={{ marginTop: '16px' }} onClick={() => navigateTo('home')}>
          Go to Home
        </button>
      </div>
    );
  }

  const movieTitle = booking.movie?.title || booking.movieTitle || 'Unknown Movie';
  const theatreName = booking.theatre?.name || booking.theatreName || 'Unknown Theatre';
  const showDate = booking.showDate || booking.show?.showDate || '';
  const showTime = booking.showTime || booking.show?.showTime || '';
  const seatsDisplay = booking.seats && booking.seats.length > 0
    ? booking.seats.map((s) => (s.rowNo ? `${s.rowNo}${s.seatNo}` : s.seatId || s)).join(', ')
    : (booking.seatIds?.join(', ') || 'N/A');
  const bId = booking.bookingId || booking.id || bookingId;
  const paymentMethod = booking.payment?.paymentMethod || booking.paymentMethod || 'UPI';
  const status = booking.bookingStatus || booking.status || 'CONFIRMED';

  return (
    <div className="confirmation-view">
      <div className="confirmation-view__icon">✓</div>
      <h2 className="confirmation-view__title">Booking Confirmed!</h2>
      <p className="confirmation-view__subtitle">Your e-ticket is ready</p>

      <div className="confirmation-view__card">
        <div className="confirmation-view__booking-id">Booking ID: #BK-{bId}</div>

        <div className="confirmation-view__detail">
          <span className="confirmation-view__detail-label">Movie</span>
          <span className="confirmation-view__detail-value">{movieTitle}</span>
        </div>
        <div className="confirmation-view__detail">
          <span>Theatre</span>
          <span>{theatreName}</span>
        </div>
        <div className="confirmation-view__detail">
          <span>Date & Time</span>
          <span>{showDate} {showTime}</span>
        </div>
        <div className="confirmation-view__detail">
          <span>Seats</span>
          <span>{seatsDisplay}</span>
        </div>
        <div className="confirmation-view__detail">
          <span>Amount Paid</span>
          <span>₹{booking.totalAmount || 0}</span>
        </div>
        <div className="confirmation-view__detail">
          <span>Payment</span>
          <span>{paymentMethod}</span>
        </div>
        <div className="confirmation-view__detail">
          <span>Status</span>
          <span style={{ color: 'var(--primary)', fontWeight: 600 }}>{status}</span>
        </div>
      </div>

      <div style={{ display: 'flex', gap: '12px', marginTop: '20px', flexWrap: 'wrap', justifyContent: 'center' }}>
        <button className="confirmation-view__download" onClick={() => window.print()}>
          ⬇ Download Ticket
        </button>
        <button className="add-btn" onClick={() => navigateTo('bookings')}>
          🎫 My Bookings
        </button>
        <button className="add-btn" onClick={() => navigateTo('home')}>
          🏠 Book More
        </button>
      </div>
    </div>
  );
}
