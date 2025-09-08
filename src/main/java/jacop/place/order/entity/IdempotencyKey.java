package jacop.place.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
public class IdempotencyKey {

    @Id
    @GeneratedValue
    private Long id;
    @Column(name="key_value", unique = true)
    private String keyValue;
    private UUID orderId;

    public IdempotencyKey() {
    }

    public IdempotencyKey( String keyValue, UUID orderId) {
        this.keyValue = keyValue;
        this.orderId = orderId;
    }
}
