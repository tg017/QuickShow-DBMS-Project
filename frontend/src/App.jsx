import { AppProvider, useApp } from './context/AppContext';
import Topbar from './components/Topbar';
import Footer from './components/Footer';
import AuthModal from './components/AuthModal';

import Home from './pages/Home';
import Movies from './pages/Movies';
import MovieDetail from './pages/MovieDetail';
import SeatLayout from './pages/SeatLayout';
import Checkout from './pages/Checkout';
import Confirmation from './pages/Confirmation';
import Bookings from './pages/Bookings';
import Profile from './pages/Profile';
import Theatres from './pages/Theatres';
import TheatreDetail from './pages/TheatreDetail';
import Admin from './pages/Admin';

function MainLayout() {
  const { currentRoute, toast } = useApp();

  const renderRoute = () => {

    if (currentRoute === '' || currentRoute === 'home') {
      return <Home />;
    }

    if (currentRoute === 'movies') {
      return <Movies />;
    }
    if (currentRoute.startsWith('movie/')) {
      const id = currentRoute.split('/')[1];
      return <MovieDetail movieId={id} />;
    }

    if (currentRoute.match(/^shows\/(.*)\/seats/)) {
      const id = currentRoute.split('/')[1];
      return <SeatLayout showId={id} />;
    }

    if (currentRoute === 'payment') {
      return <Checkout />;
    }

    if (currentRoute.match(/^booking\/(.*)\/confirmation/)) {
      const id = currentRoute.split('/')[1];
      return <Confirmation bookingId={id} />;
    }

    if (currentRoute === 'bookings') {
      return <Bookings />;
    }

    if (currentRoute === 'profile') {
      return <Profile />;
    }

    if (currentRoute === 'theatres') {
      return <Theatres />;
    }

    if (currentRoute.startsWith('theatre/')) {
      return <TheatreDetail theatreId={currentRoute.split('/')[1]} />;
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

      <div className="app-shell no-sidebar">
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
