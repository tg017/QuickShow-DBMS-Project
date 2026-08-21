package quickshow.dbms.project.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import quickshow.dbms.project.model.Customer;
import quickshow.dbms.project.model.Gender;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    // =========================================================
    // CREATE
    // =========================================================

    @Test
    void shouldCreateCustomer() {

        Customer customer = createCompleteCustomer();

        Customer createdCustomer = customerRepository.create(customer);

        assertNotNull(createdCustomer);
        assertNotNull(createdCustomer.getUserId());

        assertEquals(
                "John",
                createdCustomer.getFirstName());

        assertEquals(
                "Doe",
                createdCustomer.getLastName());

        assertEquals(
                Gender.M,
                createdCustomer.getGender());
    }

    // =========================================================
    // CREATE + READ
    // =========================================================

    @Test
    void shouldCreateAndRetrieveCustomerById() {

        Customer customer = createCompleteCustomer();

        Customer createdCustomer = customerRepository.create(customer);

        Customer retrievedCustomer = customerRepository.findById(
                createdCustomer.getUserId());

        assertNotNull(retrievedCustomer);

        assertEquals(
                createdCustomer.getUserId(),
                retrievedCustomer.getUserId());

        assertEquals(
                "John",
                retrievedCustomer.getFirstName());

        assertEquals(
                "Michael",
                retrievedCustomer.getMiddleName());

        assertEquals(
                "Doe",
                retrievedCustomer.getLastName());

        assertEquals(
                LocalDate.of(2000, 5, 15),
                retrievedCustomer.getDob());

        assertEquals(
                Gender.M,
                retrievedCustomer.getGender());

        assertEquals(
                "9876543210",
                retrievedCustomer.getPhoneNo());

        assertEquals(
                "password123",
                retrievedCustomer.getPassword());

        assertEquals(
                "101",
                retrievedCustomer.getHouseNo());

        assertEquals(
                "Main Street",
                retrievedCustomer.getStreet());

        assertEquals(
                "Central Area",
                retrievedCustomer.getArea());

        assertEquals(
                "Delhi",
                retrievedCustomer.getCity());

        assertEquals(
                "Delhi",
                retrievedCustomer.getState());

        assertEquals(
                "110001",
                retrievedCustomer.getPinCode());
    }

    // =========================================================
    // FIND NON-EXISTENT CUSTOMER
    // =========================================================

    @Test
    void shouldReturnNullForNonExistentCustomer() {

        Customer customer = customerRepository.findById(
                Integer.MAX_VALUE);

        assertNull(customer);
    }

    // =========================================================
    // FIND ALL
    // =========================================================

    @Test
    void shouldFindAllCustomers() {

        Customer customer1 = createCompleteCustomer();

        customer1.setFirstName("TestCustomer1");

        Customer customer2 = createCompleteCustomer();

        customer2.setFirstName("TestCustomer2");

        Customer savedCustomer1 = customerRepository.create(customer1);

        Customer savedCustomer2 = customerRepository.create(customer2);

        List<Customer> customers = customerRepository.findAll();

        assertNotNull(customers);

        assertTrue(
                customers.stream()
                        .anyMatch(customer -> customer.getUserId()
                                .equals(
                                        savedCustomer1
                                                .getUserId())));

        assertTrue(
                customers.stream()
                        .anyMatch(customer -> customer.getUserId()
                                .equals(
                                        savedCustomer2
                                                .getUserId())));
    }

    // =========================================================
    // UPDATE
    // =========================================================

    @Test
    void shouldUpdateCustomer() {

        Customer customer = customerRepository.create(
                createCompleteCustomer());

        customer.setFirstName("Updated");
        customer.setLastName("Customer");
        customer.setCity("Mumbai");
        customer.setPhoneNo("9999999999");
        customer.setGender(Gender.F);

        int rowsUpdated = customerRepository.update(customer);

        assertEquals(1, rowsUpdated);

        Customer updatedCustomer = customerRepository.findById(
                customer.getUserId());

        assertNotNull(updatedCustomer);

        assertEquals(
                "Updated",
                updatedCustomer.getFirstName());

        assertEquals(
                "Customer",
                updatedCustomer.getLastName());

        assertEquals(
                "Mumbai",
                updatedCustomer.getCity());

        assertEquals(
                "9999999999",
                updatedCustomer.getPhoneNo());

        assertEquals(
                Gender.F,
                updatedCustomer.getGender());
    }

    // =========================================================
    // UPDATE NON-EXISTENT CUSTOMER
    // =========================================================

    @Test
    void shouldUpdateZeroRowsForNonExistentCustomer() {

        Customer customer = createCompleteCustomer();

        customer.setUserId(
                Integer.MAX_VALUE);

        int rowsUpdated = customerRepository.update(customer);

        assertEquals(0, rowsUpdated);
    }

    // =========================================================
    // DELETE
    // =========================================================

    @Test
    void shouldDeleteCustomer() {

        Customer customer = customerRepository.create(
                createCompleteCustomer());

        Integer userId = customer.getUserId();

        assertTrue(
                customerRepository.existsById(userId));

        int rowsDeleted = customerRepository.deleteById(userId);

        assertEquals(1, rowsDeleted);

        assertFalse(
                customerRepository.existsById(userId));

        assertNull(
                customerRepository.findById(userId));
    }

    // =========================================================
    // DELETE NON-EXISTENT CUSTOMER
    // =========================================================

    @Test
    void shouldDeleteZeroRowsForNonExistentCustomer() {

        int rowsDeleted = customerRepository.deleteById(
                Integer.MAX_VALUE);

        assertEquals(0, rowsDeleted);
    }

    // =========================================================
    // EXISTS
    // =========================================================

    @Test
    void shouldCheckCustomerExistence() {

        Customer customer = customerRepository.create(
                createCompleteCustomer());

        Integer userId = customer.getUserId();

        assertTrue(
                customerRepository.existsById(userId));

        assertFalse(
                customerRepository.existsById(
                        Integer.MAX_VALUE));
    }

    // =========================================================
    // NULLABLE FIELDS
    // =========================================================

    @Test
    void shouldCreateCustomerWithNullableFields() {

        Customer customer = new Customer();

        customer.setFirstName("Minimal");
        customer.setPassword("password");

        Customer createdCustomer = customerRepository.create(customer);

        Customer retrievedCustomer = customerRepository.findById(
                createdCustomer.getUserId());

        assertNotNull(retrievedCustomer);

        assertEquals(
                "Minimal",
                retrievedCustomer.getFirstName());

        assertNull(
                retrievedCustomer.getMiddleName());

        assertNull(
                retrievedCustomer.getLastName());

        assertNull(
                retrievedCustomer.getDob());

        assertNull(
                retrievedCustomer.getGender());

        assertNull(
                retrievedCustomer.getPhoneNo());

        assertNull(
                retrievedCustomer.getHouseNo());

        assertNull(
                retrievedCustomer.getStreet());

        assertNull(
                retrievedCustomer.getArea());

        assertNull(
                retrievedCustomer.getCity());

        assertNull(
                retrievedCustomer.getState());

        assertNull(
                retrievedCustomer.getPinCode());
    }

    // =========================================================
    // GENDER MAPPING
    // =========================================================

    @Test
    void shouldPersistGenderCorrectly() {

        for (Gender gender : Gender.values()) {

            Customer customer = createCompleteCustomer();

            customer.setFirstName(
                    "GenderTest");

            customer.setPhoneNo(
                    "9" +
                            String.format(
                                    "%09d",
                                    gender.ordinal()));

            customer.setGender(gender);

            Customer createdCustomer = customerRepository.create(
                    customer);

            Customer retrievedCustomer = customerRepository.findById(
                    createdCustomer.getUserId());

            assertNotNull(retrievedCustomer);

            assertEquals(
                    gender,
                    retrievedCustomer.getGender());
        }
    }

    // =========================================================
    // NULL GENDER
    // =========================================================

    @Test
    void shouldAllowNullGender() {

        Customer customer = createCompleteCustomer();

        customer.setGender(null);

        Customer createdCustomer = customerRepository.create(customer);

        Customer retrievedCustomer = customerRepository.findById(
                createdCustomer.getUserId());

        assertNotNull(retrievedCustomer);

        assertNull(
                retrievedCustomer.getGender());
    }

    // =========================================================
    // FIND BY PHONE
    // =========================================================

    @Test
    void shouldFindCustomersByPhoneNumber() {

        Customer customer = createCompleteCustomer();

        customer.setPhoneNo(
                "8888888888");

        Customer savedCustomer = customerRepository.create(
                customer);

        List<Customer> results = customerRepository.findByPhoneNo(
                "8888888888");

        assertTrue(
                results.stream()
                        .anyMatch(c -> c.getUserId()
                                .equals(
                                        savedCustomer
                                                .getUserId())));
    }

    // =========================================================
    // FIND BY CITY
    // =========================================================

    @Test
    void shouldFindCustomersByCity() {

        Customer customer = createCompleteCustomer();

        customer.setCity("Bangalore");

        Customer savedCustomer = customerRepository.create(
                customer);

        List<Customer> results = customerRepository.findByCity(
                "Bangalore");

        assertTrue(
                results.stream()
                        .anyMatch(c -> c.getUserId()
                                .equals(
                                        savedCustomer
                                                .getUserId())));
    }

    // =========================================================
    // FIND BY GENDER
    // =========================================================

    @Test
    void shouldFindCustomersByGender() {

        Customer customer = createCompleteCustomer();

        customer.setGender(Gender.F);

        Customer savedCustomer = customerRepository.create(
                customer);

        List<Customer> results = customerRepository.findByGender(
                Gender.F);

        assertTrue(
                results.stream()
                        .anyMatch(c -> c.getUserId()
                                .equals(
                                        savedCustomer
                                                .getUserId())));

        assertTrue(
                results.stream()
                        .allMatch(c -> c.getGender() == Gender.F));
    }

    // =========================================================
    // NOT NULL CONSTRAINT
    // =========================================================

    @Test
    void shouldRejectCustomerWithoutFirstName() {

        Customer customer = createCompleteCustomer();

        customer.setFirstName(null);

        assertThrows(
                Exception.class,
                () -> customerRepository.create(
                        customer));
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private Customer createCompleteCustomer() {

        Customer customer = new Customer();

        customer.setFirstName("John");

        customer.setMiddleName("Michael");

        customer.setLastName("Doe");

        customer.setDob(
                LocalDate.of(
                        2000,
                        5,
                        15));

        customer.setGender(
                Gender.M);

        customer.setPhoneNo(
                "9876543210");

        customer.setPassword(
                "password123");

        customer.setHouseNo(
                "101");

        customer.setStreet(
                "Main Street");

        customer.setArea(
                "Central Area");

        customer.setCity(
                "Delhi");

        customer.setState(
                "Delhi");

        customer.setPinCode(
                "110001");

        return customer;
    }

    private Customer createCustomer(
            String firstName,
            String city) {

        Customer customer = createCompleteCustomer();

        customer.setFirstName(firstName);
        customer.setCity(city);

        return customer;
    }
}