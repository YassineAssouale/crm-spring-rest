package crm.spring.rest.api.v1;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import crm.spring.rest.api.v1.dto.CustomerDto;
import crm.spring.rest.config.AppConfig;
import crm.spring.rest.config.WebConfig;
import crm.spring.rest.exception.UnknownResourceException;
import crm.spring.rest.mapper.CustomerMapper;
import crm.spring.rest.model.Customer;
import crm.spring.rest.service.CustomerService;

@ExtendWith(SpringExtension.class) // Test based Junit Jupiter
@WebAppConfiguration // Enabling Unit Test
@ContextConfiguration(classes= {WebConfig.class,AppConfig.class}, loader = AnnotationConfigWebContextLoader.class)
@ActiveProfiles(profiles="test")
@TestMethodOrder(OrderAnnotation.class)
public class CustomerApiTest {
	// Create a stub class (duplicate class) of CustomerService for test
	private CustomerService customerServiceMock = Mockito.mock(CustomerService.class); 
	
	private CustomerMapper customerMapperMock = Mockito.mock(CustomerMapper.class);
	
	@InjectMocks
	private CustomerApi customerApi;
	
	private MockMvc mockMvc;
	
	private Customer customer;
	
	private List<Customer> customers;
	
	private CustomerDto customerDto;
	
	@BeforeEach
	void initMockMvc() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(customerApi).build();
	}
	
	@BeforeEach
	void setup() {
		customer = new Customer();
		customer.setId(1);
		customer.setLastname("GILBERT");
		customer.setFirstname("Marc");
		
		customerDto = new CustomerDto();
		customerDto.setId(1);
		customerDto.setLastname("GILBERT");
		customerDto.setFirstname("Marc");
		
		customers = new ArrayList<>();
		customers.add(customer);
		
	}
	
	@Test
	@Order(1)
	void testGetByIdApiOK()throws Exception{
		
		Mockito.when(customerServiceMock.getCustomerById(1)).thenReturn(customer);
		
		Mockito.when(customerMapperMock.mapCustomerToCustomerDto(customer)).thenReturn(customerDto);
		
		this.mockMvc.perform(get("/v1/customers/1")).andDo(print()).andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(customerDto.getId()))
			.andExpect(jsonPath("$.lastname").value(customerDto.getLastname()))
			.andExpect(jsonPath("$.firstname").value(customerDto.getFirstname()));
		
		Mockito.verify(customerServiceMock).getCustomerById(1);
		Mockito.verify(customerMapperMock).mapCustomerToCustomerDto(customer);
	}
	
	@Test
	@Order(2)
	void testGetByIdApiKo()throws Exception{
		
		Mockito.when(customerServiceMock.getCustomerById(1)).thenThrow(UnknownResourceException.class);
		
		this.mockMvc.perform(get("/v1/customers/1")).andDo(print()).andExpect(status().isNotFound());
		
		Mockito.verify(customerServiceMock).getCustomerById(1);
	}
	
	@Test
	@Order(3)
	void testGetAllApiOk() throws Exception{
		
		Mockito.when(customerServiceMock.getAllCustomersSortByLastnameAscending()).thenReturn(customers);
		
		this.mockMvc.perform(get("/v1/customers")).andDo(print()).andExpect(status().isOk());
		
		Mockito.verify(customerServiceMock.getAllCustomersSortByLastnameAscending());
	}
	
	@Test
	@Order(4)
	void testCreateApiOk() throws Exception{
		
		Mockito.when(customerMapperMock.mapCustomerToCustomerDto(
				customerServiceMock.createCustomer(customerMapperMock.mapCustomerDtoToCustomer(customerDto))))
		.thenReturn(customerDto);
		
		this.mockMvc.perform(post("/v1/customers").content(asJsonString(customerDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isCreated());
	}
	
	@Test
	@Order(5)
	void testUpdateApiOk() throws Exception{
		
		Mockito.when(customerServiceMock.updateCustomer(customerMapperMock.mapCustomerDtoToCustomer(customerDto)))
		.thenReturn(customer);
		
		this.mockMvc.perform(put("/v1/customers").content(asJsonString(customerDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isNoContent());
	}
	
	@Test
	@Order(6)
	void testUpdateApiKo() throws Exception{
		Mockito.when(customerServiceMock.updateCustomer(customerMapperMock.mapCustomerDtoToCustomer(customerDto)))
		.thenThrow(UnknownResourceException.class);
		
		this.mockMvc.perform(put("/v1/customers").content(asJsonString(customerDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isNotFound());
	}
	
	@Test
	@Order(7)
	void testDeleteApiOk() throws Exception{
		
		Mockito.doNothing().when(customerServiceMock).deleteCustomer(1);
		
		this.mockMvc.perform(delete("/v1/customers/1"))
		.andDo(print()).andExpect(status().isNoContent());
		
		Mockito.verify(customerServiceMock).deleteCustomer(1);	
	}
	
	@Test
	@Order(8)
	void testDeleteApiKo() throws Exception{
		Mockito.doThrow(UnknownResourceException.class).when(customerServiceMock).deleteCustomer(1);
		
		this.mockMvc.perform(delete("/v1/customers/1"))
		.andDo(print()).andExpect(status().isNotFound());
		
		Mockito.verify(customerServiceMock).deleteCustomer(1);
	}

	public static String asJsonString(final Object obj) {
		try {
			// Jackson serialization : ObjectMapper
			final ObjectMapper mapper = new ObjectMapper();
			final String jsonContent = mapper.writeValueAsString(obj);
			return jsonContent;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
