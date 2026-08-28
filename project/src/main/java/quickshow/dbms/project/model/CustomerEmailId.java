package quickshow.dbms.project.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class CustomerEmailId implements Serializable {
    @Column(name = "UserID")
    private Integer userId;

    @Column(name = "Email", length = 100)
    private String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof CustomerEmailId)) return false;

        CustomerEmailId that = (CustomerEmailId) o;

        return Objects.equals(userId, that.userId)
                && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, email);
    }
}
