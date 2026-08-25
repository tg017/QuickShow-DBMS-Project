package quickshow.dbms.project.service;

import org.springframework.stereotype.Service;
import quickshow.dbms.project.dto.TheatreListDTO;
import quickshow.dbms.project.dto.TheatreShowDTO;
import quickshow.dbms.project.exception.ResourceNotFoundException;
import quickshow.dbms.project.repository.TheatreRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class TheatreService {

    private final TheatreRepository theatreRepository;

    public TheatreService(TheatreRepository theatreRepository) {
        this.theatreRepository = theatreRepository;
    }

    public List<TheatreListDTO> getTheatresWithScheduledShows() {

        return theatreRepository.findTheatresWithScheduledShows();
    }

    public List<TheatreListDTO> getTheatresByCity(
            String city
    ) {

        if (city == null || city.isBlank()) {
            throw new ResourceNotFoundException(
                    "City cannot be empty"
            );
        }

        return theatreRepository.findTheatresByCity(
                city.trim()
        );
    }

    public List<TheatreShowDTO> getShowsByTheatreAndDate(
            Integer theatreId,
            LocalDate date
    ) {

        if (theatreId == null) {
            throw new ResourceNotFoundException(
                    "Theatre ID cannot be null"
            );
        }

        if (date == null) {
            date = LocalDate.now();
        }

        return theatreRepository.findShowsByTheatreAndDate(
                theatreId,
                date
        );
    }
}