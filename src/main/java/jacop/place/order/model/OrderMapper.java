package jacop.place.order.model;

import jacop.place.order.entity.Order;
import jacop.place.order.entity.OrderLine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(target = "orderLines.order", ignore = true)
    OrderDTO getOrderDto(Order order);

    @Mapping(target = "orderLines.order", ignore = true)
    List<OrderDTO> getOrderDtos(List<Order> all);

    /*@Mapping(target = "order", ignore = true)
    OrderLineDTO getOrderLineDto(OrderLine orderLine);

    @Mapping(target = "order", ignore = true)
    List<OrderLineDTO> getOrderLineDtos(List<OrderLine> orderLines);


     */
}
