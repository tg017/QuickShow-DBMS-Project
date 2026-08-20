package quickshow.dbms.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Theatre")
public class Theatre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TheatreID")
    private Integer theatreId;

    @Column(name = "Name", nullable = false, length = 50)
    private String name;

    @Column(name = "ContactNo", length = 10)
    private String contactNo;

    @Column(name = "BuildingName", length = 30)
    private String buildingName;

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

    @OneToMany(mappedBy = "theatre")
    private List<Screen> screens = new ArrayList<>();
}
