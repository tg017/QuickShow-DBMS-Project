package quickshow.dbms.project.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import quickshow.dbms.project.dto.AdminLoginResponseDTO;
import quickshow.dbms.project.dto.AdminRegisterRequestDTO;
import quickshow.dbms.project.model.Admin;
import quickshow.dbms.project.repository.AdminRepository;
import quickshow.dbms.project.security.JwtService;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    public AdminService(
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {

        this.adminRepository =
                adminRepository;

        this.passwordEncoder =
                passwordEncoder;

        this.jwtService =
                jwtService;
    }


    // =========================================================
    // REGISTER ADMIN
    // =========================================================

    public void register(
            AdminRegisterRequestDTO request
    ) {

        Admin existingAdmin =
                adminRepository.findByEmail(
                        request.getEmail()
                );

        if (existingAdmin != null) {

            throw new IllegalArgumentException(
                    "Admin with this email already exists"
            );
        }

        String encodedPassword =
                passwordEncoder.encode(
                        request.getPassword()
                );

        adminRepository.createAdmin(
                request.getFirstName(),
                request.getMiddleName(),
                request.getLastName(),
                request.getRole(),
                encodedPassword,
                request.getEmail()
        );
    }


    // =========================================================
    // LOGIN ADMIN
    // =========================================================

    public AdminLoginResponseDTO login(
            String email,
            String password
    ) {

        Admin admin =
                adminRepository.findByEmail(
                        email
                );

        if (admin == null) {

            throw new IllegalArgumentException(
                    "Invalid email or password"
            );
        }

        boolean passwordMatches =
                passwordEncoder.matches(
                        password,
                        admin.getPassword()
                );

        if (!passwordMatches) {

            throw new IllegalArgumentException(
                    "Invalid email or password"
            );
        }

        String token =
                jwtService.generateAdminToken(
                        admin.getAdminId(),
                        admin.getEmail(),
                        admin.getRole()
                );

        return new AdminLoginResponseDTO(
                admin.getAdminId(),
                admin.getFirstName(),
                admin.getLastName(),
                admin.getEmail(),
                admin.getRole(),
                token,
                "Admin login successful"
        );
    }
}