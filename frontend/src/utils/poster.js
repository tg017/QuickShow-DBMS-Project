export const DEFAULT_POSTER = 'https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg';

/**
 * Returns a valid image URL for the movie poster, or falls back to the default poster.
 * @param {string} url - Poster URL from the database or movie object.
 * @returns {string} - Cleaned URL or fallback poster URL.
 */
export const getPosterSrc = (url) => {
  if (!url || typeof url !== 'string' || !url.trim()) {
    return DEFAULT_POSTER;
  }
  return url.trim();
};

/**
 * Image onError handler to replace broken/failing poster links with the fallback poster
 * and prevent infinite error looping.
 * @param {Event} event - Image error event.
 */
export const handlePosterError = (event) => {
  if (event?.target && event.target.src !== DEFAULT_POSTER) {
    event.target.src = DEFAULT_POSTER;
  }
};
