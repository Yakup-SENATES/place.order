package jacop.place.order.repo;

import jacop.place.order.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {


    List<OutboxEvent> findAllByPublished(boolean published);
}
