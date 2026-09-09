export const SEED_MOVIES = [
  { movieId:1, title:'Interstellar', poster:'https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg', language:'English', genre:'Sci-Fi / Adventure', duration:169, releaseDate:'2014-11-07', imdbRating:8.9, certificate:'UA_13_PLUS', director:'Christopher Nolan', description:'When Earth becomes uninhabitable, a team of explorers travels through a wormhole near Saturn in search of a new habitable planet for humankind.', cast:['Matthew McConaughey','Anne Hathaway','Jessica Chastain','Michael Caine'] },
  { movieId:2, title:'Dune: Part Two', poster:'https://image.tmdb.org/t/p/w500/1pdfLvkbY9ohJlCjQH2CZjjYVvJ.jpg', language:'English', genre:'Sci-Fi / Drama', duration:166, releaseDate:'2024-03-01', imdbRating:8.6, certificate:'UA_13_PLUS', director:'Denis Villeneuve', description:'Paul Atreides unites with Chani and the Fremen while seeking revenge against the conspirators who destroyed his family, striving to prevent a terrible future.', cast:['Timothée Chalamet','Zendaya','Rebecca Ferguson','Javier Bardem'] },
  { movieId:3, title:'Kalki 2898 AD', poster:'https://image.tmdb.org/t/p/w500/wqnLdwVXoBjKibFRR5U3y0aDUhs.jpg', language:'Telugu', genre:'Sci-Fi / Action', duration:181, releaseDate:'2024-06-27', imdbRating:7.6, certificate:'UA_13_PLUS', director:'Nag Ashwin', description:'A modern avatar of Lord Vishnu descends to protect the world from evil forces in a post-apocalyptic dystopian future setting.', cast:['Prabhas','Amitabh Bachchan','Kamal Haasan','Deepika Padukone'] },
  { movieId:4, title:'Jawan', poster:'https://image.tmdb.org/t/p/w500/40vgCRMNxidkYOWUTqwWi1nBOy1.jpg', language:'Hindi', genre:'Action / Thriller', duration:169, releaseDate:'2023-09-07', imdbRating:7.1, certificate:'UA_13_PLUS', director:'Atlee', description:'A prison warden orchestrates daring social justice heists with a dedicated crew of women inmates to fight systemic corruption.', cast:['Shah Rukh Khan','Nayanthara','Vijay Sethupathi','Deepika Padukone'] },
  { movieId:5, title:'Avatar: The Way of Water', poster:'https://image.tmdb.org/t/p/w500/t6HIqrRAclMCA60NsSmeqe9RmNV.jpg', language:'English', genre:'Sci-Fi / Adventure', duration:192, releaseDate:'2022-12-16', imdbRating:7.6, certificate:'UA_13_PLUS', director:'James Cameron', description:'Jake Sully and Neytiri have formed a family and are doing everything to stay together. However, they must leave their home and explore the regions of Pandora.', cast:['Sam Worthington','Zoe Saldana','Sigourney Weaver','Stephen Lang'] },
  { movieId:6, title:'Oppenheimer', poster:'https://image.tmdb.org/t/p/w500/8Gxv8gSFCU0XGDykEGv7zR1n2ua.jpg', language:'English', genre:'Drama / History', duration:180, releaseDate:'2023-07-21', imdbRating:8.9, certificate:'A', director:'Christopher Nolan', description:'The story of American scientist J. Robert Oppenheimer and his role in the development of the atomic bomb during World War II.', cast:['Cillian Murphy','Emily Blunt','Matt Damon','Robert Downey Jr.'] },
  { movieId:7, title:'The Dark Knight', poster:'https://image.tmdb.org/t/p/w500/qJ2tW6WMUDux911r6m7haRef0WH.jpg', language:'English', genre:'Action / Thriller', duration:152, releaseDate:'2008-07-18', imdbRating:9.0, certificate:'UA_13_PLUS', director:'Christopher Nolan', description:'When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.', cast:['Christian Bale','Heath Ledger','Aaron Eckhart','Michael Caine'] },
  { movieId:8, title:'The Matrix', poster:'https://image.tmdb.org/t/p/w500/f89U3ADr1oiB1s9GkdPOEpXUk5H.jpg', language:'English', genre:'Action / Sci-Fi', duration:136, releaseDate:'1999-03-31', imdbRating:8.7, certificate:'A', director:'Lana Wachowski, Lilly Wachowski', description:'A computer hacker learns from mysterious rebels about the true nature of his reality and his role in the war against its controllers.', cast:['Keanu Reeves','Laurence Fishburne','Carrie-Anne Moss','Hugo Weaving'] }
];

export const SEED_THEATRES = [

  { theatreId:1, name:'Galaxy Cinemas – IP Sigra Mall', buildingName:'IP Grand Mall, Level 3', street:'Vidyapeeth Road', area:'Sigra', city:'Varanasi', state:'Uttar Pradesh', pinCode:'221010' },
  { theatreId:2, name:'Galaxy Cinemas – JHV Mall', buildingName:'JHV Mall, 4th Floor', street:'Mall Road', area:'Cantonment', city:'Varanasi', state:'Uttar Pradesh', pinCode:'221002' },

  { theatreId:3, name:'Galaxy Cinemas – Orion Grand', buildingName:'Orion Mall, Level 4', street:'Dr. Rajkumar Road', area:'Rajajinagar', city:'Bengaluru', state:'Karnataka', pinCode:'560055' },
  { theatreId:4, name:'Galaxy Cinemas – Phoenix Marketcity', buildingName:'Phoenix Marketcity, 2nd Floor', street:'Whitefield Main Road', area:'Mahadevapura', city:'Bengaluru', state:'Karnataka', pinCode:'560048' },

  { theatreId:5, name:'Galaxy Cinemas – Phoenix Palladium', buildingName:'Phoenix Palladium, Level 3', street:'Senapati Bapat Marg', area:'Lower Parel', city:'Mumbai', state:'Maharashtra', pinCode:'400013' },
  { theatreId:6, name:'Galaxy Cinemas – Infiniti Mall', buildingName:'Infiniti Mall, 3rd Floor', street:'Link Road', area:'Malad West', city:'Mumbai', state:'Maharashtra', pinCode:'400064' },

  { theatreId:7, name:'Galaxy Cinemas – Select Citywalk', buildingName:'Select Citywalk, Level 2', street:'District Centre Saket', area:'Saket', city:'Delhi-NCR', state:'Delhi', pinCode:'110017' },
  { theatreId:8, name:'Galaxy Cinemas – Ambience Mall', buildingName:'Ambience Mall, 3rd Floor', street:'NH-8', area:'DLF Phase 3', city:'Delhi-NCR', state:'Haryana', pinCode:'122002' },

  { theatreId:9, name:'Galaxy Cinemas – Inorbit Mall', buildingName:'Inorbit Mall, Level 3', street:'Hitec City Road', area:'Madhapur', city:'Hyderabad', state:'Telangana', pinCode:'500081' },
  { theatreId:10, name:'Galaxy Cinemas – Forum Sujana', buildingName:'Forum Sujana Mall, 5th Floor', street:'KPHB Phase 9', area:'Kukatpally', city:'Hyderabad', state:'Telangana', pinCode:'500072' },

  { theatreId:11, name:'Galaxy Cinemas – Phoenix Marketcity', buildingName:'Phoenix Marketcity, Level 3', street:'Viman Nagar Road', area:'Viman Nagar', city:'Pune', state:'Maharashtra', pinCode:'411014' },
  { theatreId:12, name:'Galaxy Cinemas – Seasons Mall', buildingName:'Seasons Mall, 2nd Floor', street:'Magarpatta City', area:'Hadapsar', city:'Pune', state:'Maharashtra', pinCode:'411028' },

  { theatreId:13, name:'Galaxy Cinemas – Express Avenue', buildingName:'Express Avenue Mall, Level 3', street:'Whites Road', area:'Royapettah', city:'Chennai', state:'Tamil Nadu', pinCode:'600014' },
  { theatreId:14, name:'Galaxy Cinemas – Phoenix MarketCity', buildingName:'Phoenix MarketCity, Level 2', street:'Velachery Main Road', area:'Velachery', city:'Chennai', state:'Tamil Nadu', pinCode:'600042' },

  { theatreId:15, name:'Galaxy Cinemas – South City Mall', buildingName:'South City Mall, 3rd Floor', street:'Prince Anwar Shah Road', area:'Jadavpur', city:'Kolkata', state:'West Bengal', pinCode:'700068' },
  { theatreId:16, name:'Galaxy Cinemas – Quest Mall', buildingName:'Quest Mall, Level 4', street:'Syed Amir Ali Avenue', area:'Ballygunge', city:'Kolkata', state:'West Bengal', pinCode:'700017' }
];

export const SEED_SCREENS = [];
let screenCounter = 1;
for (const theatre of SEED_THEATRES) {
  SEED_SCREENS.push(
    { screenId: screenCounter++, theatreId: theatre.theatreId, name: 'Screen 1', screenType: 'IMAX', capacity: 60 },
    { screenId: screenCounter++, theatreId: theatre.theatreId, name: 'Screen 2', screenType: '3D', capacity: 60 },
    { screenId: screenCounter++, theatreId: theatre.theatreId, name: 'Screen 3', screenType: '2D', capacity: 80 }
  );
}

export function formatLocalDate(d) {
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

export function generateShows(movies, screens) {
  const shows = [];
  let showId = 1;
  const today = new Date();

  const showConfigs = [
    { time: '10:15:00', price: 180 },
    { time: '13:45:00', price: 220 },
    { time: '17:15:00', price: 280 },
    { time: '20:45:00', price: 350 }
  ];

  for (let dayOffset = -1; dayOffset <= 14; dayOffset++) {
    const d = new Date(today);
    d.setDate(today.getDate() + dayOffset);
    const dateStr = formatLocalDate(d);

    for (const screen of screens) {

      for (let s = 0; s < showConfigs.length; s++) {
        const movieIndex = (screen.screenId * 3 + dayOffset * 2 + s) % movies.length;
        const movie = movies[movieIndex >= 0 ? movieIndex : 0];
        const config = showConfigs[s];

        shows.push({
          showId: showId++,
          movieId: movie.movieId,
          theatreId: screen.theatreId,
          screenId: screen.screenId,
          showDate: dateStr,
          showTime: config.time,
          ticketPrice: config.price,
          screenType: screen.screenType,
          movieTitle: movie.title,
          language: movie.language,
          availableSeats: screen.capacity
        });
      }
    }
  }

  return shows;
}

export function generateSeats(capacity = 60) {
  const seats = [];
  const rows = ['A','B','C','D','E','F','G','H'];
  let seatId = 1;
  let cols = Math.ceil(capacity / rows.length);
  if (cols < 6) cols = 6;
  let added = 0;

  for (const row of rows) {
    for (let c = 1; c <= cols; c++) {
      if (added >= capacity) break;

      const status = Math.random() < 0.15 ? 'BOOKED' : 'AVAILABLE';
      seats.push({
        seatId: seatId++,
        rowNo: row,
        seatNo: c,
        status: status
      });
      added++;
    }
    if (added >= capacity) break;
  }
  return seats;
}
