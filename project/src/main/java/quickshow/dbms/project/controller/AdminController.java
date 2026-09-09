package quickshow.dbms.project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import quickshow.dbms.project.dto.*;
import quickshow.dbms.project.service.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final AdminTheatreService adminTheatreService;
    private final AdminScreenService adminScreenService;
    private final AdminShowService adminShowService;
    private final AdminPaymentService adminPaymentService;
    private final AdminBookingService adminBookingService;

    public AdminController(
            AdminService adminService,
            AdminTheatreService adminTheatreService,
            AdminScreenService adminScreenService,
            AdminShowService adminShowService,
            AdminPaymentService adminPaymentService,
            AdminBookingService adminBookingService
    ) {

        this.adminService =
                adminService;

        this.adminTheatreService =
                adminTheatreService;

        this.adminScreenService =
                adminScreenService;

        this.adminShowService =
                adminShowService;

        this.adminPaymentService =
                adminPaymentService ;

        this.adminBookingService =
                adminBookingService ;
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

    // =========================================================
// SHOWS
// =========================================================

    @GetMapping("/shows")
    public ResponseEntity<List<AdminShowDTO>> getAllShows() {

        return ResponseEntity.ok(
                adminShowService.getAllShows()
        );
    }


    @GetMapping("/shows/{showId}")
    public ResponseEntity<AdminShowDTO> getShowById(
            @PathVariable Integer showId
    ) {

        AdminShowDTO show =
                adminShowService.getShowById(
                        showId
                );

        if (show == null) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(show);
    }


    @PostMapping("/shows")
    public ResponseEntity<AdminShowDTO> createShow(
            @RequestBody AdminShowDTO show
    ) {

        AdminShowDTO created =
                adminShowService.createShow(
                        show
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


    @PutMapping("/shows/{showId}")
    public ResponseEntity<AdminShowDTO> updateShow(
            @PathVariable Integer showId,
            @RequestBody AdminShowDTO show
    ) {

        AdminShowDTO updated =
                adminShowService.updateShow(
                        showId,
                        show
                );

        if (updated == null) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/shows/{showId}")
    public ResponseEntity<String> deleteShow(
            @PathVariable Integer showId
    ) {

        String result =
                adminShowService.deleteShow(
                        showId
                );

        if ("NOT_FOUND".equals(result)) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        if ("HAS_BOOKED_SEATS".equals(result)) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(
                            "Show cannot be deleted because seats have already been booked."
                    );
        }

        if ("HAS_BOOKING_RECORDS".equals(result)) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(
                            "Show cannot be deleted because booking records exist for this show."
                    );
        }

        return ResponseEntity.ok(
                "Show deleted successfully."
        );
    }

    @GetMapping("/payments")
    public ResponseEntity<List<AdminPaymentDTO>> getAllPayments() {

        return ResponseEntity.ok(
                adminPaymentService.getAllPayments()
        );
    }

    @GetMapping("/payments/{paymentId}")
    public ResponseEntity<AdminPaymentDTO> getPaymentById(
            @PathVariable Integer paymentId
    ) {

        AdminPaymentDTO payment =
                adminPaymentService.getPaymentById(
                        paymentId
                );

        if (payment == null) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(payment);
    }

    @GetMapping("/payments/search")
    public ResponseEntity<List<AdminPaymentDTO>> searchPayments(
            @RequestParam(required = false) Integer theatre,
            @RequestParam(required = false) String paymentStatus
    ) {

        if (theatre == null &&
                (paymentStatus == null ||
                        paymentStatus.isBlank())) {

            return ResponseEntity.ok(
                    adminPaymentService.getAllPayments()
            );
        }


        if (theatre != null &&
                paymentStatus != null &&
                !paymentStatus.isBlank()) {

            return ResponseEntity.ok(
                    adminPaymentService
                            .getPaymentsByTheatreAndStatus(
                                    theatre,
                                    paymentStatus
                            )
            );
        }


        if (theatre != null) {

            return ResponseEntity.ok(
                    adminPaymentService
                            .getPaymentsByTheatre(
                                    theatre
                            )
            );
        }


        return ResponseEntity.ok(
                adminPaymentService
                        .getPaymentsByStatus(
                                paymentStatus
                        )
        );
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<AdminBookingListDTO>> getAllBookings() {

        return ResponseEntity.ok(
                adminBookingService.getAllBookings()
        );
    }

    @GetMapping("/bookings/search")
    public ResponseEntity<List<AdminBookingListDTO>> searchBookings(

            @RequestParam(required = false)
            Integer movie,

            @RequestParam(required = false)
            Integer theatre
    ) {

        // No filters
        if (movie == null &&
                theatre == null) {

            return ResponseEntity.ok(
                    adminBookingService.getAllBookings()
            );
        }


        // Both filters
        if (movie != null &&
                theatre != null) {

            return ResponseEntity.ok(
                    adminBookingService
                            .getBookingsByMovieAndTheatre(
                                    movie,
                                    theatre
                            )
            );
        }


        // Movie filter
        if (movie != null) {

            return ResponseEntity.ok(
                    adminBookingService
                            .getBookingsByMovie(
                                    movie
                            )
            );
        }


        // Theatre filter
        return ResponseEntity.ok(
                adminBookingService
                        .getBookingsByTheatre(
                                theatre
                        )
        );
    }




}