import { useState } from 'react';
import { useApp } from '../context/AppContext';
import { checkout } from '../api/api';

export default function Checkout() {
  const { user, setAuthModalOpen, setAuthTab, navigateTo, showToast } = useApp();
  const [selectedMethod, setSelectedMethod] = useState('UPI');
  const [loading, setLoading] = useState(false);
  const [cardNumber, setCardNumber] = useState('');
  const [cardName, setCardName] = useState('');

  const checkoutDataStr = sessionStorage.getItem('qs_checkout');
  if (!checkoutDataStr) {
    return (
      <div style={{ padding: '40px', textAlign: 'center', color: 'var(--text-secondary)' }}>
        <h2>No checkout session found</h2>
        <button className="movie-card__book-btn" style={{ marginTop: '16px' }} onClick={() => navigateTo('home')}>
          Go to Home
        </button>
      </div>
    );
  }

  const checkoutData = JSON.parse(checkoutDataStr);
  const seatNames = checkoutData.selectedSeats?.join(', ') || checkoutData.seatIds?.join(', ') || 'N/A';
  const totalAmount = checkoutData.totalAmount || (checkoutData.seatIds?.length || 0) * (checkoutData.ticketPrice || 0);

  const handlePay = async () => {
    if (!user) {
      setAuthTab('login');
      setAuthModalOpen(true);
      showToast('Please sign in to complete your booking', 'info');
      return;
    }

    setLoading(true);
    try {
      const result = await checkout({
        showId: checkoutData.showId,
        seatIds: checkoutData.seatIds,
        paymentMethod: selectedMethod
      });
      sessionStorage.removeItem('qs_checkout');
      showToast('Booking confirmed!', 'success');
      navigateTo(`booking/${result.bookingId}/confirmation`);
    } catch (err) {
      showToast(err.message || 'Payment failed', 'error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="payment-view">
      <h2 className="payment-view__title">Payment Method</h2>
      <p className="payment-view__subtitle">Review & complete your booking</p>

      <div className="payment-view__summary">
        <div className="payment-view__summary-row">
          <span>Movie</span>
          <span>{checkoutData.movie?.title || 'N/A'}</span>
        </div>
        <div className="payment-view__summary-row">
          <span>Theatre</span>
          <span>{checkoutData.theatre?.name || 'Galaxy Cinemas'}</span>
        </div>
        <div className="payment-view__summary-row">
          <span>Show</span>
          <span>{checkoutData.showDate || ''} {checkoutData.showTime || ''}</span>
        </div>
        <div className="payment-view__summary-row">
          <span>Seats</span>
          <span>{seatNames}</span>
        </div>
        <div className="payment-view__summary-row payment-view__summary-row--total">
          <span>Total Amount</span>
          <span>₹{totalAmount}</span>
        </div>
      </div>

      <div className="payment-methods">
        <div 
          className={`payment-method ${selectedMethod === 'UPI' ? 'payment-method--selected' : ''}`}
          onClick={() => setSelectedMethod('UPI')}
        >
          <div className="payment-method__radio"></div>
          <span className="payment-method__label">UPI (PhonePe / GPay / Paytm)</span>
        </div>

        <div 
          className={`payment-method ${selectedMethod === 'CREDIT_CARD' ? 'payment-method--selected' : ''}`}
          onClick={() => setSelectedMethod('CREDIT_CARD')}
        >
          <div className="payment-method__radio"></div>
          <span className="payment-method__label">Credit Card</span>
        </div>

        <div 
          className={`payment-method ${selectedMethod === 'DEBIT_CARD' ? 'payment-method--selected' : ''}`}
          onClick={() => setSelectedMethod('DEBIT_CARD')}
        >
          <div className="payment-method__radio"></div>
          <span className="payment-method__label">Debit Card</span>
        </div>
      </div>

      {(selectedMethod === 'CREDIT_CARD' || selectedMethod === 'DEBIT_CARD') && (
        <div className="payment-form">
          <div className="payment-form__group">
            <label className="payment-form__label">Card Number</label>
            <input 
              className="payment-form__input" 
              placeholder="XXXX XXXX XXXX XXXX" 
              value={cardNumber}
              onChange={(e) => setCardNumber(e.target.value)}
            />
          </div>
          <div className="payment-form__group">
            <label className="payment-form__label">Cardholder Name</label>
            <input 
              className="payment-form__input" 
              placeholder="Name on card" 
              value={cardName}
              onChange={(e) => setCardName(e.target.value)}
            />
          </div>
        </div>
      )}

      <div className="payment-amount">
        <span className="payment-amount__label">Amount to Pay</span>
        <span className="payment-amount__value">₹{totalAmount}</span>
      </div>

      <button 
        className="payment-submit" 
        disabled={loading}
        onClick={handlePay}
      >
        {loading ? 'Processing Booking...' : `Confirm & Pay ₹${totalAmount}`}
      </button>
    </div>
  );
}
