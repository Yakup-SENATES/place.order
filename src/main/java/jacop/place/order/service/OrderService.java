package jacop.place.order.service;

import jacop.place.order.entity.*;
import jacop.place.order.enums.OrderStatus;
import jacop.place.order.model.*;
import jacop.place.order.repo.IdempotencyKeyRepository;
import jacop.place.order.repo.OrderRepository;
import jacop.place.order.repo.OutboxRepository;
import jacop.place.order.repo.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@RequiredArgsConstructor
@Service
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final OrderMapper mapper = OrderMapper.INSTANCE;

    @Transactional
    public PlaceOrderResult placeOrder(PlaceOrderCommand cmd) throws JSONException {
        // Idempotency first
        var existing = idempotencyKeyRepository.findByKeyValue(cmd.idempotencyKey());
        if (existing.isPresent()) {
            var orderId = existing.get().getOrderId();
            return new PlaceOrderResult(orderId, OrderStatus.CREATED);
        }

        // stock decrease with lock
        for (OrderItem item : cmd.items()) {
            var p = productRepository.findForUpdate(item.productId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));
            p.decreaseStock(item.quantity());
        }

        // place order
        var orderLines = cmd.items().stream()
                .map(i -> new OrderLine(i.productId(), i.quantity()))
                .toList();

        var order = new Order(orderLines);
        order = orderRepository.save(order);

        //OutBox event
        JSONObject json = new JSONObject();
        json.put("orderId", order.getId());
        var payload = json.toString();
        outboxRepository.save(new OutboxEvent("OrderPlaced", order.getId().toString(), payload));

        //save the idempotency key
        idempotencyKeyRepository.save(new IdempotencyKey(cmd.idempotencyKey(), order.getId()));
        return new PlaceOrderResult(order.getId(), order.getStatus());

    }

    public List<Order> findAllOrders() {
        return orderRepository.findAll();
        //return mapper.getOrderDtos(all);
    }
}
