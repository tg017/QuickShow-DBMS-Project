import { useState, useEffect } from 'react';
import { useApp } from '../context/AppContext';
import { getShowSeats } from '../api/api';

export default function SeatLayout({ showId }) {
  const { navigateTo, showToast } = useApp();
  const [layout, setLayout] = useState(null);
  const [loading, setLoading] = useState(true);
  const [selectedSeats, setSelectedSeats] = useState([]);

  useEffect(() => {
    setLoading(true);
    getShowSeats(showId)
      .then((data) => {
        setLayout(data);
        setLoading(false);
      })
      .catch((err) => {
        console.error(err);
        setLoading(false);
      });
  }, [showId]);

  if (loading) {
    return <div className="spinner" style={{ margin: '60px auto' }}></div>;
  }

  if (!layout) {
    return (
      <div style={{ padding: '40px', textAlign: 'center', color: 'var(--text-secondary)' }}>
        <h2>Show layout not found</h2>
        <button className="movie-card__book-btn" style={{ marginTop: '16px' }} onClick={() => navigateTo('home')}>
          Back to Home
        </button>
      </div>
    );
  }

  const rowMap = new Map();
  if (layout.seats) {
    layout.seats.forEach((seat) => {
      if (!rowMap.has(seat.rowNo)) rowMap.set(seat.rowNo, []);
      rowMap.get(seat.rowNo).push(seat);
    });
  }

  const sortedRows = Array.from(rowMap.keys()).sort();
  const baseTicketPrice = layout.ticketPrice || 0;
  const totalSeatsInScreen = layout.seats ? layout.seats.length : 0;
  const totalAmount = selectedSeats.length * baseTicketPrice;

  const handleSeatClick = (seat) => {
    if (seat.status === 'BOOKED') return;

    const exists = selectedSeats.some((s) => s.id === seat.seatId);
    if (exists) {
      setSelectedSeats(selectedSeats.filter((s) => s.id !== seat.seatId));
    } else {
      if (selectedSeats.length >= 8) {
        showToast('Maximum 8 seats allowed per booking', 'warning');
        return;
      }
      setSelectedSeats([
        ...selectedSeats,
        { id: seat.seatId, name: `${seat.rowNo}${seat.seatNo}`, rowNo: seat.rowNo, seatNo: seat.seatNo, price: baseTicketPrice }
      ]);
    }
  };

  const removeSeat = (id) => {
    setSelectedSeats(selectedSeats.filter((s) => s.id !== id));
  };

  const handleProceed = () => {
    if (selectedSeats.length === 0) return;

    const checkoutData = {
      showId,
      seatIds: selectedSeats.map((s) => s.id),
      ticketPrice: baseTicketPrice,
      totalAmount,
      selectedSeats: selectedSeats.map((s) => s.name),
      movie: layout.movie,
      theatre: layout.theatre || {},
      screen: layout.screen || {},
      showDate: layout.showDate,
      showTime: layout.showTime
    };

    sessionStorage.setItem('qs_checkout', JSON.stringify(checkoutData));
    navigateTo('payment');
  };

  return (
    <div className="seat-page">

      <div className="seat-page__header">
        <div className="seat-page__movie-title">{layout.movie?.title || 'Movie'}</div>
        <div className="seat-page__show-info">
          {layout.theatre?.name || 'Galaxy Cinema'} · {layout.screen?.name || 'Screen 1'} · {layout.showDate || ''} · {layout.showTime || ''}
        </div>
      </div>

      <div className="seat-grid">
        <div className="seat-grid__screen">SCREEN THIS WAY</div>

        {sortedRows.map((rowKey) => {
          const seatsInRow = rowMap.get(rowKey).sort((a, b) => a.seatNo - b.seatNo);
          return (
            <div key={rowKey} className="seat-grid__row">
              <span className="seat-grid__row-label">{rowKey}</span>
              {seatsInRow.map((seat) => {
                const isBooked = seat.status === 'BOOKED';
                const isSelected = selectedSeats.some((s) => s.id === seat.seatId);
                let cssClass = 'seat-cell--available';
                if (isBooked) cssClass = 'seat-cell--booked';
                else if (isSelected) cssClass = 'seat-cell--selected';

                return (
                  <button
                    key={seat.seatId}
                    className={`seat-cell ${cssClass}`}
                    disabled={isBooked}
                    onClick={() => handleSeatClick(seat)}
                    title={`Row ${seat.rowNo}, Seat ${seat.seatNo} - ₹${baseTicketPrice}`}
                  >
                    {seat.seatNo}
                  </button>
                );
              })}
              <span className="seat-grid__row-label">{rowKey}</span>
            </div>
          );
        })}
      </div>

      <div className="seat-summary">
        <h3 className="seat-summary__title">Selected Seats</h3>

        <div className="seat-summary__metrics">
          <div className="seat-summary__metric">
            <div className="seat-summary__metric-value">{totalSeatsInScreen}</div>
            <div className="seat-summary__metric-label">Seats</div>
          </div>
          <div className="seat-summary__metric">
            <div className="seat-summary__metric-value">{selectedSeats.length}</div>
            <div className="seat-summary__metric-label">Selected</div>
          </div>
          <div className="seat-summary__metric">
            <div className="seat-summary__metric-value">₹{baseTicketPrice}</div>
            <div className="seat-summary__metric-label">Ticket Price</div>
          </div>
          <div className="seat-summary__metric">
            <div className="seat-summary__metric-value">₹{totalAmount}</div>
            <div className="seat-summary__metric-label">Total</div>
          </div>
        </div>

        <div className="seat-summary__legend">
          <div className="seat-summary__legend-item">
            <div className="seat-summary__legend-swatch seat-summary__legend-swatch--available"></div>
            Available
          </div>
          <div className="seat-summary__legend-item">
            <div className="seat-summary__legend-swatch seat-summary__legend-swatch--selected"></div>
            Selected
          </div>
          <div className="seat-summary__legend-item">
            <div className="seat-summary__legend-swatch seat-summary__legend-swatch--booked"></div>
            Booked
          </div>
        </div>

        <div className="seat-summary__list">
          {selectedSeats.map((seat) => (
            <span key={seat.id} className="seat-summary__seat-tag">
              {seat.name}{' '}
              <button 
                className="remove-seat-btn"
                onClick={() => removeSeat(seat.id)}
                title="Remove seat"
              >
                ×
              </button>
            </span>
          ))}
        </div>

        <div className="seat-summary__totals">
          <span className="seat-summary__total-label">Total Amount</span>
          <span className="seat-summary__total-value">₹{totalAmount}</span>
        </div>

        <button 
          className="seat-summary__proceed"
          disabled={selectedSeats.length === 0}
          onClick={handleProceed}
        >
          PROCEED TO BOOKING
        </button>
      </div>
    </div>
  );
}
