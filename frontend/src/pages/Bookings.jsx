import { useState, useEffect } from 'react';
import { useApp } from '../context/AppContext';
import { getBookings, cancelBooking } from '../api/api';

export default function Bookings() {
  const { user, setAuthModalOpen, setAuthTab, navigateTo, showToast } = useApp();
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [expandedId, setExpandedId] = useState(null);

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

  const formatDate = (dateStr) => {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleDateString(undefined, { day: 'numeric', month: 'short', year: 'numeric' });
  };

  const formatTime = (timeStr) => {
    if (!timeStr) return '';
    return timeStr.slice(0, 5);
  };

  const renderBookingCards = (list, isUpcoming) => {
    if (list.length === 0) {
      return (
        <div style={{ padding: '32px', color: 'var(--text-muted)', textAlign: 'center', fontSize: '14px' }}>
          <div style={{ fontSize: '32px', marginBottom: '8px' }}>{isUpcoming ? '🎟️' : '📁'}</div>
          No {isUpcoming ? 'upcoming' : 'past'} bookings found.
        </div>
      );
    }

    return (
      <div style={{ display: 'flex', flexDirection: 'column', gap: '12px', padding: '16px' }}>
        {list.map((b) => {
          const bId = b.bookingId || b.id || '-';
          const movieTitle = b.movie?.title || b.movieTitle || '-';
          const theatreName = b.theatre?.name || b.theatreName || '-';
          const theatreAddress = b.theatre?.address || b.theatreAddress || '';
          const showDate = b.showDate || b.show?.showDate || '';
          const showTime = b.showTime || b.show?.showTime || '';
          const screenName = b.screen?.name || b.screenName || '';
          const screenType = b.screen?.screenType || b.screenType || '';
          const seats = b.seats && b.seats.length > 0
            ? b.seats.map((s) => (s.rowNo ? `${s.rowNo}${s.seatNo}` : s.seatId || s)).join(', ')
            : (b.seatIds?.join(', ') || 'N/A');
          const status = b.bookingStatus || b.status || 'CONFIRMED';
          const isConfirmed = status === 'CONFIRMED';
          const paymentId = b.payment?.paymentId || b.paymentId || null;
          const isExpanded = expandedId === bId;

          const formatScreenType = (st) => {
            if (!st) return '';
            return st.replace('TWO_D', '2D').replace('THREE_D', '3D').replace('IMAX_3D', 'IMAX 3D').replace('IMAX_2D', 'IMAX 2D').replace('FOUR_DX', '4DX').replace(/_/g, ' ');
          };

          return (
            <div
              key={bId}
              style={{
                background: 'var(--bg-card, var(--bg-secondary))',
                border: `1px solid ${isExpanded ? 'var(--primary)' : 'var(--border-subtle)'}`,
                borderRadius: '12px',
                overflow: 'hidden',
                transition: 'border-color 0.2s',
              }}
            >
              <div
                style={{ cursor: 'pointer', padding: '16px 20px', display: 'flex', alignItems: 'center', gap: '16px', flexWrap: 'wrap' }}
                onClick={() => setExpandedId(isExpanded ? null : bId)}
              >
                <div style={{
                  width: '44px', height: '44px', borderRadius: '8px',
                  background: 'linear-gradient(135deg, var(--primary), #00b8d4)',
                  display: 'flex', alignItems: 'center', justifyContent: 'center',
                  fontSize: '20px', flexShrink: 0,
                }}>
                  🎫
                </div>

                <div style={{ flex: 1, minWidth: '160px' }}>
                  <div style={{ fontWeight: 700, fontSize: '15px', color: 'var(--text-primary)', marginBottom: '2px' }}>
                    {movieTitle}
                  </div>
                  <div style={{ fontSize: '12px', color: 'var(--text-secondary)' }}>
                    {theatreName}{screenName ? ` · ${screenName}` : ''}{formatScreenType(screenType) ? ` · ${formatScreenType(screenType)}` : ''}
                  </div>
                </div>

                <div style={{ textAlign: 'center', minWidth: '80px' }}>
                  <div style={{ fontSize: '13px', fontWeight: 600, color: 'var(--text-primary)' }}>{formatDate(showDate)}</div>
                  <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>{formatTime(showTime)}</div>
                </div>

                <div style={{ textAlign: 'center', minWidth: '60px' }}>
                  <div style={{ fontSize: '13px', fontWeight: 700, color: 'var(--primary)' }}>₹{b.totalAmount || 0}</div>
                  <div style={{ fontSize: '11px', color: 'var(--text-muted)' }}>{seats.split(',').length} seat(s)</div>
                </div>

                <div>
                  <span className={`badge badge--${isConfirmed ? 'confirmed' : 'cancelled'}`}>
                    {status}
                  </span>
                </div>

                <div style={{ color: 'var(--text-muted)', fontSize: '18px', transform: isExpanded ? 'rotate(180deg)' : 'rotate(0deg)', transition: 'transform 0.2s' }}>
                  ▾
                </div>
              </div>

              {isExpanded && (
                <div style={{
                  borderTop: '1px solid var(--border-subtle)',
                  padding: '16px 20px',
                  background: 'var(--bg-secondary)',
                  display: 'grid',
                  gridTemplateColumns: 'repeat(auto-fill, minmax(160px, 1fr))',
                  gap: '12px 24px',
                }}>
                  <Detail label="Booking ID" value={`#BK-${bId}`} />
                  <Detail label="Seats" value={seats} />
                  <Detail label="Show Date" value={formatDate(showDate)} />
                  <Detail label="Show Time" value={formatTime(showTime) || '-'} />
                  <Detail label="Theatre" value={theatreName} />
                  {theatreAddress && <Detail label="Address" value={theatreAddress} />}
                  {screenName && <Detail label="Screen" value={`${screenName}${formatScreenType(screenType) ? ` (${formatScreenType(screenType)})` : ''}`} />}
                  {paymentId && <Detail label="Payment ID" value={`#PAY-${paymentId}`} />}
                  <Detail label="Total Paid" value={`₹${b.totalAmount || 0}`} />
                  <Detail label="Status" value={status} highlight={isConfirmed} />

                  <div style={{ gridColumn: '1 / -1', display: 'flex', gap: '10px', marginTop: '8px' }}>
                    <button
                      style={{ background: 'none', border: '1px solid var(--primary)', color: 'var(--primary)', borderRadius: '6px', padding: '6px 14px', cursor: 'pointer', fontSize: '13px', fontWeight: 600 }}
                      onClick={() => navigateTo(`booking/${bId}/confirmation`)}
                    >
                      View Receipt
                    </button>
                    {isUpcoming && isConfirmed && (
                      <button
                        className="cancel-btn"
                        onClick={() => handleCancel(bId)}
                      >
                        Cancel Booking
                      </button>
                    )}
                  </div>
                </div>
              )}
            </div>
          );
        })}
      </div>
    );
  };

  return (
    <div style={{ padding: '24px' }}>
      <h2 style={{ fontSize: '24px', marginBottom: '24px', color: 'var(--text-primary)' }}>My Bookings 🎫</h2>

      <div style={{ marginBottom: '36px' }}>
        <h3 style={{ fontSize: '16px', color: 'var(--primary)', marginBottom: '12px', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
          Upcoming Shows
        </h3>
        <div style={{ background: 'var(--bg-secondary)', borderRadius: '12px', border: '1px solid var(--border-subtle)', overflow: 'hidden' }}>
          {renderBookingCards(upcomingBookings, true)}
        </div>
      </div>

      <div>
        <h3 style={{ fontSize: '16px', color: 'var(--text-secondary)', marginBottom: '12px', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
          Past Bookings
        </h3>
        <div style={{ background: 'var(--bg-secondary)', borderRadius: '12px', border: '1px solid var(--border-subtle)', overflow: 'hidden' }}>
          {renderBookingCards(pastBookings, false)}
        </div>
      </div>
    </div>
  );
}

function Detail({ label, value, highlight }) {
  return (
    <div>
      <div style={{ fontSize: '11px', color: 'var(--text-muted)', marginBottom: '2px', textTransform: 'uppercase', letterSpacing: '0.04em' }}>{label}</div>
      <div style={{ fontSize: '13px', fontWeight: 600, color: highlight ? 'var(--primary)' : 'var(--text-primary)' }}>{value}</div>
    </div>
  );
}
