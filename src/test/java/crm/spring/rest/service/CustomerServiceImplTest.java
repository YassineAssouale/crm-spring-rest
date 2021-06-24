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
import org.mockito.verification.VerificationMode;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;

import crm.spring.rest.config.AppConfig;
import crm.spring.rest.config.WebConfig;
import crm.spring.rest.model.Customer;
import crm.spring.rest.repository.CustomerRepository;
import crm.spring.rest.service.impl.CustomerServiceImpl;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {WebConfig.class, AppConfig.class}, loader = AnnotationConfigWebContextLoader.class)
@ActiveProfiles(profiles = "test")
public class CustomerServiceImplTest {
	
	@Mock
	private CustomerRepository customerRepositoryMock;
	
	@InjectMocks
	private CustomerServiceImpl customerService;
	
	private Customer customer;
	
	private List<Customer> customers;
	
	@BeforeEach
	void setup() {
		customer = new Customer();
		customer.setId(1);
		customer.setLastname("Toto");
		customer.setFirstname("Titi");
		
		customers = new ArrayList<>();
		customers.add(customer);
	}
	
	@Test
	void testGetAll() {
		Mockito.when(customerRepositoryMock.findAll(Sort.by("lastname").ascending())).thenReturn(customers);
		
		List<Customer> customers = customerService.getAllCustomersSortByLastnameAscending();
		assertEquals(1, customers.size());
		
		Mockito.verify(customerRepositoryMock).findAll(Sort.by("lastname").ascending());
	}
	
	@Test
	void testGetById() {
		Mockito.when(customerRepositoryMock.findById(1)).thenReturn(Optional.of(customer));
		
		Customer c = customerService.getCustomerById(1);
		assertEquals(c, customer);
		
		Mockito.verify(customerRepositoryMock).findById(1);
	}
	
	@Test
	void testCreate() {
		Mockito.when(customerRepositoryMock.save(customer)).thenReturn(customer);
		
		Customer c = customerService.createCustomer(customer);
		assertNotNull(c);
		
		Mockito.verify(customerRepositoryMock).save(customer);
	}
	
	@Test
	void testUpdate() {
		customer.setLastname("Another noun");
		Mockito.when(customerRepositoryMock.findById(1)).thenReturn(Optional.of(customer));
		Mockito.when(customerRepositoryMock.save(customer)).thenReturn(customer);
		customerService.updateCustomer(customer);
		
		Customer customisedCustomer = customerService.getCustomerById(1);
		
		assertEquals("Another noun", customisedCustomer.getLastname());
		Mockito.verify(customerRepositoryMock, VerificationModeFactory.times(2)).findById(1);
		Mockito.verify(customerRepositoryMock).save(customer);
	}
	
}
