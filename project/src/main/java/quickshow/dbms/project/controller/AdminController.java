package quickshow.dbms.project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import quickshow.dbms.project.dto.AdminLoginResponseDTO;
import quickshow.dbms.project.dto.AdminRegisterRequestDTO;
import quickshow.dbms.project.dto.LoginRequestDTO;
import quickshow.dbms.project.service.AdminService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(
            AdminService adminService
    ) {
        this.adminService =
                adminService;
    }


    // =========================================================
    // REGISTER
    // =========================================================

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody AdminRegisterRequestDTO request
    ) {

        adminService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        "Admin registered successfully"
                );
    }


    // =========================================================
    // LOGIN
    // =========================================================

    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponseDTO> login(
            @RequestBody LoginRequestDTO request
    ) {

        AdminLoginResponseDTO response =
                adminService.login(
                        request.getEmail(),
                        request.getPassword()
                );

        return ResponseEntity.ok(response);
    }
}