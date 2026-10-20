import { useState, useEffect } from 'react';
import { useApp } from '../context/AppContext';
import { getProfile } from '../api/api';

export default function Profile() {
  const { user, logoutUser, setAuthModalOpen, setAuthTab, navigateTo } = useApp();
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!user) {
      setAuthTab('login');
      setAuthModalOpen(true);
      return;
    }
    setAuthModalOpen(false);
  }, [user, setAuthModalOpen, setAuthTab]);

  useEffect(() => {
    if (user) {
      setLoading(true);
      getProfile()
        .then((data) => {
          setProfile({ ...(data || user), email: user?.email || data?.email });
          setLoading(false);
        })
        .catch(() => {
          setProfile(user);
          setLoading(false);
        });
    }
  }, [user]);

  if (!user) {
    return (
      <div className="profile-page profile-page--redirecting">
        <div className="spinner" />
      </div>
    );
  }

  if (loading) {
    return <div className="spinner" style={{ margin: '60px auto' }}></div>;
  }

  const p = profile || user;
  const initials = (((p.firstName?.[0] || '') + (p.lastName?.[0] || '')).toUpperCase()) || 'U';

  return (
    <div className="profile-page">
      <div className="profile-page__intro">
        <div>
          <div className="profile-page__eyebrow">ACCOUNT CENTER</div>
          <h1>My Profile</h1>
          <p>Manage your personal details and bookings.</p>
        </div>
      </div>
      <div className="profile-view">
      <div className="profile-view__header">
        <div className="profile-view__avatar">{initials}</div>
        <div>
          <div className="profile-view__name">{p.firstName || ''} {p.lastName || ''}</div>
          <div className="profile-view__email">{p.email || ''}</div>
        </div>
      </div>

      <div className="profile-view__card">
        <h3 className="profile-view__card-title">Personal Information</h3>
        <div className="profile-view__grid">
          <div>
            <div className="profile-view__field-label">Email</div>
            <div className="profile-view__field-value">{p.email || '-'}</div>
          </div>
          <div>
            <div className="profile-view__field-label">Phone</div>
            <div className="profile-view__field-value">{p.phoneNo || '-'}</div>
          </div>
          <div>
            <div className="profile-view__field-label">Date of Birth</div>
            <div className="profile-view__field-value">{p.dob || '-'}</div>
          </div>
          <div>
            <div className="profile-view__field-label">Gender</div>
            <div className="profile-view__field-value">{p.gender || '-'}</div>
          </div>
        </div>
      </div>

      <div className="profile-view__card">
        <h3 className="profile-view__card-title">Address Details</h3>
        <div className="profile-view__grid">
          <div>
            <div className="profile-view__field-label">House No</div>
            <div className="profile-view__field-value">{p.houseNo || p.address?.houseNo || '-'}</div>
          </div>
          <div>
            <div className="profile-view__field-label">Street</div>
            <div className="profile-view__field-value">{p.street || p.address?.street || '-'}</div>
          </div>
          <div>
            <div className="profile-view__field-label">Area</div>
            <div className="profile-view__field-value">{p.area || p.address?.area || '-'}</div>
          </div>
          <div>
            <div className="profile-view__field-label">City</div>
            <div className="profile-view__field-value">{p.city || p.address?.city || '-'}</div>
          </div>
          <div>
            <div className="profile-view__field-label">State</div>
            <div className="profile-view__field-value">{p.state || p.address?.state || '-'}</div>
          </div>
          <div>
            <div className="profile-view__field-label">Pin Code</div>
            <div className="profile-view__field-value">{p.pinCode || p.address?.pinCode || '-'}</div>
          </div>
        </div>
      </div>

      <div style={{ marginTop: '24px', display: 'flex', gap: '12px', flexWrap: 'wrap' }}>
        <button className="add-btn" onClick={() => navigateTo('bookings')}>
          My Bookings
        </button>
        <button className="profile-view__signout" onClick={logoutUser}>
          Sign Out
        </button>
      </div>
      </div>
    </div>
  );
}
