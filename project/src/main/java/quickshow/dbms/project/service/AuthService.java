package quickshow.dbms.project.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import quickshow.dbms.project.dto.*;
import quickshow.dbms.project.repository.CustomerRepository;
import quickshow.dbms.project.repository.data.CustomerLoginData;
import quickshow.dbms.project.repository.data.CustomerProfileData;
import quickshow.dbms.project.security.JwtService;

@Service
public class AuthService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public RegisterResponseDTO register(
            RegisterRequestDTO request
    ) {

        if (request.getEmail() == null ||
                request.getEmail().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Email cannot be empty"
            );
        }

        if (customerRepository.emailExists(request.getEmail())) {

            throw new IllegalArgumentException(
                    "Email already registered"
            );
        }

        // Hash the password before storing it
        String hashedPassword =
                passwordEncoder.encode(request.getPassword());

        // Store hashed password instead of plain password
        request.setPassword(hashedPassword);

        Integer userId =
                customerRepository.createCustomer(request);

        customerRepository.createCustomerEmail(
                userId,
                request.getEmail()
        );

        return new RegisterResponseDTO(
                userId,
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                "Registration successful"
        );
    }

    public LoginResponseDTO login(
            LoginRequestDTO request
    ) {

        CustomerLoginData customer =
                customerRepository.findLoginDataByEmail(
                        request.getEmail()
                );

        if (customer == null) {

            throw new IllegalArgumentException(
                    "Invalid email or password"
            );
        }

        boolean passwordMatches =
                passwordEncoder.matches(
                        request.getPassword(),
                        customer.getPassword()
                );

        if (!passwordMatches) {

            throw new IllegalArgumentException(
                    "Invalid email or password"
            );
        }

        String token =
                jwtService.generateToken(
                        customer.getUserId(),
                        customer.getEmail()
                );

        return new LoginResponseDTO(
                customer.getUserId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                token,
                "Login successful"
        );
    }

    public MeResponseDTO getCurrentUser(
            String email
    ) {

        CustomerProfileData customer =
                customerRepository.findProfileByEmail(email);

        if (customer == null) {

            throw new IllegalArgumentException(
                    "Customer not found"
            );
        }

        return new MeResponseDTO(
                customer.getUserId(),
                customer.getFirstName(),
                customer.getMiddleName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhoneNo(),
                customer.getCity(),
                customer.getState(),
                customer.getPinCode()
        );
    }
}