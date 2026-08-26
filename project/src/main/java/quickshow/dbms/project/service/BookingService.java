package quickshow.dbms.project.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quickshow.dbms.project.dto.BookingCheckoutRequestDTO;
import quickshow.dbms.project.dto.BookingDetailsDTO;
import quickshow.dbms.project.dto.MovieBookingSummaryDTO;
import quickshow.dbms.project.dto.PaymentSummaryDTO;
import quickshow.dbms.project.dto.ScreenSummaryDTO;
import quickshow.dbms.project.dto.SelectedSeatDTO;
import quickshow.dbms.project.dto.ShowSummaryDTO;
import quickshow.dbms.project.dto.TheatreSummaryDTO;
import quickshow.dbms.project.exception.BookingConflictException;
import quickshow.dbms.project.exception.ResourceNotFoundException;
import quickshow.dbms.project.model.BookingStatus;
import quickshow.dbms.project.model.PaymentMethod;
import quickshow.dbms.project.model.PaymentStatus;
import quickshow.dbms.project.payment.PaymentGateway;
import quickshow.dbms.project.payment.PaymentResult;
import quickshow.dbms.project.repository.BookedSeatsRepository;
import quickshow.dbms.project.repository.BookingRepository;
import quickshow.dbms.project.repository.PaymentRepository;
import quickshow.dbms.project.repository.ShowSeatAllocationRepository;
import quickshow.dbms.project.repository.data.BookingDetailsData;
import quickshow.dbms.project.repository.data.CheckoutShowData;
import quickshow.dbms.project.repository.data.PaymentData;
import quickshow.dbms.project.repository.data.SelectedSeatData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookedSeatsRepository bookedSeatsRepository;
    private final PaymentRepository paymentRepository;
    private final ShowSeatAllocationRepository
            showSeatAllocationRepository;
    private final PaymentGateway paymentGateway;


    public BookingService(
            BookingRepository bookingRepository,
            BookedSeatsRepository bookedSeatsRepository,
            PaymentRepository paymentRepository,
            ShowSeatAllocationRepository showSeatAllocationRepository,
            PaymentGateway paymentGateway
    ) {

        this.bookingRepository =
                bookingRepository;

        this.bookedSeatsRepository =
                bookedSeatsRepository;

        this.paymentRepository =
                paymentRepository;

        this.showSeatAllocationRepository =
                showSeatAllocationRepository;

        this.paymentGateway =
                paymentGateway;
    }


    // =========================================================
    // CHECKOUT
    // =========================================================

    @Transactional
    public BookingDetailsDTO checkout(
            BookingCheckoutRequestDTO request
    ) {

        validateRequest(request);


        // =====================================================
        // 1. VALIDATE CUSTOMER
        // =====================================================

        if (!bookingRepository.customerExists(
                request.getCustomerId()
        )) {

            throw new ResourceNotFoundException(
                    "Customer not found"
            );
        }


        // =====================================================
        // 2. VALIDATE SHOW
        // =====================================================

        CheckoutShowData show =
                bookingRepository.findShowForCheckout(
                        request.getShowId()
                );

        if (show == null) {

            throw new ResourceNotFoundException(
                    "Show not found"
            );
        }


        if (!"SCHEDULED".equals(
                show.getShowStatus()
        )) {

            throw new IllegalStateException(
                    "This show is not available for booking"
            );
        }


        // =====================================================
        // 3. REMOVE DUPLICATE SEAT IDs
        // =====================================================

        Set<Integer> uniqueSeatIds =
                new HashSet<>(
                        request.getSeatIds()
                );

        if (uniqueSeatIds.size()
                != request.getSeatIds().size()) {

            throw new IllegalArgumentException(
                    "Duplicate seat IDs are not allowed"
            );
        }


        // =====================================================
        // 4. VALIDATE AVAILABLE SEAT COUNT
        // =====================================================

        int requestedSeatCount =
                request.getSeatIds().size();

        if (requestedSeatCount
                > show.getAvailableSeats()) {

            throw new BookingConflictException(
                    "Not enough seats available"
            );
        }


        // =====================================================
        // 5. ATOMICALLY CLAIM SEATS
        // =====================================================

        int claimedSeats =
                showSeatAllocationRepository.claimSeats(
                        request.getShowId(),
                        show.getScreenId(),
                        request.getSeatIds()
                );

        if (claimedSeats
                != requestedSeatCount) {

            throw new BookingConflictException(
                    "One or more selected seats are no longer available"
            );
        }


        // =====================================================
        // 6. CALCULATE TOTAL
        // =====================================================

        long totalAmount =
                show.getTicketPrice()
                        * requestedSeatCount;


        // =====================================================
        // 7. CREATE BOOKING
        // =====================================================

        Integer bookingId =
                bookingRepository.createBooking(
                        request.getCustomerId(),
                        totalAmount,
                        requestedSeatCount
                );


        // =====================================================
        // 8. CREATE BOOKED SEATS
        // =====================================================

        bookedSeatsRepository.createBookedSeats(
                bookingId,
                request.getShowId(),
                show.getScreenId(),
                request.getSeatIds()
        );


        // =====================================================
        // 9. PAYMENT
        // =====================================================

        PaymentMethod paymentMethod =
                parsePaymentMethod(
                        request.getPaymentMethod()
                );

        PaymentResult paymentResult =
                paymentGateway.processPayment(
                        totalAmount,
                        paymentMethod
                );


        // =====================================================
        // 10. HANDLE PAYMENT FAILURE
        // =====================================================

        if (paymentResult.getPaymentStatus()
                != PaymentStatus.SUCCESS) {

            paymentRepository.createPayment(
                    bookingId,
                    paymentMethod,
                    paymentResult.getTransactionId(),
                    totalAmount,
                    paymentResult.getPaymentStatus()
            );

            bookingRepository.failBooking(
                    bookingId
            );

            showSeatAllocationRepository.releaseSeats(
                    request.getShowId(),
                    show.getScreenId(),
                    request.getSeatIds()
            );

            throw new BookingConflictException(
                    "Payment failed"
            );
        }


        // =====================================================
        // 11. CREATE SUCCESSFUL PAYMENT
        // =====================================================

        paymentRepository.createPayment(
                bookingId,
                paymentMethod,
                paymentResult.getTransactionId(),
                totalAmount,
                PaymentStatus.SUCCESS
        );


        // =====================================================
        // 12. UPDATE SHOW AVAILABLE SEATS
        // =====================================================

        int updated =
                bookingRepository.decreaseAvailableSeats(
                        request.getShowId(),
                        requestedSeatCount
                );

        if (updated != 1) {

            throw new IllegalStateException(
                    "Unable to update available seats"
            );
        }


        // =====================================================
        // 13. CONFIRM BOOKING
        // =====================================================

        bookingRepository.confirmBooking(
                bookingId
        );


        // =====================================================
        // 14. RETURN BOOKING DETAILS
        // =====================================================

        return getBookingDetails(
                bookingId
        );
    }


    // =========================================================
    // GET BOOKING DETAILS
    // =========================================================

    public BookingDetailsDTO getBookingDetails(
            Integer bookingId
    ) {

        if (bookingId == null) {

            throw new IllegalArgumentException(
                    "Booking ID cannot be null"
            );
        }

        BookingDetailsData booking =
                bookingRepository.findBookingDetails(
                        bookingId
                );

        if (booking == null) {

            throw new ResourceNotFoundException(
                    "Booking not found"
            );
        }

        List<SelectedSeatData> seatData =
                bookedSeatsRepository.findSeatsByBookingId(
                        bookingId
                );

        List<SelectedSeatDTO> seats =
                new ArrayList<>();

        for (SelectedSeatData seat :
                seatData) {

            seats.add(
                    new SelectedSeatDTO(
                            seat.getSeatId(),
                            seat.getRowNo(),
                            seat.getSeatNo()
                    )
            );
        }

        PaymentData payment =
                paymentRepository.findPaymentByBookingId(
                        bookingId
                );

        PaymentSummaryDTO paymentDTO = null;

        if (payment != null) {

            paymentDTO =
                    new PaymentSummaryDTO(
                            payment.getPaymentId(),
                            payment.getPaymentMethod(),
                            payment.getPaymentAmount(),
                            payment.getPaymentStatus()
                    );
        }


        ShowSummaryDTO show =
                new ShowSummaryDTO(
                        booking.getShowId(),
                        booking.getShowDate(),
                        booking.getShowTime()
                );

        MovieBookingSummaryDTO movie =
                new MovieBookingSummaryDTO(
                        booking.getMovieId(),
                        booking.getMovieTitle()
                );

        TheatreSummaryDTO theatre =
                new TheatreSummaryDTO(
                        booking.getTheatreId(),
                        booking.getTheatreName(),
                        booking.getCity()
                );

        ScreenSummaryDTO screen =
                new ScreenSummaryDTO(
                        booking.getScreenId(),
                        booking.getScreenName(),
                        booking.getScreenType()
                );


        return new BookingDetailsDTO(
                booking.getBookingId(),
                show,
                movie,
                theatre,
                screen,
                seats,
                booking.getTotalSeatCount(),
                booking.getTotalAmount(),
                booking.getBookingStatus(),
                paymentDTO
        );
    }


    // =========================================================
    // VALIDATE REQUEST
    // =========================================================

    private void validateRequest(
            BookingCheckoutRequestDTO request
    ) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Request cannot be null"
            );
        }

        if (request.getCustomerId() == null) {
            throw new IllegalArgumentException(
                    "Customer ID cannot be null"
            );
        }

        if (request.getShowId() == null) {
            throw new IllegalArgumentException(
                    "Show ID cannot be null"
            );
        }

        if (request.getSeatIds() == null
                || request.getSeatIds().isEmpty()) {

            throw new IllegalArgumentException(
                    "At least one seat must be selected"
            );
        }

        if (request.getPaymentMethod() == null
                || request.getPaymentMethod().isBlank()) {

            throw new IllegalArgumentException(
                    "Payment method cannot be empty"
            );
        }

        for (Integer seatId :
                request.getSeatIds()) {

            if (seatId == null || seatId <= 0) {

                throw new IllegalArgumentException(
                        "Invalid seat ID"
                );
            }
        }
    }


    // =========================================================
    // PAYMENT METHOD
    // =========================================================

    private PaymentMethod parsePaymentMethod(
            String value
    ) {

        try {

            return PaymentMethod.valueOf(
                    value.toUpperCase()
            );

        } catch (IllegalArgumentException e) {

            throw new IllegalArgumentException(
                    "Invalid payment method: " + value
            );
        }
    }
}