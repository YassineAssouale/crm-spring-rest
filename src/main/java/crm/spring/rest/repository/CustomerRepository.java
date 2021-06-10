package crm.spring.rest.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import crm.spring.rest.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer>{
	
	/**
	 * Get customer by lastname
	 * @param lastname
	 * @return the customer
	 */
	Customer fingByLastname(String lastname);
	/**
	 * Get customer by status
	 * @param active
	 * @return active customers 
	 */
	List<Customer> findByActive(@Param("active") Boolean active);
	/**
	 * @return a list of customer with mobil
	 */
	@Query("SELECT c FROM Customer c WHERE c.mobile IS NOT NULL")
	List<Customer> findCustomerWithMobil();
	/**
	 * 
	 * @param active
	 * @param pagingCustomer
	 * @return a page of active customers 
	 */
	Page<Customer> findByActive(@Param("active") Boolean active, Pageable paginCustomer);
}
