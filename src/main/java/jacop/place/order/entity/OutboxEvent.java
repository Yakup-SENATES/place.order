package jacop.place.order.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
public class OutboxEvent {
    @Id
    @GeneratedValue
    private Long id;

    private String type; // "OrderPlaced"

    private String aggregatedId;

    @Lob
    private String payload;

    private Instant createdAt = Instant.now();

    private boolean published = false;

    protected OutboxEvent() {
    }

    public OutboxEvent( String type, String payload) {
        this.type = type;
        this.payload = payload;
    }
}
