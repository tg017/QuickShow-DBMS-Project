package quickshow.dbms.project.service;

import org.springframework.stereotype.Service;

import quickshow.dbms.project.dto.AdminPaymentDTO;
import quickshow.dbms.project.model.PaymentStatus;
import quickshow.dbms.project.repository.AdminPaymentRepository;

import java.util.List;

@Service
public class AdminPaymentService {

    private final AdminPaymentRepository adminPaymentRepository;


    public AdminPaymentService(
            AdminPaymentRepository adminPaymentRepository
    ) {

        this.adminPaymentRepository =
                adminPaymentRepository;
    }


    // =========================================================
    // GET ALL PAYMENTS
    // =========================================================

    public List<AdminPaymentDTO> getAllPayments() {

        return adminPaymentRepository.findAll();
    }


    // =========================================================
    // GET PAYMENT BY ID
    // =========================================================

    public AdminPaymentDTO getPaymentById(
            Integer paymentId
    ) {

        return adminPaymentRepository.findById(
                paymentId
        );
    }


    // =========================================================
    // FILTER BY THEATRE
    // =========================================================

    public List<AdminPaymentDTO> getPaymentsByTheatre(
            Integer theatreId
    ) {

        return adminPaymentRepository.findByTheatre(
                theatreId
        );
    }


    // =========================================================
    // FILTER BY PAYMENT STATUS
    // =========================================================

    public List<AdminPaymentDTO> getPaymentsByStatus(
            String paymentStatus
    ) {

        String status =
                parsePaymentStatus(
                        paymentStatus
                );

        return adminPaymentRepository.findByStatus(
                status
        );
    }


    // =========================================================
    // FILTER BY THEATRE + PAYMENT STATUS
    // =========================================================

    public List<AdminPaymentDTO> getPaymentsByTheatreAndStatus(
            Integer theatreId,
            String paymentStatus
    ) {

        String status =
                parsePaymentStatus(
                        paymentStatus
                );

        return adminPaymentRepository
                .findByTheatreAndStatus(
                        theatreId,
                        status
                );
    }


    // =========================================================
    // VALIDATE PAYMENT STATUS
    // =========================================================

    private String parsePaymentStatus(
            String value
    ) {

        if (value == null ||
                value.isBlank()) {

            throw new IllegalArgumentException(
                    "Payment status cannot be empty"
            );
        }

        try {

            return PaymentStatus
                    .valueOf(
                            value.toUpperCase()
                    )
                    .name();

        } catch (IllegalArgumentException e) {

            throw new IllegalArgumentException(
                    "Invalid payment status: " + value
            );
        }
    }
}