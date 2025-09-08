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
import jacop.place.order.model.PlaceOrderResult;
import jacop.place.order.service.OrderService;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    MockMvc mockMvc;

    Gson gson;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    OrderService orderService;

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
    void placeOrder_shouldReturnCreated() throws Exception {

        when(orderService.placeOrder(any())).thenReturn(
                new PlaceOrderResult(uuid, OrderStatus.CREATED)
        );


        mockMvc.perform(
                        post("/api/orders")
                                .header("Idempotency-Key", "idem-123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(gson.toJson(items))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(uuid.toString()))
                .andExpect(jsonPath("$.status").value("CREATED"));

    }



}