package crm.spring.rest.api.v1;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import crm.spring.rest.api.v1.dto.OrderDto;
import crm.spring.rest.exception.UnknownResourceException;
import crm.spring.rest.mapper.OrderMapper;
import crm.spring.rest.service.OrderService;

@RestController // MVC REST : RESTful Service
@RequestMapping("/v1/orders")
/**
 * Allows a webpage to request additional resources into browser from other domains e.g. fonts, CSS or static images from CDN. 
 * CORS helps in serving web content from multiple domains into browsers who usually have the same-origin security policy.
 * value = {"*"} : means all origins are allowed
 * allowedHeaders = {"*"} : means all headers requested by the client are allowed
 */
@CrossOrigin(value ={"*"} ,allowedHeaders = {"*"})
public class OrderApi {

	Logger log = LoggerFactory.getLogger(OrderApi.class);
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	OrderMapper orderMapper;
	
	
	@GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
	@Transactional
	public ResponseEntity<List<OrderDto>> getAll(){
		return ResponseEntity.ok(orderService.getAllOrders()
				.stream()
				.map(orderMapper::mapOrderToOrderDto)
				.collect(Collectors.toList()));
	}
	
	@GetMapping(value ="/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
	@Transactional
	public ResponseEntity<OrderDto> getById(@PathVariable final Integer id){
		try {
			return ResponseEntity.ok(orderMapper.mapOrderToOrderDto(orderService.getById(id)));
		} catch (UnknownResourceException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order Not Found");
		}
	}
	
	@PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
	@Transactional
	public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto orderDto){
		log.debug("Attempting to create order with label {}", orderDto.getLabel());
		OrderDto newOrder = orderMapper.mapOrderToOrderDto(orderService.createOrder(orderMapper.mapOrderDtoToOrder(orderDto)));
		return ResponseEntity.created(URI.create("/v1/orders/" + newOrder.getId())).body(newOrder);
	}
	
	
	@DeleteMapping(path = "{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<Void> deleteOrder(@PathVariable final Integer id){
		try {
			log.debug("Attempting to delete order {}", id);
			orderService.deleteOrder(id);
			return ResponseEntity.noContent().build();
		} catch (UnknownResourceException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Order Not Found!");
		}
		
	}
	
	@PutMapping(path = "{id}",produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
	public ResponseEntity <Void> updateOrder(@PathVariable final Integer id, @RequestBody OrderDto orderDto){
		try {
			log.debug("Updating order {}",id);
			orderDto.setId(id);
			orderService.updateOrder(orderMapper.mapOrderDtoToOrder(orderDto));
			log.debug("Successfullu updated order {}",id);
			return ResponseEntity.noContent().build();
		} catch (UnknownResourceException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Order Not Found!");
		}
	}
	
	@PatchMapping(path ="{id}", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
	public ResponseEntity<Void> updateOrderLabel(@PathVariable final Integer id, @RequestParam String label){
		try {
			log.debug("Updating order {}", id);
			orderService.patchOrderLabel(id, label);
			log.debug("Successfully updated order {}",id);
			return ResponseEntity.noContent().build();
			
		} catch (UnknownResourceException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Order Not Found!");
		}
	}
}
