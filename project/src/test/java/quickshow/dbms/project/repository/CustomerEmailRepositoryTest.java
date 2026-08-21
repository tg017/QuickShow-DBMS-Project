package quickshow.dbms.project.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import quickshow.dbms.project.model.Customer;
import quickshow.dbms.project.model.CustomerEmail;
import quickshow.dbms.project.model.CustomerEmailId;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomerEmailRepositoryTest {

    @Autowired
    private CustomerEmailRepository customerEmailRepository;

    @Autowired
    private CustomerRepository customerRepository;


    // =========================================================
    // CREATE
    // =========================================================

    @Test
    void shouldCreateCustomerEmail() {

        Customer customer =
                customerRepository.create(
                        createCustomer()
                );

        String email =
                uniqueEmail("create");

        CustomerEmail customerEmail =
                createEmail(
                        customer.getUserId(),
                        email
                );

        CustomerEmail createdEmail =
                customerEmailRepository.create(
                        customerEmail
                );

        assertNotNull(createdEmail);
        assertNotNull(createdEmail.getId());

        assertEquals(
                customer.getUserId(),
                createdEmail.getId().getUserId()
        );

        assertEquals(
                email,
                createdEmail.getId().getEmail()
        );
    }


    // =========================================================
    // CREATE + READ
    // =========================================================

    @Test
    void shouldCreateAndRetrieveCustomerEmailById() {

        Customer customer =
                customerRepository.create(
                        createCustomer()
                );

        String email =
                uniqueEmail("retrieve");

        CustomerEmail customerEmail =
                createEmail(
                        customer.getUserId(),
                        email
                );

        CustomerEmail createdEmail =
                customerEmailRepository.create(
                        customerEmail
                );

        CustomerEmail retrievedEmail =
                customerEmailRepository.findById(
                        createdEmail.getId()
                );

        assertNotNull(retrievedEmail);

        assertEquals(
                customer.getUserId(),
                retrievedEmail.getId().getUserId()
        );

        assertEquals(
                email,
                retrievedEmail.getId().getEmail()
        );
    }


    // =========================================================
    // FIND NON-EXISTENT EMAIL
    // =========================================================

    @Test
    void shouldReturnNullForNonExistentEmail() {

        CustomerEmailId id =
                new CustomerEmailId(
                        Integer.MAX_VALUE,
                        uniqueEmail("missing")
                );

        CustomerEmail customerEmail =
                customerEmailRepository.findById(id);

        assertNull(customerEmail);
    }


    // =========================================================
    // FIND ALL
    // =========================================================

    @Test
    void shouldFindAllCustomerEmails() {

        Customer customer =
                customerRepository.create(
                        createCustomer()
                );

        String email1 =
                uniqueEmail("all1");

        String email2 =
                uniqueEmail("all2");

        CustomerEmail savedEmail1 =
                customerEmailRepository.create(
                        createEmail(
                                customer.getUserId(),
                                email1
                        )
                );

        CustomerEmail savedEmail2 =
                customerEmailRepository.create(
                        createEmail(
                                customer.getUserId(),
                                email2
                        )
                );

        List<CustomerEmail> emails =
                customerEmailRepository.findAll();

        assertNotNull(emails);

        assertTrue(
                emails.stream()
                        .anyMatch(email ->
                                email.getId()
                                        .equals(
                                                savedEmail1
                                                        .getId()
                                        )
                        )
        );

        assertTrue(
                emails.stream()
                        .anyMatch(email ->
                                email.getId()
                                        .equals(
                                                savedEmail2
                                                        .getId()
                                        )
                        )
        );
    }


    // =========================================================
    // FIND BY CUSTOMER ID
    // =========================================================

    @Test
    void shouldFindEmailsByCustomerId() {

        Customer customer =
                customerRepository.create(
                        createCustomer()
                );

        String email1 =
                uniqueEmail("customer1");

        String email2 =
                uniqueEmail("customer2");

        customerEmailRepository.create(
                createEmail(
                        customer.getUserId(),
                        email1
                )
        );

        customerEmailRepository.create(
                createEmail(
                        customer.getUserId(),
                        email2
                )
        );

        List<CustomerEmail> emails =
                customerEmailRepository.findByCustomerId(
                        customer.getUserId()
                );

        assertEquals(
                2,
                emails.size()
        );

        assertTrue(
                emails.stream()
                        .anyMatch(email ->
                                email.getId()
                                        .getEmail()
                                        .equals(email1)
                        )
        );

        assertTrue(
                emails.stream()
                        .anyMatch(email ->
                                email.getId()
                                        .getEmail()
                                        .equals(email2)
                        )
        );

        assertTrue(
                emails.stream()
                        .allMatch(email ->
                                email.getId()
                                        .getUserId()
                                        .equals(
                                                customer.getUserId()
                                        )
                        )
        );
    }


    // =========================================================
    // FIND BY EMAIL
    // =========================================================

    @Test
    void shouldFindCustomerEmailByEmail() {

        Customer customer =
                customerRepository.create(
                        createCustomer()
                );

        String email =
                uniqueEmail("find");

        customerEmailRepository.create(
                createEmail(
                        customer.getUserId(),
                        email
                )
        );

        CustomerEmail result =
                customerEmailRepository.findByEmail(
                        email
                );

        assertNotNull(result);

        assertEquals(
                customer.getUserId(),
                result.getId().getUserId()
        );

        assertEquals(
                email,
                result.getId().getEmail()
        );
    }


    // =========================================================
    // UPDATE EMAIL
    // =========================================================

    @Test
    void shouldUpdateEmail() {

        Customer customer =
                customerRepository.create(
                        createCustomer()
                );

        String oldEmail =
                uniqueEmail("old");

        String newEmail =
                uniqueEmail("new");

        CustomerEmailId oldId =
                new CustomerEmailId(
                        customer.getUserId(),
                        oldEmail
                );

        customerEmailRepository.create(
                createEmail(
                        customer.getUserId(),
                        oldEmail
                )
        );

        int rowsUpdated =
                customerEmailRepository.updateEmail(
                        oldId,
                        newEmail
                );

        assertEquals(1, rowsUpdated);

        assertNull(
                customerEmailRepository.findById(
                        oldId
                )
        );

        CustomerEmail updatedEmail =
                customerEmailRepository.findById(
                        new CustomerEmailId(
                                customer.getUserId(),
                                newEmail
                        )
                );

        assertNotNull(updatedEmail);

        assertEquals(
                newEmail,
                updatedEmail.getId().getEmail()
        );
    }


    // =========================================================
    // UPDATE NON-EXISTENT EMAIL
    // =========================================================

    @Test
    void shouldUpdateZeroRowsForNonExistentEmail() {

        CustomerEmailId oldId =
                new CustomerEmailId(
                        Integer.MAX_VALUE,
                        uniqueEmail("missing")
                );

        int rowsUpdated =
                customerEmailRepository.updateEmail(
                        oldId,
                        uniqueEmail("new")
                );

        assertEquals(0, rowsUpdated);
    }


    // =========================================================
    // DELETE
    // =========================================================

    @Test
    void shouldDeleteCustomerEmail() {

        Customer customer =
                customerRepository.create(
                        createCustomer()
                );

        String email =
                uniqueEmail("delete");

        CustomerEmailId id =
                new CustomerEmailId(
                        customer.getUserId(),
                        email
                );

        customerEmailRepository.create(
                createEmail(
                        customer.getUserId(),
                        email
                )
        );

        assertTrue(
                customerEmailRepository.existsById(id)
        );

        int rowsDeleted =
                customerEmailRepository.deleteById(id);

        assertEquals(1, rowsDeleted);

        assertFalse(
                customerEmailRepository.existsById(id)
        );

        assertNull(
                customerEmailRepository.findById(id)
        );
    }


    // =========================================================
    // DELETE NON-EXISTENT EMAIL
    // =========================================================

    @Test
    void shouldDeleteZeroRowsForNonExistentEmail() {

        CustomerEmailId id =
                new CustomerEmailId(
                        Integer.MAX_VALUE,
                        uniqueEmail("missing")
                );

        int rowsDeleted =
                customerEmailRepository.deleteById(id);

        assertEquals(0, rowsDeleted);
    }


    // =========================================================
    // EXISTS
    // =========================================================

    @Test
    void shouldCheckCustomerEmailExistence() {

        Customer customer =
                customerRepository.create(
                        createCustomer()
                );

        String email =
                uniqueEmail("exists");

        CustomerEmailId id =
                new CustomerEmailId(
                        customer.getUserId(),
                        email
                );

        customerEmailRepository.create(
                createEmail(
                        customer.getUserId(),
                        email
                )
        );

        assertTrue(
                customerEmailRepository.existsById(id)
        );

        assertFalse(
                customerEmailRepository.existsById(
                        new CustomerEmailId(
                                customer.getUserId(),
                                uniqueEmail("missing")
                        )
                )
        );
    }


    // =========================================================
    // UNIQUE EMAIL CONSTRAINT
    // =========================================================

    @Test
    void shouldRejectDuplicateEmail() {

        Customer customer1 =
                customerRepository.create(
                        createCustomer()
                );

        Customer customer2 =
                customerRepository.create(
                        createCustomer()
                );

        String email =
                uniqueEmail("duplicate");

        customerEmailRepository.create(
                createEmail(
                        customer1.getUserId(),
                        email
                )
        );

        assertThrows(
                Exception.class,
                () ->
                        customerEmailRepository.create(
                                createEmail(
                                        customer2.getUserId(),
                                        email
                                )
                        )
        );
    }


    // =========================================================
    // FOREIGN KEY CONSTRAINT
    // =========================================================

    @Test
    void shouldRejectEmailForNonExistentCustomer() {

        CustomerEmail customerEmail =
                createEmail(
                        Integer.MAX_VALUE,
                        uniqueEmail("invalid")
                );

        assertThrows(
                Exception.class,
                () ->
                        customerEmailRepository.create(
                                customerEmail
                        )
        );
    }


    // =========================================================
    // DELETE ALL EMAILS OF CUSTOMER
    // =========================================================

    @Test
    void shouldDeleteAllEmailsOfCustomer() {

        Customer customer =
                customerRepository.create(
                        createCustomer()
                );

        String email1 =
                uniqueEmail("delete1");

        String email2 =
                uniqueEmail("delete2");

        customerEmailRepository.create(
                createEmail(
                        customer.getUserId(),
                        email1
                )
        );

        customerEmailRepository.create(
                createEmail(
                        customer.getUserId(),
                        email2
                )
        );

        int rowsDeleted =
                customerEmailRepository.deleteByCustomerId(
                        customer.getUserId()
                );

        assertEquals(2, rowsDeleted);

        List<CustomerEmail> emails =
                customerEmailRepository.findByCustomerId(
                        customer.getUserId()
                );

        assertTrue(emails.isEmpty());
    }


    // =========================================================
    // CASCADE DELETE
    // =========================================================

    @Test
    void shouldDeleteEmailsWhenCustomerIsDeleted() {

        Customer customer =
                customerRepository.create(
                        createCustomer()
                );

        String email =
                uniqueEmail("cascade");

        CustomerEmailId id =
                new CustomerEmailId(
                        customer.getUserId(),
                        email
                );

        customerEmailRepository.create(
                createEmail(
                        customer.getUserId(),
                        email
                )
        );

        assertTrue(
                customerEmailRepository.existsById(id)
        );

        int rowsDeleted =
                customerRepository.deleteById(
                        customer.getUserId()
                );

        assertEquals(1, rowsDeleted);

        assertFalse(
                customerEmailRepository.existsById(id)
        );
    }


    // =========================================================
    // HELPERS
    // =========================================================

    private CustomerEmail createEmail(
            Integer userId,
            String email
    ) {

        CustomerEmailId id =
                new CustomerEmailId(
                        userId,
                        email
                );

        return new CustomerEmail(
                id,
                null
        );
    }


    private Customer createCustomer() {

        Customer customer =
                new Customer();

        customer.setFirstName("Test");

        customer.setLastName("User");

        customer.setPassword("password");

        return customer;
    }


    private String uniqueEmail(String prefix) {

        return prefix +
                "_" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8) +
                "@test.com";
    }
}