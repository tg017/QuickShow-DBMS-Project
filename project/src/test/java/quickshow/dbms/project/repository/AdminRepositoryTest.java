package quickshow.dbms.project.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import quickshow.dbms.project.model.Admin;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AdminRepositoryTest {

    @Autowired
    private AdminRepository adminRepository;


    // =========================================================
    // CREATE
    // =========================================================

    @Test
    void shouldCreateAdmin() {

        Admin admin =
                createAdmin(
                        "John",
                        "Michael",
                        "Doe",
                        "MANAGER",
                        "password123",
                        "john@example.com"
                );

        Admin created =
                adminRepository.create(admin);

        assertNotNull(created);
        assertNotNull(created.getAdminId());

        assertEquals(
                "John",
                created.getFirstName()
        );

        assertEquals(
                "Michael",
                created.getMiddleName()
        );

        assertEquals(
                "Doe",
                created.getLastName()
        );

        assertEquals(
                "MANAGER",
                created.getRole()
        );

        assertEquals(
                "password123",
                created.getPassword()
        );

        assertEquals(
                "john@example.com",
                created.getEmail()
        );
    }


    // =========================================================
    // CREATE + READ
    // =========================================================

    @Test
    void shouldCreateAndRetrieveAdminById() {

        Admin admin =
                adminRepository.create(
                        createAdmin(
                                "Alice",
                                "Marie",
                                "Smith",
                                "ADMIN",
                                "secret",
                                "alice@example.com"
                        )
                );

        Admin retrieved =
                adminRepository.findById(
                        admin.getAdminId()
                );

        assertNotNull(retrieved);

        assertEquals(
                admin.getAdminId(),
                retrieved.getAdminId()
        );

        assertEquals(
                "Alice",
                retrieved.getFirstName()
        );

        assertEquals(
                "Marie",
                retrieved.getMiddleName()
        );

        assertEquals(
                "Smith",
                retrieved.getLastName()
        );

        assertEquals(
                "ADMIN",
                retrieved.getRole()
        );

        assertEquals(
                "secret",
                retrieved.getPassword()
        );

        assertEquals(
                "alice@example.com",
                retrieved.getEmail()
        );
    }


    // =========================================================
    // NULLABLE COLUMNS
    // =========================================================

    @Test
    void shouldAllowNullValues() {

        Admin admin =
                new Admin();

        admin.setFirstName(null);
        admin.setMiddleName(null);
        admin.setLastName(null);
        admin.setRole(null);
        admin.setPassword(null);
        admin.setEmail(null);

        Admin created =
                adminRepository.create(admin);

        assertNotNull(
                created.getAdminId()
        );

        Admin retrieved =
                adminRepository.findById(
                        created.getAdminId()
                );

        assertNotNull(retrieved);

        assertNull(
                retrieved.getFirstName()
        );

        assertNull(
                retrieved.getMiddleName()
        );

        assertNull(
                retrieved.getLastName()
        );

        assertNull(
                retrieved.getRole()
        );

        assertNull(
                retrieved.getPassword()
        );

        assertNull(
                retrieved.getEmail()
        );
    }


    // =========================================================
    // FIND NON-EXISTENT
    // =========================================================

    @Test
    void shouldReturnNullForNonExistentAdmin() {

        Admin admin =
                adminRepository.findById(
                        Integer.MAX_VALUE
                );

        assertNull(admin);
    }


    // =========================================================
    // FIND ALL
    // =========================================================

    @Test
    void shouldFindAllAdmins() {

        Admin admin1 =
                adminRepository.create(
                        createAdmin(
                                "First",
                                null,
                                "Admin",
                                "ADMIN",
                                "pass1",
                                "first@example.com"
                        )
                );

        Admin admin2 =
                adminRepository.create(
                        createAdmin(
                                "Second",
                                null,
                                "Admin",
                                "MANAGER",
                                "pass2",
                                "second@example.com"
                        )
                );

        List<Admin> admins =
                adminRepository.findAll();

        assertTrue(
                admins.stream()
                        .anyMatch(admin ->
                                admin.getAdminId()
                                        .equals(
                                                admin1.getAdminId()
                                        )
                        )
        );

        assertTrue(
                admins.stream()
                        .anyMatch(admin ->
                                admin.getAdminId()
                                        .equals(
                                                admin2.getAdminId()
                                        )
                        )
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Test
    void shouldUpdateAdmin() {

        Admin admin =
                adminRepository.create(
                        createAdmin(
                                "OldFirst",
                                "OldMiddle",
                                "OldLast",
                                "USER",
                                "oldPassword",
                                "old@example.com"
                        )
                );

        admin.setFirstName("NewFirst");
        admin.setMiddleName("NewMiddle");
        admin.setLastName("NewLast");
        admin.setRole("ADMIN");
        admin.setPassword("newPassword");
        admin.setEmail("new@example.com");

        int rowsUpdated =
                adminRepository.update(admin);

        assertEquals(1, rowsUpdated);

        Admin updated =
                adminRepository.findById(
                        admin.getAdminId()
                );

        assertNotNull(updated);

        assertEquals(
                "NewFirst",
                updated.getFirstName()
        );

        assertEquals(
                "NewMiddle",
                updated.getMiddleName()
        );

        assertEquals(
                "NewLast",
                updated.getLastName()
        );

        assertEquals(
                "ADMIN",
                updated.getRole()
        );

        assertEquals(
                "newPassword",
                updated.getPassword()
        );

        assertEquals(
                "new@example.com",
                updated.getEmail()
        );
    }


    // =========================================================
    // UPDATE NON-EXISTENT
    // =========================================================

    @Test
    void shouldUpdateZeroRowsForNonExistentAdmin() {

        Admin admin =
                createAdmin(
                        "Test",
                        null,
                        "Admin",
                        "ADMIN",
                        "password",
                        "test@example.com"
                );

        admin.setAdminId(
                Integer.MAX_VALUE
        );

        int rowsUpdated =
                adminRepository.update(admin);

        assertEquals(0, rowsUpdated);
    }


    // =========================================================
    // DELETE
    // =========================================================

    @Test
    void shouldDeleteAdmin() {

        Admin admin =
                adminRepository.create(
                        createAdmin(
                                "Delete",
                                null,
                                "Me",
                                "ADMIN",
                                "password",
                                "delete@example.com"
                        )
                );

        Integer adminId =
                admin.getAdminId();

        assertTrue(
                adminRepository.existsById(adminId)
        );

        int rowsDeleted =
                adminRepository.deleteById(adminId);

        assertEquals(1, rowsDeleted);

        assertFalse(
                adminRepository.existsById(adminId)
        );

        assertNull(
                adminRepository.findById(adminId)
        );
    }


    // =========================================================
    // DELETE NON-EXISTENT
    // =========================================================

    @Test
    void shouldDeleteZeroRowsForNonExistentAdmin() {

        int rowsDeleted =
                adminRepository.deleteById(
                        Integer.MAX_VALUE
                );

        assertEquals(0, rowsDeleted);
    }


    // =========================================================
    // EXISTS
    // =========================================================

    @Test
    void shouldCheckAdminExistence() {

        Admin admin =
                adminRepository.create(
                        createAdmin(
                                "Exists",
                                null,
                                "Test",
                                "ADMIN",
                                "password",
                                "exists@example.com"
                        )
                );

        assertTrue(
                adminRepository.existsById(
                        admin.getAdminId()
                )
        );

        assertFalse(
                adminRepository.existsById(
                        Integer.MAX_VALUE
                )
        );
    }


    // =========================================================
    // FIND BY EMAIL
    // =========================================================

    @Test
    void shouldFindAdminsByEmail() {

        Admin admin =
                adminRepository.create(
                        createAdmin(
                                "Email",
                                null,
                                "Test",
                                "ADMIN",
                                "password",
                                "specific@example.com"
                        )
                );

        List<Admin> results =
                adminRepository.findByEmail(
                        "specific@example.com"
                );

        assertTrue(
                results.stream()
                        .anyMatch(result ->
                                result.getAdminId()
                                        .equals(
                                                admin.getAdminId()
                                        )
                        )
        );
    }


    // =========================================================
    // FIND BY NON-EXISTENT EMAIL
    // =========================================================

    @Test
    void shouldReturnEmptyListForUnknownEmail() {

        List<Admin> results =
                adminRepository.findByEmail(
                        "doesnotexist@example.com"
                );

        assertTrue(results.isEmpty());
    }


    // =========================================================
    // FIND BY ROLE
    // =========================================================

    @Test
    void shouldFindAdminsByRole() {

        Admin admin1 =
                adminRepository.create(
                        createAdmin(
                                "Role1",
                                null,
                                "Test",
                                "MANAGER",
                                "password",
                                "role1@example.com"
                        )
                );

        Admin admin2 =
                adminRepository.create(
                        createAdmin(
                                "Role2",
                                null,
                                "Test",
                                "MANAGER",
                                "password",
                                "role2@example.com"
                        )
                );

        List<Admin> results =
                adminRepository.findByRole(
                        "MANAGER"
                );

        assertTrue(
                results.stream()
                        .anyMatch(result ->
                                result.getAdminId()
                                        .equals(
                                                admin1.getAdminId()
                                        )
                        )
        );

        assertTrue(
                results.stream()
                        .anyMatch(result ->
                                result.getAdminId()
                                        .equals(
                                                admin2.getAdminId()
                                        )
                        )
        );
    }


    // =========================================================
    // DUPLICATE EMAIL SHOULD BE ALLOWED
    // =========================================================

    @Test
    void shouldAllowDuplicateEmail() {

        Admin admin1 =
                adminRepository.create(
                        createAdmin(
                                "First",
                                null,
                                "User",
                                "ADMIN",
                                "password",
                                "same@example.com"
                        )
                );

        Admin admin2 =
                adminRepository.create(
                        createAdmin(
                                "Second",
                                null,
                                "User",
                                "ADMIN",
                                "password",
                                "same@example.com"
                        )
                );

        assertNotNull(admin1.getAdminId());
        assertNotNull(admin2.getAdminId());

        assertNotEquals(
                admin1.getAdminId(),
                admin2.getAdminId()
        );
    }


    // =========================================================
    // COLUMN LENGTH - FIRST NAME
    // =========================================================

    @Test
    void shouldRejectFirstNameExceedingColumnLength() {

        Admin admin =
                createAdmin(
                        "A".repeat(51),
                        null,
                        null,
                        null,
                        null,
                        null
                );

        assertThrows(
                Exception.class,
                () -> adminRepository.create(admin)
        );
    }


    // =========================================================
    // COLUMN LENGTH - ROLE
    // =========================================================

    @Test
    void shouldRejectRoleExceedingColumnLength() {

        Admin admin =
                createAdmin(
                        null,
                        null,
                        null,
                        "A".repeat(31),
                        null,
                        null
                );

        assertThrows(
                Exception.class,
                () -> adminRepository.create(admin)
        );
    }


    // =========================================================
    // COLUMN LENGTH - PASSWORD
    // =========================================================

    @Test
    void shouldRejectPasswordExceedingColumnLength() {

        Admin admin =
                createAdmin(
                        null,
                        null,
                        null,
                        null,
                        "A".repeat(256),
                        null
                );

        assertThrows(
                Exception.class,
                () -> adminRepository.create(admin)
        );
    }


    // =========================================================
    // COLUMN LENGTH - EMAIL
    // =========================================================

    @Test
    void shouldRejectEmailExceedingColumnLength() {

        Admin admin =
                createAdmin(
                        null,
                        null,
                        null,
                        null,
                        null,
                        "A".repeat(101)
                );

        assertThrows(
                Exception.class,
                () -> adminRepository.create(admin)
        );
    }


    // =========================================================
    // HELPER
    // =========================================================

    private Admin createAdmin(
            String firstName,
            String middleName,
            String lastName,
            String role,
            String password,
            String email
    ) {

        Admin admin =
                new Admin();

        admin.setFirstName(firstName);
        admin.setMiddleName(middleName);
        admin.setLastName(lastName);
        admin.setRole(role);
        admin.setPassword(password);
        admin.setEmail(email);

        return admin;
    }
}