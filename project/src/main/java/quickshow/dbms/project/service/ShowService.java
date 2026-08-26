package quickshow.dbms.project.service;

import org.springframework.stereotype.Service;
import quickshow.dbms.project.dto.*;
import quickshow.dbms.project.exception.ResourceNotFoundException;
import quickshow.dbms.project.repository.ShowRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShowService {
    private final ShowRepository showRepository;

    public ShowService(ShowRepository showRepository) {
        this.showRepository = showRepository;
    }

    public List<TheatreShowsDTO> getShowsByMovieAndDate(
            Integer movieId,
            LocalDate date
    ) {

        if (movieId == null) {
            throw new ResourceNotFoundException(
                    "Movie ID cannot be null"
            );
        }

        if (date == null) {
            date = LocalDate.now();
        }

        List<MovieShowWithTheatreDTO> rows =
                showRepository.findShowsByMovieAndDate(
                        movieId,
                        date
                );

        Map<Integer, List<MovieShowDTO>> grouped =
                new LinkedHashMap<>();

        Map<Integer, String> theatreNames =
                new LinkedHashMap<>();


        // =========================================================
        // GROUP SHOWS BY THEATRE
        // =========================================================

        for (MovieShowWithTheatreDTO row : rows) {

            MovieShowDTO show = new MovieShowDTO(
                    row.getShowId(),
                    row.getMovieTitle(),
                    row.getLanguage(),
                    row.getScreenType(),
                    row.getAvailableSeats(),
                    row.getTicketPrice(),
                    row.getShowTime()
            );

            grouped
                    .computeIfAbsent(
                            row.getTheatreId(),
                            key -> new ArrayList<>()
                    )
                    .add(show);

            theatreNames.put(
                    row.getTheatreId(),
                    row.getTheatreName()
            );
        }


        // =========================================================
        // BUILD FINAL RESPONSE
        // =========================================================

        List<TheatreShowsDTO> result =
                new ArrayList<>();

        for (Map.Entry<Integer, List<MovieShowDTO>> entry
                : grouped.entrySet()) {

            Integer theatreId =
                    entry.getKey();

            String theatreName =
                    theatreNames.get(theatreId);

            result.add(
                    new TheatreShowsDTO(
                            theatreId,
                            theatreName,
                            entry.getValue()
                    )
            );
        }

        return result;
    }

    public SeatLayoutDTO getSeatLayout(
            Integer showId
    ) {

        if (showId == null) {
            throw new ResourceNotFoundException(
                    "Show ID cannot be null"
            );
        }

        SeatLayoutDTO seatLayout =
                showRepository.findSeatLayoutShow(
                        showId
                );

        if (seatLayout == null) {
            throw new ResourceNotFoundException(
                    "Show not found"
            );
        }

        List<SeatDTO> seats =
                showRepository.findSeatsByShowId(
                        showId
                );

        return new SeatLayoutDTO(
                seatLayout.getShowId(),
                seatLayout.getMovie(),
                seatLayout.getTheatre(),
                seatLayout.getScreen(),
                seatLayout.getShowDate(),
                seatLayout.getShowTime(),
                seatLayout.getTicketPrice(),
                seatLayout.getAvailableSeats(),
                seatLayout.getShowStatus(),
                seats
        );
    }
}
