package jacop.place.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jacop.place.order.entity.Order;
import jacop.place.order.entity.OrderLine;
import jacop.place.order.entity.Product;
import jacop.place.order.enums.OrderStatus;
import jacop.place.order.model.OrderDTO;
import jacop.place.order.model.OrderItem;
import jacop.place.order.model.OrderLineDTO;
import jacop.place.order.model.PlaceOrderCommand;
import jacop.place.order.repo.OrderRepository;
import jacop.place.order.repo.OutboxRepository;
import jacop.place.order.repo.ProductRepository;
import jacop.place.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@WebMvcTest
class OrderControllerTest {

    @Autowired
    MockMvc mockMvc;

    Gson gson;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ProductRepository productRepository;
    @MockitoBean
    OrderService orderService;
    @MockitoBean
    OrderRepository orderRepository;
    @MockitoBean
    OutboxRepository outboxRepository;

    List<OrderItem> items;
    List<OrderDTO> orderDTOS;
    List<Order> orders;
    Product product;
    UUID uuid;

    @BeforeEach
    void setUp() {
        gson = new GsonBuilder()
                .addSerializationExclusionStrategy(
                        new ExclusionStrategy() {
                            @Override
                            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                                return fieldAttributes.getName().equals("order");
                            }

                            @Override
                            public boolean shouldSkipClass(Class<?> aClass) {
                                return false;
                            }
                        }
                ).create();
        uuid = UUID.randomUUID();
        product = new Product(uuid, "Laptop", 2);
        items = List.of(
                new OrderItem(uuid, 5)
        );
        orderDTOS = List.of(
                new OrderDTO( OrderStatus.CREATED,
                        List.of(
                                new OrderLineDTO(uuid, 3))
                )
        );
        orders = List.of(
                new Order(
                        List.of(
                                new OrderLine(uuid, 3))
                )
        );
    }

    @Test
    void getOrders_shouldFindAllOrders() throws Exception {

        when(orderRepository.findAll()).thenReturn(orders);

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(orders)));

    }

    @Test
    @DisplayName("place when given items is valid")
    void placeOrder_shouldFail_whenInSufficientStock() {
        // given
        when(productRepository.findForUpdate(product.getId())).thenReturn(Optional.of(product));
        var cmd = new PlaceOrderCommand("idem-2", items);

        //when
        assertThrows(IllegalStateException.class, () -> orderService.placeOrder(cmd));

        //then
        verify(orderRepository, never()).save(any());
        verify(outboxRepository, never()).save(any());

    }


}