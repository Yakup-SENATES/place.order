package jacop.place.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
public class OrderLine {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Her order line mutlaka bir order’a bağlıdır
    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @Column(name = "product_id", nullable = false, columnDefinition = "UUID")
    private UUID productId;

    @Column(name = "product_name", nullable = false)
    private String productName;// Denormalization: sipariş anındaki ürün adı sabit kalır

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "unit_price", precision = 15, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "line_total", precision = 15, scale = 2, nullable = false)
    private BigDecimal lineTotal;

    public OrderLine(UUID productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public OrderLine(Order order, UUID productId, String productName, Integer quantity, BigDecimal unitPrice) {
        this.order = order;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public void changeQuantity(Integer newQuantity) {
        this.quantity = newQuantity;
        this.lineTotal = this.unitPrice.multiply(BigDecimal.valueOf(newQuantity));
    }

    public OrderLine() {

    }
}
