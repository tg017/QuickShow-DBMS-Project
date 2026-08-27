package quickshow.dbms.project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import quickshow.dbms.project.dto.*;
import quickshow.dbms.project.security.AuthenticatedCustomer;
import quickshow.dbms.project.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(
            @RequestBody RegisterRequestDTO request
    ) {

        RegisterResponseDTO response =
                authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody LoginRequestDTO request
    ) {

        try {

            LoginResponseDTO response =
                    authService.login(request);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponseDTO> getCurrentUser(
            Authentication authentication
    ) {

        AuthenticatedCustomer customer =
                (AuthenticatedCustomer)
                        authentication.getPrincipal();

        MeResponseDTO response =
                authService.getCurrentUser(
                        customer.getEmail()
                );

        return ResponseEntity.ok(response);
    }
}