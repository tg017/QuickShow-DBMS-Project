import { useApp } from '../context/AppContext';

export default function Footer() {
  const { showToast } = useApp();

  return (
    <footer className="footer">
      <div className="footer__content">
        <div className="footer__about">About Galaxy Cinemas</div>

        <div className="footer__contact">
          <span>Contact Us</span>
          <div className="footer__socials">
            <a 
              href="https://facebook.com" 
              target="_blank" 
              rel="noopener noreferrer" 
              className="footer__social-link" 
              title="Facebook" 
              onClick={(e) => { e.preventDefault(); showToast('Opening Galaxy Cinemas Facebook page...', 'info'); }}
            >
              <svg width="16" height="16" fill="currentColor" viewBox="0 0 24 24">
                <path d="M18 2h-3a5 5 0 00-5 5v3H7v4h3v8h4v-8h3l1-4h-4V7a1 1 0 011-1h3z"/>
              </svg>
            </a>
            <a 
              href="https://x.com" 
              target="_blank" 
              rel="noopener noreferrer" 
              className="footer__social-link" 
              title="X (Twitter)" 
              onClick={(e) => { e.preventDefault(); showToast('Opening Galaxy Cinemas X feed...', 'info'); }}
            >
              <svg width="14" height="14" fill="currentColor" viewBox="0 0 24 24">
                <path d="M18.244 2.25h3.308l-7.227 8.26 8.502 11.24H16.17l-5.214-6.817L4.99 21.75H1.68l7.73-8.835L1.254 2.25H8.08l4.713 6.231zm-1.161 17.52h1.833L7.084 4.126H5.117z"/>
              </svg>
            </a>
            <a 
              href="https://instagram.com" 
              target="_blank" 
              rel="noopener noreferrer" 
              className="footer__social-link" 
              title="Instagram" 
              onClick={(e) => { e.preventDefault(); showToast('Opening Galaxy Cinemas Instagram...', 'info'); }}
            >
              <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                <rect x="2" y="2" width="20" height="20" rx="5" ry="5"/>
                <path d="M16 11.37A4 4 0 1112.63 8 4 4 0 0116 11.37z"/>
                <line x1="17.5" y1="6.5" x2="17.51" y2="6.5"/>
              </svg>
            </a>
            <a 
              href="https://youtube.com" 
              target="_blank" 
              rel="noopener noreferrer" 
              className="footer__social-link" 
              title="YouTube" 
              onClick={(e) => { e.preventDefault(); showToast('Opening Galaxy Cinemas YouTube channel...', 'info'); }}
            >
              <svg width="16" height="16" fill="currentColor" viewBox="0 0 24 24">
                <path d="M23.498 6.186a3.016 3.016 0 00-2.122-2.136C19.505 3.545 12 3.545 12 3.545s-7.505 0-9.377.505A3.017 3.017 0 00.502 6.186C0 8.07 0 12 0 12s0 3.93.502 5.814a3.016 3.016 0 002.122 2.136c1.871.505 9.376.505 9.376.505s7.505 0 9.377-.505a3.015 3.015 0 002.122-2.136C24 15.93 24 12 24 12s0-3.93-.502-5.814zM9.545 15.568V8.432L15.818 12l-6.273 3.568z"/>
              </svg>
            </a>
          </div>
        </div>

        <button 
          className="footer__app-btn" 
          onClick={() => showToast('QuickShow Mobile App coming soon to iOS & Android!', 'info')}
        >
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <rect x="5" y="2" width="14" height="20" rx="2" ry="2"/>
            <line x1="12" y1="18" x2="12.01" y2="18"/>
          </svg>
          <div>
            <div style={{ fontSize: '10px', color: 'var(--text-muted)' }}>Download the</div>
            <strong style={{ color: 'var(--text-primary)' }}>Quick Mobile App</strong>
          </div>
        </button>
      </div>
    </footer>
  );
}
