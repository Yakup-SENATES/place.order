package jacop.place.order.controller;

import jacop.place.order.model.OrderItem;
import jacop.place.order.model.PlaceOrderCommand;
import jacop.place.order.model.PlaceOrderResult;
import jacop.place.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;


    @PostMapping
    public ResponseEntity<PlaceOrderResult> placeOrder(
            @RequestHeader("Idempotency-Key") String idemKey,
            @RequestBody List<OrderItem> items) throws JSONException {

        var res = orderService.placeOrder(new PlaceOrderCommand(idemKey, items));

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }


}
