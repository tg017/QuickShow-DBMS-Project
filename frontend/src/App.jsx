import { AppProvider, useApp } from './context/AppContext';
import Topbar from './components/Topbar';
import Sidebar from './components/Sidebar';
import Footer from './components/Footer';
import AuthModal from './components/AuthModal';

import Home from './pages/Home';
import MovieDetail from './pages/MovieDetail';
import SeatLayout from './pages/SeatLayout';
import Checkout from './pages/Checkout';
import Confirmation from './pages/Confirmation';
import Bookings from './pages/Bookings';
import Profile from './pages/Profile';
import Theatres from './pages/Theatres';
import Admin from './pages/Admin';

function MainLayout() {
  const { currentRoute, toast } = useApp();

  const hideSidebar = currentRoute.startsWith('admin') || 
                      currentRoute.includes('/seats') || 
                      currentRoute.startsWith('payment') || 
                      currentRoute.includes('/confirmation');

  const renderRoute = () => {

    if (currentRoute === '' || currentRoute === 'home' || currentRoute === 'movies') {
      return <Home />;
    }

    if (currentRoute.startsWith('movie/')) {
      const id = currentRoute.split('/')[1];
      return <MovieDetail movieId={id} />;
    }

    // Seat Layout: #shows/:id/seats
    if (currentRoute.match(/^shows\/(.*)\/seats/)) {
      const id = currentRoute.split('/')[1];
      return <SeatLayout showId={id} />;
    }

    // Payment / Checkout: #payment
    if (currentRoute === 'payment') {
      return <Checkout />;
    }

    if (currentRoute.match(/^booking\/(.*)\/confirmation/)) {
      const id = currentRoute.split('/')[1];
      return <Confirmation bookingId={id} />;
    }

    // My Bookings: #bookings
    if (currentRoute === 'bookings') {
      return <Bookings />;
    }

    if (currentRoute === 'profile') {
      return <Profile />;
    }

    if (currentRoute === 'theatres' || currentRoute === 'showtimes') {
      return <Theatres />;
    }

    if (currentRoute === 'admin') {
      return <Admin />;
    }

    return (
      <div style={{ padding: '60px', textAlign: 'center', color: 'var(--text-secondary)' }}>
        <h2>404 - Page Not Found</h2>
        <p style={{ marginTop: '8px' }}>The requested route <code>#{currentRoute}</code> does not exist.</p>
        <a href="#home" className="movie-card__book-btn" style={{ display: 'inline-block', marginTop: '16px', textDecoration: 'none' }}>
          Back to Home
        </a>
      </div>
    );
  };

  return (
    <>
      <Topbar />

      <div className={`app-shell ${hideSidebar ? 'no-sidebar' : ''}`}>
        {!hideSidebar && <Sidebar />}

        <main className="main-content">
          {renderRoute()}
        </main>
      </div>

      <Footer />
      <AuthModal />

      {toast && (
        <div className="toast-container">
          <div className={`toast toast--${toast.type}`}>{toast.message}</div>
        </div>
      )}
    </>
  );
}

export default function App() {
  return (
    <AppProvider>
      <MainLayout />
    </AppProvider>
  );
}
