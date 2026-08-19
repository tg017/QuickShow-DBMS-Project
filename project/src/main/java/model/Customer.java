package model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserId")
    private Integer userId ;

    @Column(name = "FirstName", nullable = false, length = 20)
    private String firstName;

    @Column(name = "MiddleName", length = 20)
    private String middleName;

    @Column(name = "LastName", length = 20)
    private String lastName;

    @Column(name = "DOB")
    private LocalDate dob;

    @Enumerated(EnumType.STRING)
    @Column(name = "Gender")
    private Gender gender;

    @Column(name = "PhoneNo", length = 10)
    private String phoneNo;

    @Column(name = "Password", length = 255)
    private String password;

    @Column(name = "HouseNo", length = 10)
    private String houseNo;

    @Column(name = "Street", length = 20)
    private String street;

    @Column(name = "Area", length = 20)
    private String area;

    @Column(name = "City", length = 20)
    private String city;

    @Column(name = "State", length = 20)
    private String state;

    @Column(name = "PinCode", length = 10)
    private String pinCode;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomerEmail> emails = new ArrayList<>();

    public void addEmail(CustomerEmail email) {
        emails.add(email);
        email.setCustomer(this);
    }

    public void removeEmail(CustomerEmail email) {
        emails.remove(email);
        email.setCustomer(null);
    }
}
