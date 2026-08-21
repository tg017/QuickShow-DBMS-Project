package quickshow.dbms.project.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import quickshow.dbms.project.model.Booking;
import quickshow.dbms.project.model.BookingStatus;
import quickshow.dbms.project.model.Customer;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CustomerRepository customerRepository;


    // =========================================================
    // CREATE
    // =========================================================

    @Test
    void shouldCreateBooking() {

        Customer customer =
                customerRepository.create(
                        createCustomer()
                );

        Booking booking =
                createBooking(customer);

        Booking createdBooking =
                bookingRepository.create(
                        booking
                );

        assertNotNull(createdBooking);

        assertNotNull(
                createdBooking.getBookingId()
        );

        assertEquals(
                customer.getUserId(),
                createdBooking.getCustomer()
                        .getUserId()
        );

        assertEquals(
                BookingStatus.PENDING,
                createdBooking.getBookingStatus()
        );

        assertEquals(
                500L,
                createdBooking.getTotalAmount()
        );

        assertEquals(
                2,
                createdBooking.getTotalSeatsCount()
        );
    }


    // =========================================================
    // CREATE + READ
    // =========================================================

    @Test
    void shouldCreateAndRetrieveBookingById() {

        Customer customer =
                customerRepository.create(
                        createCustomer()
                );

        Booking booking =
                createBooking(customer);

        Booking createdBooking =
                bookingRepository.create(
                        booking
                );

        Booking retrievedBooking =
                bookingRepository.findById(
                        createdBooking.getBookingId()
                );

        assertNotNull(retrievedBooking);

        assertEquals(
                createdBooking.getBookingId(),
                retrievedBooking.getBookingId()
        );

        assertEquals(
                customer.getUserId(),
                retrievedBooking.getCustomer()
                        .getUserId()
        );

        assertEquals(
                LocalDateTime.of(
                        2026,
                        8,
                        21,
                        18,
                        30
                ),
                retrievedBooking
                        .getBookingDateTime()
        );

        assertEquals(
                BookingStatus.PENDING,
                retrievedBooking.getBookingStatus()
        );

        assertEquals(
                500L,
                retrievedBooking.getTotalAmount()
        );

        assertEquals(
                2,
                retrievedBooking.getTotalSeatsCount()
        );
    }


    // =========================================================
    // FIND NON-EXISTENT BOOKING
    // =========================================================

    @Test
    void shouldReturnNullForNonExistentBooking() {

        Booking booking =
                bookingRepository.findById(
                        Integer.MAX_VALUE
                );

        assertNull(booking);
    }


    // =========================================================
    // FIND ALL
    // =========================================================

    @Test
    void shouldFindAllBookings() {

        Customer customer =
                customerRepository.create(
                        createCustomer()
                );

        Booking booking1 =
                createBooking(customer);

        booking1.setTotalAmount(500L);

        Booking booking2 =
                createBooking(customer);

        booking2.setTotalAmount(800L);

        Booking savedBooking1 =
                bookingRepository.create(
                        booking1
                );

        Booking savedBooking2 =
                bookingRepository.create(
                        booking2
                );

        List<Booking> bookings =
                bookingRepository.findAll();

        assertNotNull(bookings);

        assertTrue(
                bookings.stream()
                        .anyMatch(booking ->
                                booking.getBookingId()
                                        .equals(
                                                savedBooking1
                                                        .getBookingId()
                                        )
                        )
        );

        assertTrue(
                bookings.stream()
                        .anyMatch(booking ->
                                booking.getBookingId()
                                        .equals(
                                                savedBooking2
                                                        .getBookingId()
                                        )
                        )
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Test
    void shouldUpdateBooking() {

        Customer customer =
                customerRepository.create(
                        createCustomer()
                );

        Booking booking =
                bookingRepository.create(
                        createBooking(customer)
                );

        booking.setBookingStatus(
                BookingStatus.CONFIRMED
        );

        booking.setTotalAmount(
                750L
        );

        booking.setTotalSeatsCount(
                3
        );

        int rowsUpdated =
                bookingRepository.update(
                        booking
                );

        assertEquals(
                1,
                rowsUpdated
        );

        Booking updatedBooking =
                bookingRepository.findById(
                        booking.getBookingId()
                );

        assertNotNull(updatedBooking);

        assertEquals(
                BookingStatus.CONFIRMED,
                updatedBooking.getBookingStatus()
        );

        assertEquals(
                750L,
                updatedBooking.getTotalAmount()
        );

        assertEquals(
                3,
                updatedBooking.getTotalSeatsCount()
        );
    }


    // =========================================================
    // UPDATE NON-EXISTENT BOOKING
    // =========================================================

    @Test
    void shouldUpdateZeroRowsForNonExistentBooking() {

        Customer customer =
                customerRepository.create(
                        createCustomer()
                );

        Booking booking =
                createBooking(customer);

        booking.setBookingId(
                Integer.MAX_VALUE
        );

        int rowsUpdated =
                bookingRepository.update(
                        booking
                );

        assertEquals(
                0,
                rowsUpdated
        );
    }


    // =========================================================
    // DELETE
    // =========================================================

    @Test
    void shouldDeleteBooking() {

        Customer customer =
                customerRepository.create(
                        createCustomer()
                );

        Booking booking =
                bookingRepository.create(
                        createBooking(customer)
                );

        Integer bookingId =
                booking.getBookingId();

        assertTrue(
                bookingRepository.existsById(
                        bookingId
                )
        );

        int rowsDeleted =
                bookingRepository.deleteById(
                        bookingId
                );

        assertEquals(
                1,
                rowsDeleted
        );

        assertFalse(
                bookingRepository.existsById(
                        bookingId
                )
        );

        assertNull(
                bookingRepository.findById(
                        bookingId
                )
        );
    }


    // =========================================================
    // DELETE NON-EXISTENT BOOKING
    // =========================================================

    @Test
    void shouldDeleteZeroRowsForNonExistentBooking() {

        int rowsDeleted =
                bookingRepository.deleteById(
                        Integer.MAX_VALUE
                );

        assertEquals(
                0,
                rowsDeleted
        );
    }


    // =========================================================
    // EXISTS
    // =========================================================

    @Test
    void shouldCheckBookingExistence() {

        Customer customer =
                customerRepository.create(
                        createCustomer()
                );

        Booking booking =
                bookingRepository.create(
                        createBooking(customer)
                );

        Integer bookingId =
                booking.getBookingId();

        assertTrue(
                bookingRepository.existsById(
                        bookingId
                )
        );

        assertFalse(
                bookingRepository.existsById(
                        Integer.MAX_VALUE
                )
        );
    }


    // =========================================================
    // BOOKING STATUS MAPPING
    // =========================================================

    @Test
    void shouldPersistBookingStatusCorrectly() {

        Customer customer =
                customerRepository.create(
                        createCustomer()
                );

        for (BookingStatus status :
                BookingStatus.values()) {

            Booking booking =
                    createBooking(customer);

            booking.setBookingStatus(
                    status
            );

            Booking createdBooking =
                    bookingRepository.create(
                            booking
                    );

            Booking retrievedBooking =
                    bookingRepository.findById(
                            createdBooking
                                    .getBookingId()
                    );

            assertNotNull(
                    retrievedBooking
            );

            assertEquals(
                    status,
                    retrievedBooking
                            .getBookingStatus()
            );
        }
    }


    // =========================================================
    // FIND BY CUSTOMER
    // =========================================================

    @Test
    void shouldFindBookingsByCustomerId() {

        Customer customer =
                customerRepository.create(
                        createCustomer()
                );

        Booking booking1 =
                bookingRepository.create(
                        createBooking(customer)
                );

        Booking booking2 =
                bookingRepository.create(
                        createBooking(customer)
                );

        List<Booking> bookings =
                bookingRepository.findByCustomerId(
                        customer.getUserId()
                );

        assertTrue(
                bookings.stream()
                        .anyMatch(booking ->
                                booking.getBookingId()
                                        .equals(
                                                booking1
                                                        .getBookingId()
                                        )
                        )
        );

        assertTrue(
                bookings.stream()
                        .anyMatch(booking ->
                                booking.getBookingId()
                                        .equals(
                                                booking2
                                                        .getBookingId()
                                        )
                        )
        );

        assertTrue(
                bookings.stream()
                        .allMatch(booking ->
                                booking.getCustomer()
                                        .getUserId()
                                        .equals(
                                                customer
                                                        .getUserId()
                                        )
                        )
        );
    }


    // =========================================================
    // FIND BY STATUS
    // =========================================================

    @Test
    void shouldFindBookingsByStatus() {

        Customer customer =
                customerRepository.create(
                        createCustomer()
                );

        Booking pendingBooking =
                createBooking(customer);

        pendingBooking.setBookingStatus(
                BookingStatus.PENDING
        );

        Booking confirmedBooking =
                createBooking(customer);

        confirmedBooking.setBookingStatus(
                BookingStatus.CONFIRMED
        );

        Booking savedPending =
                bookingRepository.create(
                        pendingBooking
                );

        Booking savedConfirmed =
                bookingRepository.create(
                        confirmedBooking
                );

        List<Booking> results =
                bookingRepository.findByStatus(
                        BookingStatus.PENDING
                );

        assertTrue(
                results.stream()
                        .anyMatch(booking ->
                                booking.getBookingId()
                                        .equals(
                                                savedPending
                                                        .getBookingId()
                                        )
                        )
        );

        assertFalse(
                results.stream()
                        .anyMatch(booking ->
                                booking.getBookingId()
                                        .equals(
                                                savedConfirmed
                                                        .getBookingId()
                                        )
                        )
        );

        assertTrue(
                results.stream()
                        .allMatch(booking ->
                                booking.getBookingStatus()
                                        == BookingStatus.PENDING
                        )
        );
    }


    // =========================================================
    // FIND BY CUSTOMER + STATUS
    // =========================================================

    @Test
    void shouldFindBookingsByCustomerAndStatus() {

        Customer customer =
                customerRepository.create(
                        createCustomer()
                );

        Booking pendingBooking =
                createBooking(customer);

        pendingBooking.setBookingStatus(
                BookingStatus.PENDING
        );

        Booking confirmedBooking =
                createBooking(customer);

        confirmedBooking.setBookingStatus(
                BookingStatus.CONFIRMED
        );

        Booking savedPending =
                bookingRepository.create(
                        pendingBooking
                );

        Booking savedConfirmed =
                bookingRepository.create(
                        confirmedBooking
                );

        List<Booking> results =
                bookingRepository
                        .findByCustomerIdAndStatus(
                                customer.getUserId(),
                                BookingStatus.PENDING
                        );

        assertTrue(
                results.stream()
                        .anyMatch(booking ->
                                booking.getBookingId()
                                        .equals(
                                                savedPending
                                                        .getBookingId()
                                        )
                        )
        );

        assertFalse(
                results.stream()
                        .anyMatch(booking ->
                                booking.getBookingId()
                                        .equals(
                                                savedConfirmed
                                                        .getBookingId()
                                        )
                        )
        );
    }


    // =========================================================
    // NOT NULL CONSTRAINTS
    // =========================================================

    @Test
    void shouldRejectBookingWithoutCustomer() {

        Booking booking =
                new Booking();

        booking.setBookingDateTime(
                LocalDateTime.of(
                        2026,
                        8,
                        21,
                        18,
                        30
                )
        );

        booking.setBookingStatus(
                BookingStatus.PENDING
        );

        booking.setTotalAmount(
                500L
        );

        booking.setTotalSeatsCount(
                2
        );

        assertThrows(
                Exception.class,
                () ->
                        bookingRepository.create(
                                booking
                        )
        );
    }


    @Test
    void shouldRejectBookingWithoutStatus() {

        Customer customer =
                customerRepository.create(
                        createCustomer()
                );

        Booking booking =
                createBooking(customer);

        booking.setBookingStatus(null);

        assertThrows(
                Exception.class,
                () ->
                        bookingRepository.create(
                                booking
                        )
        );
    }


    // =========================================================
    // HELPERS
    // =========================================================

    private Booking createBooking(
            Customer customer
    ) {

        Booking booking =
                new Booking();

        booking.setCustomer(
                customer
        );

        booking.setBookingDateTime(
                LocalDateTime.of(
                        2026,
                        8,
                        21,
                        18,
                        30
                )
        );

        booking.setBookingStatus(
                BookingStatus.PENDING
        );

        booking.setTotalAmount(
                500L
        );

        booking.setTotalSeatsCount(
                2
        );

        return booking;
    }


    private Customer createCustomer() {

        Customer customer =
                new Customer();

        customer.setFirstName(
                "Test"
        );

        customer.setLastName(
                "User"
        );

        customer.setPassword(
                "password"
        );

        return customer;
    }
}