package crm.spring.rest.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;

import crm.spring.rest.config.AppConfig;
import crm.spring.rest.config.WebConfig;
import crm.spring.rest.model.Order;
import crm.spring.rest.repository.OrderRepository;
import crm.spring.rest.service.impl.OrderServiceImpl;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@WebAppConfiguration
@ContextConfiguration(classes = { WebConfig.class, AppConfig.class }, loader = AnnotationConfigWebContextLoader.class)
@ActiveProfiles(profiles = "test")
class OrderServiceImplTest {
	
	@Mock
	private OrderRepository orderRepositoryMock;
	
	@InjectMocks
	private OrderServiceImpl orderService;
	
	private Order order;
	
	private List<Order> orders;
	
	@BeforeEach
	void setup() {
		order = new Order ();
		order.setId(1);
		order.setLabel("labelTest");
		order.setStatus("Testing...");
		
		orders = new ArrayList<>();		
		orders.add(order);
	}

	@Test
	void testGetAll() {
		Mockito.when(orderRepositoryMock.findAll(Sort.by("label").ascending())).thenReturn(orders);
		
		List<Order> orders = orderService.getAllOrders();
		assertEquals(1, orders.size());
		
		Mockito.verify(orderRepositoryMock).findAll(Sort.by("label").ascending());
	}
	
	@Test
	void testGetById() {
		Mockito.when(orderRepositoryMock.findById(1)).thenReturn(Optional.of(order));
		Order myOrder = orderService.getById(1);
		assertEquals(myOrder, order);
		Mockito.verify(orderRepositoryMock).findById(1);
	}
	
	@Test
	void testCreate() {
		Mockito.when(orderRepositoryMock.save(order)).thenReturn(order);
		Order createdOrder = orderService.createOrder(order);
		assertNotNull(createdOrder);
		Mockito.verify(orderRepositoryMock).save(order);
	}
	
	@Test
	void testUpdate() {
		order.setLabel("labelTest2");
		Mockito.when(orderRepositoryMock.findById(1)).thenReturn(Optional.of(order));
		Mockito.when(orderRepositoryMock.save(order)).thenReturn(order);
		orderService.updateOrder(order);
		
		Order modifiedOrder = orderService.getById(1);
		
		assertEquals("labelTest2", modifiedOrder.getLabel());
		Mockito.verify(orderRepositoryMock, VerificationModeFactory.times(2)).findById(1);
		Mockito.verify(orderRepositoryMock).save(order);
	}
	
	@Test
	void testDelete() {
		Mockito.when(orderRepositoryMock.findById(1)).thenReturn(Optional.of(order));
		Mockito.doNothing().when(orderRepositoryMock).delete(order);
		orderService.deleteOrder(1);
		
		Mockito.verify(orderRepositoryMock).findById(1);
		Mockito.verify(orderRepositoryMock).delete(order);
	}
	
}
