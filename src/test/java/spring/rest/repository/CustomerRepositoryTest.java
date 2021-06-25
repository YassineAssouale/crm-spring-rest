package spring.rest.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import crm.spring.rest.config.AppConfig;
import crm.spring.rest.model.Customer;
import crm.spring.rest.repository.CustomerRepository;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AppConfig.class}, loader = AnnotationConfigContextLoader.class)
@Transactional
@DirtiesContext
@ActiveProfiles(profiles = "test")
public class CustomerRepositoryTest {
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Test
	void testGetAllCustomers() {
		List<Customer> customers = customerRepository.findAll();
		assertEquals(4, customers.size());
	}
	
	@Test
	void testGetCustomersByActive() {
		List<Customer> activeCutomers = customerRepository.findByActive(true);
		assertEquals(2, activeCutomers.size());
	}
	
	@Test
	void testGetCustomerByActiveWithPagination() {
		// First page with 1 element per page
		Pageable pagingCustomer1 = PageRequest.of(0, 1);
		Page<Customer> customerPage1 = customerRepository.findByActive(true, pagingCustomer1);
		System.out.println("Customers pagination : page N° = " + customerPage1.getNumber() + 
				", number of element on the page = " + customerPage1.getNumberOfElements() +
				", totat number of elements = " + customerPage1.getTotalElements() + 
				", totat number of pages = " + customerPage1.getTotalPages());
		assertEquals(1, customerPage1.getNumberOfElements());
		
		Pageable pagingCustomer2 = PageRequest.of(1, 1);
		Page<Customer> customerPage2 = customerRepository.findByActive(true, pagingCustomer2);
		System.out.println("Customers pagination : page N° = " + customerPage2.getNumber() + 
				", number of element on the page = " + customerPage2.getNumberOfElements() +
				", totat number of elements = " + customerPage2.getTotalElements() + 
				", totat number of pages = " + customerPage2.getTotalPages());
		assertEquals(1, customerPage2.getNumberOfElements());
	}
	
	@Test
	void testGetCustomersWithMobile() {
		List<Customer> customersWM = customerRepository.findCustomerWithMobil();
		customersWM.forEach(c -> assertNotNull(c.getMobile()));
	}
	
	@Test
	void testCreate() {
		Customer newCustomer = new Customer();
		newCustomer.setFirstname("Winnie");
		newCustomer.setLastname("L'Ourson");
		newCustomer.setCompany("Disney");
		newCustomer.setPhone("0222222222");
		newCustomer.setMobile("0666666666");
		newCustomer.setMail("winnie.l.ourson@disney.com");
		newCustomer.setNotes("Les notes de Winnie");
		newCustomer.setActive(true);
		
		customerRepository.save(newCustomer);
		
		Customer c = customerRepository.findByLastname("L'Ourson");
		Assertions.assertNotNull(c, "Winnie not found!");
	}
	
	@Test
	void testUpdate() {
		Customer customer = customerRepository.findByLastname("GILBERT");
		customer.setCompany("NBE");
		
		customerRepository.save(customer);
		
		Customer updatedCustomer = customerRepository.findByLastname("GILBERT");
		Assertions.assertEquals("NBE", updatedCustomer.getCompany());
	}
	
	@Test
	void testDelete() {
		Optional<Customer> customer = customerRepository.findById(2);
		if(customer.isEmpty())
			fail();
		
		customerRepository.delete(customer.get());
		
		Optional<Customer> deletedCustomer = customerRepository.findById(2);
		Assertions.assertTrue(deletedCustomer.isEmpty(), "Deleted customer must be null!");
	}
}
