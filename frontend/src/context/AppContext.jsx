import { createContext, useContext, useState, useEffect } from 'react';

const AppContext = createContext();

export function AppProvider({ children }) {

  const [user, setUser] = useState(() => {
    try { return JSON.parse(localStorage.getItem('quickshow_user')); } catch { return null; }
  });

  const [admin, setAdmin] = useState(() => {
    try { return JSON.parse(localStorage.getItem('quickshow_admin')); } catch { return null; }
  });

  const [selectedCity, setSelectedCity] = useState(localStorage.getItem('qs_city') || 'Varanasi');
  const [selectedDate, setSelectedDate] = useState(new Date().toISOString().split('T')[0]);
  const [filters, setFilters] = useState({ language: '', genre: '', rating: '' });
  const [currentRoute, setCurrentRoute] = useState(window.location.hash.slice(1) || 'home');
  const [authModalOpen, setAuthModalOpen] = useState(false);
  const [authTab, setAuthTab] = useState('login');
  const [toast, setToast] = useState(null);

  const showToast = (message, type = 'info') => {
    setToast({ message, type });
    setTimeout(() => setToast(null), 3500);
  };

  const navigateTo = (route) => {
    window.location.hash = route;
    setCurrentRoute(route);
  };

  useEffect(() => {
    const handleHashChange = () => setCurrentRoute(window.location.hash.slice(1) || 'home');
    window.addEventListener('hashchange', handleHashChange);
    return () => window.removeEventListener('hashchange', handleHashChange);
  }, []);

  const loginUser = (userData) => {
    setUser(userData);
    localStorage.setItem('quickshow_user', JSON.stringify(userData));
  };

  const logoutUser = () => {
    setUser(null);
    setAdmin(null);
    localStorage.removeItem('quickshow_user');
    localStorage.removeItem('quickshow_admin');
    navigateTo('home');
    showToast('Logged out successfully');
  };

  const changeCity = (city) => {
    setSelectedCity(city);
    localStorage.setItem('qs_city', city);
  };

  return (
    <AppContext.Provider value={{
      user, loginUser, logoutUser,
      admin, setAdmin,
      selectedCity, changeCity,
      selectedDate, setSelectedDate,
      filters, setFilters,
      currentRoute, navigateTo,
      authModalOpen, setAuthModalOpen,
      authTab, setAuthTab,
      toast, showToast,
    }}>
      {children}
    </AppContext.Provider>
  );
}

export const useApp = () => useContext(AppContext);
