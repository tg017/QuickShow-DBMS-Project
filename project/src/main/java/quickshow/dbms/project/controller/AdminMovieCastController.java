package quickshow.dbms.project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import quickshow.dbms.project.dto.AddActorDTO;
import quickshow.dbms.project.model.MovieCast;
import quickshow.dbms.project.service.AdminMovieCastService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/movies/{movieId}/cast")
public class AdminMovieCastController {

    private final AdminMovieCastService adminMovieCastService;

    public AdminMovieCastController(
            AdminMovieCastService adminMovieCastService
    ) {
        this.adminMovieCastService =
                adminMovieCastService;
    }


    // =========================================================
    // GET CAST
    //
    // GET /api/admin/movies/{movieId}/cast
    // =========================================================

    @GetMapping
    public ResponseEntity<List<MovieCast>> getMovieCast(
            @PathVariable Integer movieId
    ) {

        // First verify movie exists
        // so that a nonexistent movie isn't treated
        // as simply having an empty cast.

        List<MovieCast> cast =
                adminMovieCastService.getMovieCast(movieId);

        return ResponseEntity.ok(cast);
    }


    // =========================================================
    // ADD ACTOR
    //
    // POST /api/admin/movies/{movieId}/cast
    // =========================================================

    @PostMapping
    public ResponseEntity<String> addActor(
            @PathVariable Integer movieId,
            @RequestBody AddActorDTO request
    ) {

        String result =
                adminMovieCastService.addActor(
                        movieId,
                        request.getActor()
                );

        if ("MOVIE_NOT_FOUND".equals(result)) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        if ("INVALID_ACTOR".equals(result)) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Actor name cannot be empty."
                    );
        }

        if ("ACTOR_EXISTS".equals(result)) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(
                            "Actor already exists in movie cast."
                    );
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        "Actor added successfully."
                );
    }


    // =========================================================
    // DELETE ACTOR
    //
    // DELETE /api/admin/movies/{movieId}/cast/{actor}
    // =========================================================

    @DeleteMapping("/{actor}")
    public ResponseEntity<Void> deleteActor(
            @PathVariable Integer movieId,
            @PathVariable String actor
    ) {

        boolean deleted =
                adminMovieCastService.deleteActor(
                        movieId,
                        actor
                );

        if (!deleted) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity
                .noContent()
                .build();
    }
}