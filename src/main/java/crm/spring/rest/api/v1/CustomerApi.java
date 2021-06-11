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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import crm.spring.rest.api.v1.dto.CustomerDto;
import crm.spring.rest.exception.UnknownResourceException;
import crm.spring.rest.mapper.CustomerMapper;
import crm.spring.rest.service.CustomerService;

@RestController // MVC REST : RESTful Service
@RequestMapping("/v1/customers")
/**
 * Allows a webpage to request additional resources into browser from other domains e.g. fonts, CSS or static images from CDN. 
 * CORS helps in serving web content from multiple domains into browsers who usually have the same-origin security policy.
 * value = {"*"} : means all origins are allowed
 * allowedHeaders = {"*"} : means all headers requested by the client are allowed
 */
@CrossOrigin(value = {"*"}, allowedHeaders = {"*"}) 


public class CustomerApi {
	
	Logger log = LoggerFactory.getLogger(CustomerApi.class);
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private CustomerMapper customerMapper;
	
	@GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE}) // READ (CRUD) / GET (HTTP)
	@Transactional
	/**
	 * stream() : Returns a sequential Stream with this collection as its source, it replaces iterator to browse the collection or list
	 * map() : to map object X into object Y
	 */
	public ResponseEntity<List<CustomerDto>> getAll(){
		return ResponseEntity.ok(customerService.getAllCustomersSortByLastnameAscending().stream()
				.map(customerMapper::mapCustomerToCustomerDto).collect(Collectors.toList()));	
	}
	
	@GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity <CustomerDto> getById(@PathVariable final Integer id) {
		try {
			return ResponseEntity.ok(customerMapper.mapCustomerToCustomerDto(customerService.getCustomerById(id)));
		} catch (UnknownResourceException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Customer Not Found");
		}
	}
	
	@PostMapping(produces= {MediaType.APPLICATION_JSON_VALUE}, consumes= {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
	public ResponseEntity<CustomerDto> createCustomer(@RequestBody final CustomerDto customerDto){
		log.debug("Attempting to create customer with name {}", customerDto.getLastname());
		CustomerDto newCustomer = customerMapper.mapCustomerToCustomerDto(
				customerService.createCustomer(customerMapper.mapCustomerDtoToCustomer(customerDto)));
		return ResponseEntity.created(URI.create("/v1/customers/"+ newCustomer.getId())).body(newCustomer);
		
	}
	
	@DeleteMapping(path= "{id}")
	public ResponseEntity<Void> deleteCustomer(@PathVariable final Integer id){
		try {
			log.debug("Preparing to delete customer with id {}", id);
			customerService.deleteCustomer(id);
			return ResponseEntity.noContent().build();
		} catch (UnknownResourceException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Customer Not Found");
		}
	}
	
	@PutMapping(path="{id}", produces = {MediaType.APPLICATION_JSON_VALUE}, 
			consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
	public ResponseEntity<Void> updateCustomer(@PathVariable final Integer id, @RequestBody CustomerDto customerDto){
		try {
			log.debug("Updating customer {}", id);
			customerDto.setId(id);
			customerService.updateCustomer(customerMapper.mapCustomerDtoToCustomer(customerDto));
			log.debug("Successfully updating customer {}",id);
			return ResponseEntity.noContent().build();
		} catch (UnknownResourceException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer Not Found");
		}
	}
	@PatchMapping(path= "{id}",produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
	public ResponseEntity<Void> patchCustomerStatus(@PathVariable final Integer id, @RequestBody CustomerDto customerDto){
		try {
			log.debug("Patching customer {} status",id);
			customerService.patchCustomerStatus(id, customerDto.getActive());
			log.debug("Successfully patched customer {}",id);
			return ResponseEntity.noContent().build();
		} catch (UnknownResourceException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer Not Found");
		}
	}
	

}
