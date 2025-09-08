package jacop.place.order.service;

import jacop.place.order.entity.Product;
import jacop.place.order.enums.OrderStatus;
import jacop.place.order.model.OrderItem;
import jacop.place.order.model.OrderMapper;
import jacop.place.order.model.PlaceOrderCommand;
import jacop.place.order.model.PlaceOrderResult;
import jacop.place.order.repo.IdempotencyKeyRepository;
import jacop.place.order.repo.OrderRepository;
import jacop.place.order.repo.OutboxRepository;
import jacop.place.order.repo.ProductRepository;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    ProductRepository productRepository = mock(ProductRepository.class);
    OrderRepository orderRepository = mock(OrderRepository.class);
    OutboxRepository outboxRepository = mock(OutboxRepository.class);
    IdempotencyKeyRepository idempotencyRepository = mock(IdempotencyKeyRepository.class);

    OrderService orderService =
            new OrderService(productRepository, orderRepository, outboxRepository, idempotencyRepository);

    List<OrderItem> items;
    Product product;
    UUID uuid;
    //OrderMapper mapper = OrderMapper.INSTANCE;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        product = new Product(uuid, "Laptop", 10);
        items = List.of(
                new OrderItem(uuid, 5)
        );
    }

    @Test
    void placeOrder_shouldDecreaseStock_andCreateOrder_andEmitEvent() throws JSONException {
        //given
        when(productRepository.findForUpdate(uuid)).thenReturn(Optional.of(product));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        //when
        PlaceOrderResult res = orderService.placeOrder(
                new PlaceOrderCommand("idem-2", items)
        );

        // then
        assertEquals(OrderStatus.CREATED, res.status());
        assertEquals(5, product.getStock());
        verify(outboxRepository).save(argThat(e -> e.getType().equals("OrderPlaced")));

    }

    @Test
    @DisplayName("valid outbox event test")
    void placeOrder_shouldCreateOutboxEvent() throws JSONException {
        when(productRepository.findForUpdate(uuid)).thenReturn(Optional.of(product));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = orderService.placeOrder(
                new PlaceOrderCommand(
                        "idem-456", items
                )
        );

        //stok must decrease
        assertEquals(5, product.getStock());

        //outbox record must be created
        verify(outboxRepository).save(argThat(
                e -> e.getType().equalsIgnoreCase("OrderPlaced") &&
                        e.getAggregatedId().equals(result.orderId().toString())
        ));
    }

    @Test
    @DisplayName("test insufficient stock ")
    void placeOrder_shouldThrowIllegalStateException_whenInsufficientStock() {
        product.setStock(2);// stock düşürüldü
        when(productRepository.findForUpdate(uuid)).thenReturn(Optional.of(product));

        //when
        var placeOrderCommand = new PlaceOrderCommand("idem-798", items);

        // then
        assertThrows(IllegalStateException.class, () -> orderService.placeOrder(placeOrderCommand));
        verify(orderRepository, never()).save(any());
        verify(outboxRepository, never()).save(any());
        verify(productRepository, times(1)).findForUpdate(uuid);

    }


}
