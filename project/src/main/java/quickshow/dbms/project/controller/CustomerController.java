package quickshow.dbms.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import quickshow.dbms.project.dto.CustomerProfileDTO;
import quickshow.dbms.project.security.AuthenticatedCustomer;
import quickshow.dbms.project.service.CustomerService;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(
            CustomerService customerService
    ) {
        this.customerService =
                customerService;
    }

    // =========================================================
    // GET CUSTOMER PROFILE
    // =========================================================

    @GetMapping("/profile")
    public ResponseEntity<CustomerProfileDTO> getProfile(
            Authentication authentication
    ) {

        AuthenticatedCustomer customer =
                (AuthenticatedCustomer)
                        authentication.getPrincipal();

        CustomerProfileDTO profile =
                customerService.getProfile(
                        customer.getUserId()
                );

        return ResponseEntity.ok(profile);
    }
}