package crm.spring.rest.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

import crm.spring.rest.api.v1.dto.CustomerDto;
import crm.spring.rest.api.v1.dto.OrderDto;
import crm.spring.rest.model.Customer;

@Component
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CustomerMapper {
	
	@Mapping(target = "orders", expression = "java(getOrders(customer))")
    CustomerDto mapCustomerToCustomerDto(Customer customer);

	default List<OrderDto> getOrders(Customer customer) {
		List<OrderDto> orders = new ArrayList<>();
		if (null != customer.getOrders()) {
			orders = customer.getOrders().stream().map(order -> new OrderDto(((OrderDto) order).getId(), ((OrderDto) order).getLabel(), ((OrderDto) order).getAdrEt(),
					((OrderDto) order).getNumberOfDays(), ((OrderDto) order).getTva(), ((OrderDto) order).getStatus(), ((OrderDto) order).getType(), ((OrderDto) order).getNotes(), customer.getId()))
	                .collect(Collectors.toList());
		}
        return orders;
    }
	
    Customer mapCustomerDtoToCustomer(CustomerDto customerDto);

}