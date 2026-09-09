import { useState, useEffect } from 'react';
import { useApp } from '../context/AppContext';
import { getBookings, cancelBooking } from '../api/api';

export default function Bookings() {
  const { user, setAuthModalOpen, setAuthTab, navigateTo, showToast } = useApp();
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchUserBookings = () => {
    setLoading(true);
    getBookings()
      .then((data) => {
        setBookings(data || []);
        setLoading(false);
      })
      .catch((err) => {
        console.error(err);
        setLoading(false);
      });
  };

  useEffect(() => {
    if (user) {
      fetchUserBookings();
    }
  }, [user]);

  if (!user) {
    return (
      <div style={{ padding: '60px', textAlign: 'center', color: 'var(--text-secondary)' }}>
        <h2>Sign In to View Bookings</h2>
        <p style={{ margin: '12px 0 20px' }}>You must be logged in to check your booking history and tickets.</p>
        <button 
          className="movie-card__book-btn"
          onClick={() => { setAuthTab('login'); setAuthModalOpen(true); }}
        >
          Sign In Now
        </button>
      </div>
    );
  }

  if (loading) {
    return <div className="spinner" style={{ margin: '60px auto' }}></div>;
  }

  const handleCancel = async (bookingId) => {
    if (!window.confirm('Are you sure you want to cancel this booking?')) return;
    try {
      await cancelBooking(bookingId);
      showToast('Booking cancelled successfully', 'info');
      fetchUserBookings();
    } catch (err) {
      showToast(err.message || 'Failed to cancel booking', 'error');
    }
  };

  const today = new Date();
  today.setHours(0, 0, 0, 0);

  const upcomingBookings = [];
  const pastBookings = [];

  bookings.forEach((b) => {
    const showDateStr = b.showDate || b.show?.showDate;
    if (showDateStr) {
      const showDate = new Date(showDateStr);
      if (showDate < today) {
        pastBookings.push(b);
      } else {
        upcomingBookings.push(b);
      }
    } else {
      upcomingBookings.push(b);
    }
  });

  const renderBookingTable = (list, isUpcoming) => {
    if (list.length === 0) {
      return (
        <div style={{ padding: '24px', color: 'var(--text-muted)', textAlign: 'center' }}>
          No {isUpcoming ? 'upcoming' : 'past'} bookings found.
        </div>
      );
    }

    return (
      <div style={{ overflowX: 'auto' }}>
        <table className="bookings-table">
          <thead>
            <tr>
              <th>Booking ID</th>
              <th>Movie</th>
              <th>Theatre</th>
              <th>Date & Time</th>
              <th>Seats</th>
              <th>Amount</th>
              <th>Status</th>
              {isUpcoming && <th>Action</th>}
            </tr>
          </thead>
          <tbody>
            {list.map((b) => {
              const bId = b.bookingId || b.id || '-';
              const movieTitle = b.movie?.title || b.movieTitle || '-';
              const theatreName = b.theatre?.name || b.theatreName || '-';
              const showDate = b.showDate || b.show?.showDate || '';
              const showTime = b.showTime || b.show?.showTime || '';
              const seats = b.seats && b.seats.length > 0
                ? b.seats.map((s) => (s.rowNo ? `${s.rowNo}${s.seatNo}` : s.seatId || s)).join(', ')
                : (b.seatIds?.join(', ') || 'N/A');
              const status = b.bookingStatus || b.status || 'CONFIRMED';
              const isConfirmed = status === 'CONFIRMED';

              return (
                <tr key={bId}>
                  <td>
                    <button 
                      style={{ background: 'none', border: 'none', color: 'var(--primary)', cursor: 'pointer', fontWeight: 600 }}
                      onClick={() => navigateTo(`booking/${bId}/confirmation`)}
                    >
                      #BK-{bId}
                    </button>
                  </td>
                  <td>{movieTitle}</td>
                  <td>{theatreName}</td>
                  <td>{showDate} {showTime}</td>
                  <td>{seats}</td>
                  <td>₹{b.totalAmount || 0}</td>
                  <td>
                    <span className={`badge badge--${isConfirmed ? 'confirmed' : 'cancelled'}`}>
                      {status}
                    </span>
                  </td>
                  {isUpcoming && (
                    <td>
                      {isConfirmed && (
                        <button 
                          className="cancel-btn"
                          onClick={() => handleCancel(bId)}
                        >
                          Cancel
                        </button>
                      )}
                    </td>
                  )}
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    );
  };

  return (
    <div style={{ padding: '24px' }}>
      <h2 style={{ fontSize: '24px', marginBottom: '24px', color: 'var(--text-primary)' }}>My Bookings 🎫</h2>

      <div style={{ marginBottom: '36px' }}>
        <h3 style={{ fontSize: '18px', color: 'var(--primary)', marginBottom: '12px' }}>Upcoming Shows</h3>
        <div style={{ background: 'var(--bg-secondary)', borderRadius: '8px', border: '1px solid var(--border-subtle)', overflow: 'hidden' }}>
          {renderBookingTable(upcomingBookings, true)}
        </div>
      </div>

      <div>
        <h3 style={{ fontSize: '18px', color: 'var(--text-secondary)', marginBottom: '12px' }}>Past Bookings</h3>
        <div style={{ background: 'var(--bg-secondary)', borderRadius: '8px', border: '1px solid var(--border-subtle)', overflow: 'hidden' }}>
          {renderBookingTable(pastBookings, false)}
        </div>
      </div>
    </div>
  );
}
