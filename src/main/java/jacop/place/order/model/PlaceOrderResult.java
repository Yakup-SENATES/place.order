package jacop.place.order.model;

import jacop.place.order.enums.OrderStatus;

import java.util.UUID;

public record PlaceOrderResult(
        UUID orderId,
        OrderStatus status
) {
}
