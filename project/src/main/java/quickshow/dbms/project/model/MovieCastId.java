package quickshow.dbms.project.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieCastId implements Serializable {

    @Column(name = "MovieID")
    private Integer movieId;

    @Column(name = "Actor", length = 100)
    private String actor;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof MovieCastId)) return false;

        MovieCastId that = (MovieCastId) o;

        return Objects.equals(movieId, that.movieId)
                && Objects.equals(actor, that.actor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movieId, actor);
    }
}
