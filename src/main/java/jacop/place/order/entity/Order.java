package jacop.place.order.entity;

import jacop.place.order.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "`order`")
public class Order {
    @Id
    private UUID id;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLine> lines = new ArrayList<>();

    public Order(List<OrderLine> lines) {
        this.id = UUID.randomUUID();
        this.status = OrderStatus.CREATED;
        this.lines = lines;
        this.lines.forEach(l -> l.setOrder(this));
    }

    public Order() {

    }
}
