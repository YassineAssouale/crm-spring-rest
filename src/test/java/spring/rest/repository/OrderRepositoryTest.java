package spring.rest.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import crm.spring.rest.config.AppConfig;
import crm.spring.rest.model.Order;
import crm.spring.rest.repository.OrderRepository;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { AppConfig.class }, loader = AnnotationConfigContextLoader.class)
@Transactional
@DirtiesContext
@TestMethodOrder(OrderAnnotation.class)
@ActiveProfiles(profiles = "test")
class OrderRepositoryTest {

	public static final String LABEL = "Formation Java";
	public static final String LASTNAME = "GILBERT";

	@Autowired
	private OrderRepository orderRepository;

	@Test
	@org.junit.jupiter.api.Order(1)
	void findOrderByIdTest() {
		Optional<Order> order = orderRepository.findById(1);
		Assertions.assertNotNull(order.get(), "Order not found");
	}

	@Test
	@org.junit.jupiter.api.Order(2)
	void findOrderByIdLabelTest() {
		Optional<Order> order = orderRepository.findById(1);
		Assertions.assertEquals(LABEL, order.get().getLabel(), "Order label should be " + LABEL);
	}

	@Test
	@org.junit.jupiter.api.Order(3)
	void givenOrder_thenReturnExpectedCustomer() {
		Optional<Order> order = orderRepository.findById(1);
		Assertions.assertEquals(LASTNAME, order.get().getCustomer().getLastname(),
				"Customer lastname should be " + LASTNAME);
	}

	@Test
	@org.junit.jupiter.api.Order(4)
	void listOfOrdersTest() {
		List<Order> orders = orderRepository.findAll();
		Assertions.assertEquals(3, orders.size(), "Wrong number of orders");
	}

	@Test
	@org.junit.jupiter.api.Order(5)
	void findOrdersByTypeAndStatusTest() {
		List<Order> orders = orderRepository.findByStatusAndType("En attente", "Forfait");
		Assertions.assertEquals(1, orders.size(), "Wrong number of orders for type and status");
	}

	@Test
	@org.junit.jupiter.api.Order(6)
	void testFindByNumberOfDays() {
		List<Order> orders = orderRepository.findByNumberOfDays(Double.valueOf(2.0));
		Assertions.assertEquals(2, orders.size(), "Wrong number of orders for a number of days greater than 2");
	}

	@Test
	@org.junit.jupiter.api.Order(7)
	void testCreate() {
		Order newOrder = new Order();
		newOrder.setLabel(LABEL);
		newOrder.setNumberOfDays(Double.valueOf(5));
		newOrder.setAdrEt(Double.valueOf(350));
		newOrder.setTva(Double.valueOf(20.0));
		newOrder.setType("Super commande");
		newOrder.setStatus("En cours");
		newOrder.setNotes("Les notes sur la commande");

		List<Order> orders = orderRepository.findAll();
		int numberOfOrdersBeforeCreation = orders.size();

		orderRepository.save(newOrder);

		List<Order> ordersAfterCreation = orderRepository.findAll();
		int numberOfOrdersAfterCreation = ordersAfterCreation.size();
		Assertions.assertEquals(numberOfOrdersBeforeCreation + 1, numberOfOrdersAfterCreation);
	}

	@Test
	@org.junit.jupiter.api.Order(8)
	void testUpdate() {

		Optional<Order> order = orderRepository.findById(2);
		order.get().setLabel("Nouveau libellé");

		orderRepository.save(order.get());

		Optional<Order> updatedOrder = orderRepository.findById(2);
		Assertions.assertEquals("Nouveau libellé", updatedOrder.get().getLabel());
	}

	@Test
	@org.junit.jupiter.api.Order(9)
	void testDelete() {
		Optional<Order> order = orderRepository.findById(2);

		List<Order> orders = orderRepository.findAll();
		int numberOfOrdersBeforeCreation = orders.size();

		orderRepository.delete(order.get());

		List<Order> ordersAfterCreation = orderRepository.findAll();
		int numberOfOrdersAfterCreation = ordersAfterCreation.size();

		Assertions.assertEquals(numberOfOrdersBeforeCreation - 1, numberOfOrdersAfterCreation,
				"Deleted order must be null");
	}

	@Test
	@org.junit.jupiter.api.Order(10)
	void testUpdateStatus() {

		List<Order> ordersToUpdate = orderRepository.findByStatusAndType("En cours", "Forfait");

		ordersToUpdate.forEach(order -> order.setStatus("Terminé"));

		ordersToUpdate.forEach(order -> orderRepository.save(order));

		List<Order> updatedOrders = orderRepository.findByStatusAndType("Terminé", "Forfait");
		Assertions.assertEquals(1, updatedOrders.size(), "Wrong number of orders with type Forfait and status Terminé");
	}

}
