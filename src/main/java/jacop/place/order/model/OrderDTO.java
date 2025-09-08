package jacop.place.order.model;

import jacop.place.order.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    private OrderStatus status;

    private List<OrderLineDTO> lines = new ArrayList<>();

}
