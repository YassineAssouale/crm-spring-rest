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
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import crm.spring.rest.api.v1.dto.OrderDto;
import crm.spring.rest.config.AppConfig;
import crm.spring.rest.config.WebConfig;
import crm.spring.rest.exception.UnknownResourceException;
import crm.spring.rest.mapper.OrderMapper;
import crm.spring.rest.model.Order;
import crm.spring.rest.service.OrderService;

@ExtendWith(SpringExtension.class) // Test based Junit Jupiter
@WebAppConfiguration
@ContextConfiguration(classes = {WebConfig.class, AppConfig.class}, loader = AnnotationConfigWebContextLoader.class)
@ActiveProfiles(profiles = "test")
@TestMethodOrder(OrderAnnotation.class)
public class OrderApiTest {
	// Create a stub class (duplicate class) of OrderService for test
	private OrderService orderServiceMock = Mockito.mock(OrderService.class);
	
	private OrderMapper orderMapperMock = Mockito.mock(OrderMapper.class);
	
	@InjectMocks
	private OrderApi orderApi;
	
	private MockMvc mockMvc;
	
	private Order order;
	
	private List<Order> orders;
	
	private OrderDto orderDto;
	
	@BeforeEach
	public void initMockMvc() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(orderApi).build();
	}
	
	@BeforeEach
	void setup() {
		order = new Order();
		order.setId(1);
		order.setLabel("En cours");
		order.setTva(20.0);
		
		orderDto = new OrderDto();
		orderDto.setId(1);
		orderDto.setLabel("En cours");
		orderDto.setTva(20.0);
		
		orders = new ArrayList<>();
		orders.add(order);
	}
	
	@Test
	@org.junit.jupiter.api.Order(1)
	void testGetByIdApiOk()throws Exception{
		Mockito.when(orderServiceMock.getById(1)).thenReturn(order);
		Mockito.when(orderMapperMock.mapOrderToOrderDto(order)).thenReturn(orderDto);
		
		this.mockMvc.perform(get("/v1/orders/1")).andDo(print()).andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(orderDto.getId()))
			.andExpect(jsonPath("$.label").value(orderDto.getLabel()))
			.andExpect(jsonPath("$.tva").value(orderDto.getTva()));
		
		Mockito.verify(orderServiceMock).getById(1);
		Mockito.verify(orderMapperMock).mapOrderToOrderDto(order);	
	}
	
	@Test
	@org.junit.jupiter.api.Order(2)
	void testGetByIdApiKO() throws Exception {
		Mockito.when(orderServiceMock.getById(1)).thenThrow(UnknownResourceException.class);
		this.mockMvc.perform(get("/v1/orders/1")).andDo(print()).andExpect(status().isNotFound());
		Mockito.verify(orderServiceMock).getById(1);
	}
	
	@Test
	@org.junit.jupiter.api.Order(3)
	void testGetAllApiOk() throws Exception{
		Mockito.when(orderServiceMock.getAllOrders()).thenReturn(orders);
		this.mockMvc.perform(get("/v1/orders")).andDo(print()).andExpect(status().isOk());
		Mockito.verify(orderServiceMock).getAllOrders();
	}
	
	@Test
	@org.junit.jupiter.api.Order(4)
	void testCreateApiOk()throws Exception{
		Mockito.when(orderMapperMock.mapOrderToOrderDto(orderServiceMock.createOrder(orderMapperMock.mapOrderDtoToOrder(orderDto))))
		.thenReturn(orderDto);
		this.mockMvc.perform(post("/v1/orders").content(asJsonString(orderDto))
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON))
		.andDo(print()).andExpect(status().isCreated());
		
		orders = new ArrayList<>();
		orders.add(order);
	}
	
	@Test
	@org.junit.jupiter.api.Order(5)
	void testUpdateApiOK()throws Exception{
		Mockito.when(orderServiceMock.updateOrder(orderMapperMock.mapOrderDtoToOrder(orderDto))).thenReturn(order);
		this.mockMvc.perform(put("/v1/orders/1").content(asJsonString(orderDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print()).andExpect(status().isNoContent());
	}
	
	@Test
	@org.junit.jupiter.api.Order(6)
	void testUpdateApiKO()throws Exception{
		Mockito.when(orderServiceMock.updateOrder(orderMapperMock.mapOrderDtoToOrder(orderDto))).thenThrow(UnknownResourceException.class);
		this.mockMvc.perform(put("/v1/orders/1").content(asJsonString(orderDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print()).andExpect(status().isNotFound());
	}
	
	@Test
	@org.junit.jupiter.api.Order(7)
	void testDeleteApiOK()throws Exception{
		Mockito.doNothing().when(orderServiceMock).deleteOrder(1);
		this.mockMvc.perform(delete("/v1/orders/1")).andDo(print()).andExpect(status().isNoContent());
		Mockito.verify(orderServiceMock).deleteOrder(1);
	}
	
	@Test
	@org.junit.jupiter.api.Order(7)
	void testDeleteApiKO()throws Exception{
		Mockito.doThrow(UnknownResourceException.class).when(orderServiceMock).deleteOrder(1);
		this.mockMvc.perform(delete("/v1/orders/1")).andDo(print()).andExpect(status().isNotFound());
		Mockito.verify(orderServiceMock).deleteOrder(1);
	}
	
	public static String asJsonString(final Object obj) {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final String jsonContent = mapper.writeValueAsString(obj);
			return jsonContent;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
