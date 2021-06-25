package crm.spring.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

import crm.spring.rest.api.v1.dto.OrderDto;
import crm.spring.rest.model.Order;

@Component
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderMapper {
	
	@Mapping(target="customerId", source="customer.id")
	OrderDto mapOrderToOrderDto(Order order);
	
	@Mapping(target="customer.id", source="customerId")
	Order mapOrderDtoToOrder(OrderDto orderDto);
}
