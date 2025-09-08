package jacop.place.order.model;


import java.util.List;

public record PlaceOrderCommand(
        String idempotencyKey,
        List<OrderItem> items
) {
}
