package jacop.place.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Product {
    @Id
    private UUID id;
    private String name;
    private int stock;


    public void decreaseStock(int qty) {
        if (qty <= 0 ) throw new IllegalArgumentException("quantity less than 0");
        if (stock < qty) throw new IllegalStateException("Insufficient stock");
        this.stock -= qty;

    }
}
