import { useState } from 'react';
import { useApp } from '../context/AppContext';
import { login, register } from '../api/api';

export default function AuthModal() {
  const { authModalOpen, setAuthModalOpen, authTab, setAuthTab, loginUser, showToast } = useApp();
  const [loading, setLoading] = useState(false);

  const [loginEmail, setLoginEmail] = useState('');
  const [loginPassword, setLoginPassword] = useState('');

  const [regData, setRegData] = useState({
    firstName: '',
    middleName: '',
    lastName: '',
    email: '',
    password: '',
    phoneNo: '',
    dob: '',
    gender: 'MALE',
    houseNo: '',
    street: '',
    area: '',
    city: '',
    state: '',
    pinCode: '',
  });

  if (!authModalOpen) return null;

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {

      const res = await login({ email: loginEmail, password: loginPassword });
      loginUser(res);   
      showToast(`Welcome back, ${res.firstName || 'User'}!`, 'success');
      setAuthModalOpen(false);
    } catch (err) {
      showToast(err.message || 'Login failed', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await register(regData);
      showToast('Registration successful! Please sign in.', 'success');
      setAuthTab('login');
      setLoginEmail(regData.email);
    } catch (err) {
      showToast('Registration failed: ' + (err.message || 'Error occurred'), 'error');
    } finally {
      setLoading(false);
    }
  };

  const fillDemo = (email, pwd) => { setLoginEmail(email); setLoginPassword(pwd); };

  return (
    <div className="modal-overlay open" onClick={() => setAuthModalOpen(false)}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="auth-modal">
          <button className="modal-close" onClick={() => setAuthModalOpen(false)} title="Close">×</button>

          <div className="auth-modal__header">
            <div className="auth-modal__logo">🎬 QuickShow</div>
            <p className="auth-modal__subtitle">
              {authTab === 'login'
                ? 'Sign in to access your bookings & profile'
                : 'Create an account for instant ticket booking'}
            </p>
          </div>

          <div className="auth-modal__tabs">
            <button
              className={`auth-modal__tab ${authTab === 'login' ? 'auth-modal__tab--active' : ''}`}
              onClick={() => setAuthTab('login')}
            >Sign In</button>
            <button
              className={`auth-modal__tab ${authTab === 'register' ? 'auth-modal__tab--active' : ''}`}
              onClick={() => setAuthTab('register')}
            >Create Account</button>
          </div>

          {authTab === 'login' ? (
            <form className="auth-modal__form" onSubmit={handleLogin}>
              <div className="auth-modal__group">
                <label className="auth-modal__label">Email Address</label>
                <input
                  type="email" className="auth-modal__input" placeholder="e.g. test.cse@gmail.com"
                  value={loginEmail} onChange={(e) => setLoginEmail(e.target.value)}
                  required autoComplete="email"
                />
              </div>
              <div className="auth-modal__group">
                <label className="auth-modal__label">Password</label>
                <input
                  type="password" className="auth-modal__input" placeholder="Enter your password"
                  value={loginPassword} onChange={(e) => setLoginPassword(e.target.value)}
                  required autoComplete="current-password"
                />
              </div>

              <button type="submit" className="auth-modal__submit" disabled={loading}>
                {loading ? 'Signing in...' : 'Sign In'}
              </button>

              <div className="auth-modal__demo-hints">
                <span className="auth-modal__demo-label">⚡ Quick Fill Demo:</span>
                <div className="auth-modal__chips">
                  <button type="button" className="auth-chip" onClick={() => fillDemo('test.cse@gmail.com', 'Test@1234')}>
                    👤 Test User (Test@1234)
                  </button>
                </div>
              </div>

              <div className="auth-modal__footer">
                Don't have an account?{' '}
                <button type="button" className="auth-modal__switch-link" onClick={() => setAuthTab('register')}>
                  Register here
                </button>
              </div>
            </form>
          ) : (
            <form className="auth-modal__form" onSubmit={handleRegister}>
              {/* Name row */}
              <div className="auth-modal__row">
                <div className="auth-modal__group">
                  <label className="auth-modal__label">First Name *</label>
                  <input type="text" className="auth-modal__input" placeholder="First Name"
                    value={regData.firstName} onChange={(e) => setRegData({ ...regData, firstName: e.target.value })} required />
                </div>
                <div className="auth-modal__group">
                  <label className="auth-modal__label">Last Name *</label>
                  <input type="text" className="auth-modal__input" placeholder="Last Name"
                    value={regData.lastName} onChange={(e) => setRegData({ ...regData, lastName: e.target.value })} required />
                </div>
              </div>

              {/* Email */}
              <div className="auth-modal__group">
                <label className="auth-modal__label">Email Address *</label>
                <input type="email" className="auth-modal__input" placeholder="name@example.com"
                  value={regData.email} onChange={(e) => setRegData({ ...regData, email: e.target.value })} required />
              </div>

              {/* Password + Phone */}
              <div className="auth-modal__row">
                <div className="auth-modal__group">
                  <label className="auth-modal__label">Password *</label>
                  <input type="password" className="auth-modal__input" placeholder="Choose a password"
                    value={regData.password} onChange={(e) => setRegData({ ...regData, password: e.target.value })} required />
                </div>
                <div className="auth-modal__group">
                  <label className="auth-modal__label">Phone *</label>
                  <input type="tel" className="auth-modal__input" placeholder="10-digit phone"
                    value={regData.phoneNo} onChange={(e) => setRegData({ ...regData, phoneNo: e.target.value })} required />
                </div>
              </div>

              {/* DOB + Gender */}
              <div className="auth-modal__row">
                <div className="auth-modal__group">
                  <label className="auth-modal__label">Date of Birth *</label>
                  <input type="date" className="auth-modal__input"
                    value={regData.dob} onChange={(e) => setRegData({ ...regData, dob: e.target.value })} required />
                </div>
                <div className="auth-modal__group">
                  <label className="auth-modal__label">Gender *</label>
                  <select className="auth-modal__input" value={regData.gender}
                    onChange={(e) => setRegData({ ...regData, gender: e.target.value })}>
                    <option value="MALE">Male</option>
                    <option value="FEMALE">Female</option>
                    <option value="OTHER">Other</option>
                  </select>
                </div>
              </div>

              {/* Address */}
              <div className="auth-modal__row">
                <div className="auth-modal__group">
                  <label className="auth-modal__label">House No *</label>
                  <input type="text" className="auth-modal__input" placeholder="House/Flat No."
                    value={regData.houseNo} onChange={(e) => setRegData({ ...regData, houseNo: e.target.value })} required />
                </div>
                <div className="auth-modal__group">
                  <label className="auth-modal__label">Street *</label>
                  <input type="text" className="auth-modal__input" placeholder="Street"
                    value={regData.street} onChange={(e) => setRegData({ ...regData, street: e.target.value })} required />
                </div>
              </div>
              <div className="auth-modal__row">
                <div className="auth-modal__group">
                  <label className="auth-modal__label">Area *</label>
                  <input type="text" className="auth-modal__input" placeholder="Area / Locality"
                    value={regData.area} onChange={(e) => setRegData({ ...regData, area: e.target.value })} required />
                </div>
                <div className="auth-modal__group">
                  <label className="auth-modal__label">City *</label>
                  <input type="text" className="auth-modal__input" placeholder="City"
                    value={regData.city} onChange={(e) => setRegData({ ...regData, city: e.target.value })} required />
                </div>
              </div>
              <div className="auth-modal__row">
                <div className="auth-modal__group">
                  <label className="auth-modal__label">State *</label>
                  <input type="text" className="auth-modal__input" placeholder="State"
                    value={regData.state} onChange={(e) => setRegData({ ...regData, state: e.target.value })} required />
                </div>
                <div className="auth-modal__group">
                  <label className="auth-modal__label">Pin Code *</label>
                  <input type="text" className="auth-modal__input" placeholder="PIN Code"
                    value={regData.pinCode} onChange={(e) => setRegData({ ...regData, pinCode: e.target.value })} required />
                </div>
              </div>

              <button type="submit" className="auth-modal__submit" disabled={loading}>
                {loading ? 'Creating account...' : 'Create Account'}
              </button>

              <div className="auth-modal__footer">
                Already have an account?{' '}
                <button type="button" className="auth-modal__switch-link" onClick={() => setAuthTab('login')}>
                  Sign In
                </button>
              </div>
            </form>
          )}
        </div>
      </div>
    </div>
  );
}
