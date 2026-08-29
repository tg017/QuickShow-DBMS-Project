package quickshow.dbms.project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import quickshow.dbms.project.dto.*;
import quickshow.dbms.project.service.AdminScreenService;
import quickshow.dbms.project.service.AdminService;
import quickshow.dbms.project.service.AdminTheatreService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final AdminTheatreService adminTheatreService;
    private final AdminScreenService adminScreenService;

    public AdminController(
            AdminService adminService,
            AdminTheatreService adminTheatreService,
            AdminScreenService adminScreenService
    ) {
        this.adminService = adminService;
        this.adminTheatreService = adminTheatreService;
        this.adminScreenService = adminScreenService;
    }


    // =========================================================
    // REGISTER
    // =========================================================

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody AdminRegisterRequestDTO request
    ) {

        adminService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        "Admin registered successfully"
                );
    }


    // =========================================================
    // LOGIN
    // =========================================================

    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponseDTO> login(
            @RequestBody LoginRequestDTO request
    ) {

        AdminLoginResponseDTO response =
                adminService.login(
                        request.getEmail(),
                        request.getPassword()
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/theatres")
    public ResponseEntity<List<TheatreListDTO>> getAllTheatres() {

        return ResponseEntity.ok(
                adminTheatreService.getAllTheatres()
        );
    }

    @GetMapping("/theatres/{theatreId}")
    public ResponseEntity<TheatreListDTO> getTheatreById(
            @PathVariable Integer theatreId
    ) {

        TheatreListDTO theatre =
                adminTheatreService.getTheatreById(
                        theatreId
                );

        if (theatre == null) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(theatre);
    }

    @PostMapping("/theatres")
    public ResponseEntity<TheatreListDTO> createTheatre(
            @RequestBody TheatreListDTO theatre
    ) {

        TheatreListDTO created =
                adminTheatreService.createTheatre(
                        theatre
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @PutMapping("/theatres/{theatreId}")
    public ResponseEntity<TheatreListDTO> updateTheatre(
            @PathVariable Integer theatreId,
            @RequestBody TheatreListDTO theatre
    ) {

        TheatreListDTO updated =
                adminTheatreService.updateTheatre(
                        theatreId,
                        theatre
                );

        if (updated == null) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/theatres/{theatreId}")
    public ResponseEntity<String> deleteTheatre(
            @PathVariable Integer theatreId
    ) {

        String result =
                adminTheatreService.deleteTheatre(
                        theatreId
                );

        if ("NOT_FOUND".equals(result)) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        if ("HAS_SHOWS".equals(result)) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(
                            "Theatre cannot be deleted because it has associated shows."
                    );
        }

        if ("HAS_SCREENS".equals(result)) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(
                            "Theatre cannot be deleted because it has associated screens."
                    );
        }

        return ResponseEntity.ok(
                "Theatre deleted successfully."
        );
    }

    @GetMapping("/theatres/{theatreId}/screens")
    public ResponseEntity<List<AdminScreenDTO>> getScreensByTheatre(
            @PathVariable Integer theatreId
    ) {

        if (adminTheatreService.getTheatreById(
                theatreId
        ) == null) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(
                adminScreenService.getScreensByTheatre(
                        theatreId
                )
        );
    }

    @GetMapping("/screens/{screenId}")
    public ResponseEntity<AdminScreenDTO> getScreenById(
            @PathVariable Integer screenId
    ) {

        AdminScreenDTO screen =
                adminScreenService.getScreenById(
                        screenId
                );

        if (screen == null) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(screen);
    }

    @PostMapping("/theatres/{theatreId}/screens")
    public ResponseEntity<AdminScreenDTO> createScreen(
            @PathVariable Integer theatreId,
            @RequestBody AdminScreenDTO screen
    ) {

        AdminScreenDTO created =
                adminScreenService.createScreen(
                        theatreId,
                        screen
                );

        if (created == null) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @PutMapping("/screens/{screenId}")
    public ResponseEntity<AdminScreenDTO> updateScreen(
            @PathVariable Integer screenId,
            @RequestBody AdminScreenDTO screen
    ) {

        AdminScreenDTO updated =
                adminScreenService.updateScreen(
                        screenId,
                        screen
                );

        if (updated == null) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/screens/{screenId}")
    public ResponseEntity<String> deleteScreen(
            @PathVariable Integer screenId
    ) {

        String result =
                adminScreenService.deleteScreen(
                        screenId
                );

        if ("NOT_FOUND".equals(result)) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        if ("HAS_SHOWS".equals(result)) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(
                            "Screen cannot be deleted because it has associated shows."
                    );
        }

        if ("HAS_SEATS".equals(result)) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(
                            "Screen cannot be deleted because it has associated seats."
                    );
        }

        return ResponseEntity.ok(
                "Screen deleted successfully."
        );
    }
}