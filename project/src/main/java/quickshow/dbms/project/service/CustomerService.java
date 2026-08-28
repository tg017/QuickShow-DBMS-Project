package quickshow.dbms.project.service;

import org.springframework.stereotype.Service;
import quickshow.dbms.project.dto.CustomerProfileDTO;
import quickshow.dbms.project.exception.ResourceNotFoundException;
import quickshow.dbms.project.repository.CustomerRepository;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(
            CustomerRepository customerRepository
    ) {
        this.customerRepository =
                customerRepository;
    }

    // =========================================================
    // GET CUSTOMER PROFILE
    // =========================================================

    public CustomerProfileDTO getProfile(
            Integer userId
    ) {

        CustomerProfileDTO profile =
                customerRepository.findProfileById(
                        userId
                );

        if (profile == null) {

            throw new ResourceNotFoundException(
                    "Customer not found"
            );
        }

        return profile;
    }
}