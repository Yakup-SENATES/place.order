package jacop.place.order.model;

import jacop.place.order.entity.Order;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class OrderLineDTO {
    private Long id;

    private UUID productId;

    private int quantity;

    private Order order;

    public OrderLineDTO(UUID productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public OrderLineDTO() {

    }
}
