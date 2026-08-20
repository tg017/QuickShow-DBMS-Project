package quickshow.dbms.project.repository;

import quickshow.dbms.project.model.Seat;
import quickshow.dbms.project.model.SeatId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, SeatId> {
}