package quickshow.dbms.project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import quickshow.dbms.project.dto.BookingCheckoutRequestDTO;
import quickshow.dbms.project.dto.BookingDetailsDTO;
import quickshow.dbms.project.service.BookingService;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(
            BookingService bookingService
    ) {
        this.bookingService = bookingService;
    }


    // =========================================================
    // CHECKOUT
    // =========================================================

    @PostMapping("/checkout")
    public ResponseEntity<BookingDetailsDTO> checkout(
            @RequestBody BookingCheckoutRequestDTO request
    ) {

        BookingDetailsDTO booking =
                bookingService.checkout(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(booking);
    }


    // =========================================================
    // GET BOOKING DETAILS
    // =========================================================

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDetailsDTO> getBooking(
            @PathVariable Integer bookingId
    ) {

        return ResponseEntity.ok(
                bookingService.getBookingDetails(
                        bookingId
                )
        );
    }
}