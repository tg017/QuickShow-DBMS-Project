package quickshow.dbms.project.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import quickshow.dbms.project.model.Booking;
import quickshow.dbms.project.model.BookingStatus;
import quickshow.dbms.project.model.Customer;
import quickshow.dbms.project.model.Payment;
import quickshow.dbms.project.model.PaymentMethod;
import quickshow.dbms.project.model.PaymentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CustomerRepository customerRepository;


    // =========================================================
    // CREATE
    // =========================================================

    @Test
    void shouldCreatePayment() {

        Booking booking =
                createBooking();

        Payment payment =
                createPayment(booking);

        Payment createdPayment =
                paymentRepository.create(
                        payment
                );

        assertNotNull(createdPayment);

        assertNotNull(
                createdPayment.getPaymentId()
        );

        assertEquals(
                PaymentMethod.UPI,
                createdPayment.getPaymentMethod()
        );

        assertEquals(
                payment.getTransactionId(),
                createdPayment.getTransactionId()
        );

        assertEquals(
                500L,
                createdPayment.getPaymentAmount()
        );

        assertEquals(
                PaymentStatus.SUCCESS,
                createdPayment.getPaymentStatus()
        );

        assertEquals(
                booking.getBookingId(),
                createdPayment.getBooking()
                        .getBookingId()
        );
    }


    // =========================================================
    // CREATE + READ
    // =========================================================

    @Test
    void shouldCreateAndRetrievePaymentById() {

        Booking booking =
                createBooking();

        Payment payment =
                createPayment(booking);

        Payment createdPayment =
                paymentRepository.create(
                        payment
                );

        Payment retrievedPayment =
                paymentRepository.findById(
                        createdPayment.getPaymentId()
                );

        assertNotNull(retrievedPayment);

        assertEquals(
                createdPayment.getPaymentId(),
                retrievedPayment.getPaymentId()
        );

        assertEquals(
                PaymentMethod.UPI,
                retrievedPayment.getPaymentMethod()
        );

        assertEquals(
                payment.getTransactionId(),
                retrievedPayment.getTransactionId()
        );

        assertEquals(
                500L,
                retrievedPayment.getPaymentAmount()
        );

        assertEquals(
                LocalDateTime.of(
                        2026,
                        8,
                        21,
                        18,
                        45
                ),
                retrievedPayment
                        .getPaymentDateTime()
        );

        assertEquals(
                PaymentStatus.SUCCESS,
                retrievedPayment.getPaymentStatus()
        );

        assertEquals(
                booking.getBookingId(),
                retrievedPayment.getBooking()
                        .getBookingId()
        );
    }


    // =========================================================
    // FIND NON-EXISTENT PAYMENT
    // =========================================================

    @Test
    void shouldReturnNullForNonExistentPayment() {

        Payment payment =
                paymentRepository.findById(
                        Integer.MAX_VALUE
                );

        assertNull(payment);
    }


    // =========================================================
    // FIND ALL
    // =========================================================

    @Test
    void shouldFindAllPayments() {

        Booking booking =
                createBooking();

        Payment payment1 =
                createPayment(booking);

        Payment payment2 =
                createPayment(booking);

        Payment savedPayment1 =
                paymentRepository.create(
                        payment1
                );

        Payment savedPayment2 =
                paymentRepository.create(
                        payment2
                );

        List<Payment> payments =
                paymentRepository.findAll();

        assertNotNull(payments);

        assertTrue(
                payments.stream()
                        .anyMatch(payment ->
                                payment.getPaymentId()
                                        .equals(
                                                savedPayment1
                                                        .getPaymentId()
                                        )
                        )
        );

        assertTrue(
                payments.stream()
                        .anyMatch(payment ->
                                payment.getPaymentId()
                                        .equals(
                                                savedPayment2
                                                        .getPaymentId()
                                        )
                        )
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Test
    void shouldUpdatePayment() {

        Booking booking =
                createBooking();

        Payment payment =
                paymentRepository.create(
                        createPayment(booking)
                );

        payment.setPaymentMethod(
                PaymentMethod.CREDIT_CARD
        );

        payment.setPaymentAmount(
                750L
        );

        payment.setPaymentStatus(
                PaymentStatus.PROCESSING
        );

        int rowsUpdated =
                paymentRepository.update(
                        payment
                );

        assertEquals(
                1,
                rowsUpdated
        );

        Payment updatedPayment =
                paymentRepository.findById(
                        payment.getPaymentId()
                );

        assertNotNull(updatedPayment);

        assertEquals(
                PaymentMethod.CREDIT_CARD,
                updatedPayment.getPaymentMethod()
        );

        assertEquals(
                750L,
                updatedPayment.getPaymentAmount()
        );

        assertEquals(
                PaymentStatus.PROCESSING,
                updatedPayment.getPaymentStatus()
        );
    }


    // =========================================================
    // UPDATE NON-EXISTENT PAYMENT
    // =========================================================

    @Test
    void shouldUpdateZeroRowsForNonExistentPayment() {

        Booking booking =
                createBooking();

        Payment payment =
                createPayment(booking);

        payment.setPaymentId(
                Integer.MAX_VALUE
        );

        int rowsUpdated =
                paymentRepository.update(
                        payment
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
    void shouldDeletePayment() {

        Booking booking =
                createBooking();

        Payment payment =
                paymentRepository.create(
                        createPayment(booking)
                );

        Integer paymentId =
                payment.getPaymentId();

        assertTrue(
                paymentRepository.existsById(
                        paymentId
                )
        );

        int rowsDeleted =
                paymentRepository.deleteById(
                        paymentId
                );

        assertEquals(
                1,
                rowsDeleted
        );

        assertFalse(
                paymentRepository.existsById(
                        paymentId
                )
        );

        assertNull(
                paymentRepository.findById(
                        paymentId
                )
        );
    }


    // =========================================================
    // DELETE NON-EXISTENT PAYMENT
    // =========================================================

    @Test
    void shouldDeleteZeroRowsForNonExistentPayment() {

        int rowsDeleted =
                paymentRepository.deleteById(
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
    void shouldCheckPaymentExistence() {

        Booking booking =
                createBooking();

        Payment payment =
                paymentRepository.create(
                        createPayment(booking)
                );

        Integer paymentId =
                payment.getPaymentId();

        assertTrue(
                paymentRepository.existsById(
                        paymentId
                )
        );

        assertFalse(
                paymentRepository.existsById(
                        Integer.MAX_VALUE
                )
        );
    }


    // =========================================================
    // PAYMENT METHOD MAPPING
    // =========================================================

    @Test
    void shouldPersistPaymentMethodCorrectly() {

        Booking booking =
                createBooking();

        for (PaymentMethod method :
                PaymentMethod.values()) {

            Payment payment =
                    createPayment(booking);

            payment.setPaymentMethod(
                    method
            );

            Payment createdPayment =
                    paymentRepository.create(
                            payment
                    );

            Payment retrievedPayment =
                    paymentRepository.findById(
                            createdPayment
                                    .getPaymentId()
                    );

            assertNotNull(
                    retrievedPayment
            );

            assertEquals(
                    method,
                    retrievedPayment
                            .getPaymentMethod()
            );
        }
    }


    // =========================================================
    // PAYMENT STATUS MAPPING
    // =========================================================

    @Test
    void shouldPersistPaymentStatusCorrectly() {

        Booking booking =
                createBooking();

        for (PaymentStatus status :
                PaymentStatus.values()) {

            Payment payment =
                    createPayment(booking);

            payment.setPaymentStatus(
                    status
            );

            Payment createdPayment =
                    paymentRepository.create(
                            payment
                    );

            Payment retrievedPayment =
                    paymentRepository.findById(
                            createdPayment
                                    .getPaymentId()
                    );

            assertNotNull(
                    retrievedPayment
            );

            assertEquals(
                    status,
                    retrievedPayment
                            .getPaymentStatus()
            );
        }
    }


    // =========================================================
    // FIND BY BOOKING
    // =========================================================

    @Test
    void shouldFindPaymentsByBookingId() {

        Booking booking =
                createBooking();

        Payment payment1 =
                paymentRepository.create(
                        createPayment(booking)
                );

        Payment payment2 =
                paymentRepository.create(
                        createPayment(booking)
                );

        List<Payment> payments =
                paymentRepository.findByBookingId(
                        booking.getBookingId()
                );

        assertTrue(
                payments.stream()
                        .anyMatch(payment ->
                                payment.getPaymentId()
                                        .equals(
                                                payment1
                                                        .getPaymentId()
                                        )
                        )
        );

        assertTrue(
                payments.stream()
                        .anyMatch(payment ->
                                payment.getPaymentId()
                                        .equals(
                                                payment2
                                                        .getPaymentId()
                                        )
                        )
        );

        assertTrue(
                payments.stream()
                        .allMatch(payment ->
                                payment.getBooking()
                                        .getBookingId()
                                        .equals(
                                                booking
                                                        .getBookingId()
                                        )
                        )
        );
    }


    // =========================================================
    // FIND BY TRANSACTION ID
    // =========================================================

    @Test
    void shouldFindPaymentByTransactionId() {

        Booking booking =
                createBooking();

        Payment payment =
                createPayment(booking);

        Payment savedPayment =
                paymentRepository.create(
                        payment
                );

        Payment result =
                paymentRepository.findByTransactionId(
                        payment.getTransactionId()
                );

        assertNotNull(result);

        assertEquals(
                savedPayment.getPaymentId(),
                result.getPaymentId()
        );

        assertEquals(
                payment.getTransactionId(),
                result.getTransactionId()
        );
    }


    // =========================================================
    // FIND BY STATUS
    // =========================================================

    @Test
    void shouldFindPaymentsByStatus() {

        Booking booking =
                createBooking();

        Payment successPayment =
                createPayment(booking);

        successPayment.setPaymentStatus(
                PaymentStatus.SUCCESS
        );

        Payment failedPayment =
                createPayment(booking);

        failedPayment.setPaymentStatus(
                PaymentStatus.FAILED
        );

        Payment savedSuccess =
                paymentRepository.create(
                        successPayment
                );

        Payment savedFailed =
                paymentRepository.create(
                        failedPayment
                );

        List<Payment> results =
                paymentRepository.findByStatus(
                        PaymentStatus.SUCCESS
                );

        assertTrue(
                results.stream()
                        .anyMatch(payment ->
                                payment.getPaymentId()
                                        .equals(
                                                savedSuccess
                                                        .getPaymentId()
                                        )
                        )
        );

        assertFalse(
                results.stream()
                        .anyMatch(payment ->
                                payment.getPaymentId()
                                        .equals(
                                                savedFailed
                                                        .getPaymentId()
                                        )
                        )
        );

        assertTrue(
                results.stream()
                        .allMatch(payment ->
                                payment.getPaymentStatus()
                                        == PaymentStatus.SUCCESS
                        )
        );
    }


    // =========================================================
    // FIND BY PAYMENT METHOD
    // =========================================================

    @Test
    void shouldFindPaymentsByPaymentMethod() {

        Booking booking =
                createBooking();

        Payment upiPayment =
                createPayment(booking);

        upiPayment.setPaymentMethod(
                PaymentMethod.UPI
        );

        Payment cardPayment =
                createPayment(booking);

        cardPayment.setPaymentMethod(
                PaymentMethod.CREDIT_CARD
        );

        Payment savedUpi =
                paymentRepository.create(
                        upiPayment
                );

        Payment savedCard =
                paymentRepository.create(
                        cardPayment
                );

        List<Payment> results =
                paymentRepository.findByPaymentMethod(
                        PaymentMethod.UPI
                );

        assertTrue(
                results.stream()
                        .anyMatch(payment ->
                                payment.getPaymentId()
                                        .equals(
                                                savedUpi
                                                        .getPaymentId()
                                        )
                        )
        );

        assertFalse(
                results.stream()
                        .anyMatch(payment ->
                                payment.getPaymentId()
                                        .equals(
                                                savedCard
                                                        .getPaymentId()
                                        )
                        )
        );

        assertTrue(
                results.stream()
                        .allMatch(payment ->
                                payment.getPaymentMethod()
                                        == PaymentMethod.UPI
                        )
        );
    }


    // =========================================================
    // DUPLICATE TRANSACTION ID
    // =========================================================

    @Test
    void shouldRejectDuplicateTransactionId() {

        Booking booking =
                createBooking();

        String transactionId =
                uniqueTransactionId();

        Payment payment1 =
                createPayment(booking);

        payment1.setTransactionId(
                transactionId
        );

        Payment payment2 =
                createPayment(booking);

        payment2.setTransactionId(
                transactionId
        );

        paymentRepository.create(
                payment1
        );

        assertThrows(
                Exception.class,
                () ->
                        paymentRepository.create(
                                payment2
                        )
        );
    }


    // =========================================================
    // NOT NULL CONSTRAINTS
    // =========================================================

    @Test
    void shouldRejectPaymentWithoutPaymentMethod() {

        Booking booking =
                createBooking();

        Payment payment =
                createPayment(booking);

        payment.setPaymentMethod(null);

        assertThrows(
                Exception.class,
                () ->
                        paymentRepository.create(
                                payment
                        )
        );
    }


    @Test
    void shouldRejectPaymentWithoutTransactionId() {

        Booking booking =
                createBooking();

        Payment payment =
                createPayment(booking);

        payment.setTransactionId(null);

        assertThrows(
                Exception.class,
                () ->
                        paymentRepository.create(
                                payment
                        )
        );
    }


    @Test
    void shouldRejectPaymentWithoutPaymentStatus() {

        Booking booking =
                createBooking();

        Payment payment =
                createPayment(booking);

        payment.setPaymentStatus(null);

        assertThrows(
                Exception.class,
                () ->
                        paymentRepository.create(
                                payment
                        )
        );
    }


    @Test
    void shouldRejectPaymentWithoutBooking() {

        Payment payment =
                new Payment();

        payment.setPaymentMethod(
                PaymentMethod.UPI
        );

        payment.setTransactionId(
                uniqueTransactionId()
        );

        payment.setPaymentAmount(
                500L
        );

        payment.setPaymentDateTime(
                LocalDateTime.of(
                        2026,
                        8,
                        21,
                        18,
                        45
                )
        );

        payment.setPaymentStatus(
                PaymentStatus.PENDING
        );

        assertThrows(
                Exception.class,
                () ->
                        paymentRepository.create(
                                payment
                        )
        );
    }


    // =========================================================
    // HELPERS
    // =========================================================

    private Payment createPayment(
            Booking booking
    ) {

        Payment payment =
                new Payment();

        payment.setPaymentMethod(
                PaymentMethod.UPI
        );

        payment.setTransactionId(
                uniqueTransactionId()
        );

        payment.setPaymentAmount(
                500L
        );

        payment.setPaymentDateTime(
                LocalDateTime.of(
                        2026,
                        8,
                        21,
                        18,
                        45
                )
        );

        payment.setPaymentStatus(
                PaymentStatus.SUCCESS
        );

        payment.setBooking(
                booking
        );

        return payment;
    }


    private Booking createBooking() {

        Customer customer =
                customerRepository.create(
                        createCustomer()
                );

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
                BookingStatus.CONFIRMED
        );

        booking.setTotalAmount(
                500L
        );

        booking.setTotalSeatsCount(
                2
        );

        return bookingRepository.create(
                booking
        );
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


    private String uniqueTransactionId() {

        return "TXN_" +
                UUID.randomUUID()
                        .toString()
                        .replace(
                                "-",
                                ""
                        );
    }
}