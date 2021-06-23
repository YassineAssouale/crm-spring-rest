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

import javax.print.attribute.standard.Media;

import org.h2.server.web.WebApp;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import crm.spring.rest.api.v1.dto.UserDto;
import crm.spring.rest.config.AppConfig;
import crm.spring.rest.exception.UnknownResourceException;
import crm.spring.rest.mapper.UserMapper;
import crm.spring.rest.model.User;
import crm.spring.rest.service.UserService;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {WebApp.class, AppConfig.class}, loader = AnnotationConfigWebContextLoader.class)
@ActiveProfiles(profiles = "test")
@TestMethodOrder(OrderAnnotation.class)
public class UserApiTest {
	
	private UserService userServiceMock = Mockito.mock(UserService.class);
	
	private UserMapper userMapperMock = Mockito.mock(UserMapper.class);
	
	@InjectMocks
	private UserApi userApi;
	
	private MockMvc mockMvc;
	
	private User user;
	
	private List<User> users;
	
	private UserDto userDto;
	
	@BeforeEach
	public void initMockMvc() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(userApi).build();	
	}
	
	@BeforeEach
	void setup() {
		user = new User();
		user.setId(1);
		user.setUsername("Yassine");
		user.setPassword("0123456");
		user.setMail("ya@gmail.com");
		
		userDto = new UserDto();
		userDto.setId(1);
		userDto.setUsername("Yassine");
		userDto.setPassword("0123456");
		userDto.setMail("ya@gmail.com");
		
		users = new ArrayList<>();
		users.add(user);	
	}
	
	@Test
	@Order(1)
	void testGetByIdApiOK()throws Exception{
		
		Mockito.when(userServiceMock.getUserById(1)).thenReturn(user);
		
		Mockito.when(userMapperMock.mapUserToUserDto(user)).thenReturn(userDto);
		
		this.mockMvc.perform(get("/v1/users/1")).andDo(print()).andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(userDto.getId()))
			.andExpect(jsonPath("$.username").value(userDto.getUsername()))
			.andExpect(jsonPath("$.password").value(userDto.getPassword()))
			.andExpect(jsonPath("$.mail").value(userDto.getMail()));
		
		Mockito.verify(userServiceMock).getUserById(1);
		Mockito.verify(userMapperMock).mapUserToUserDto(user);
	}
	
	@Test
	@Order(2)
	void testGetByIdApiKO()throws Exception{
		
		Mockito.when(userServiceMock.getUserById(1)).thenThrow(UnknownResourceException.class);
		
		this.mockMvc.perform(get("/v1/users/1")).andDo(print()).andExpect(status().isNotFound());
		
		Mockito.verify(userServiceMock).getUserById(1);
	}
	
	@Test
	@Order(3)
	void testGetAllApiOK()throws Exception{
		
		Mockito.when(userServiceMock.getAllUsersSortedByUsernameAscending()).thenReturn(users);
		
		this.mockMvc.perform(get("/v1/users")).andDo(print()).andExpect(status().isOk());
		
		Mockito.verify(userServiceMock).getAllUsersSortedByUsernameAscending();
		
	}
	
	@Test
	@Order(4)
	void testCreateApiOK()throws Exception{
		
		Mockito.when(userMapperMock.mapUserToUserDto(
				userServiceMock.createUser(userMapperMock.mapUserDtoToUser(userDto)))).thenReturn(userDto);
		
		this.mockMvc.perform(post("/v1/users").content(asJsonString(userDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isCreated());
	}

	@Test
	@Order(5)
	void testUpdateApiOk()throws Exception{
		
		Mockito.when(userServiceMock.updateUser(userMapperMock.mapUserDtoToUser(userDto))).thenReturn(user);
		
		this.mockMvc.perform(put("/v1/update/1").content(asJsonString(userDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print()).andExpect(status().isNoContent());
	}
	
	@Test
	@Order(6)
	void testUpdateApikO()throws Exception{
		
		Mockito.when(userServiceMock.updateUser(userMapperMock.mapUserDtoToUser(userDto))).thenThrow(UnknownResourceException.class);
		
		this.mockMvc.perform(put("/v1/users/1").content(asJsonString(userDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print()).andExpect(status().isNotFound());
	}
	
	@Test
	@Order(7)
	void testDeleteApikO()throws Exception{
		
		Mockito.doThrow(UnknownResourceException.class).when(userServiceMock).deleteUser(1);
		
		this.mockMvc.perform(delete("/v1/users/1"))
		.andDo(print()).andExpect(status().isNotFound());
		
		Mockito.verify(userServiceMock).deleteUser(1);
	}
	
	@Test
	@Order(7)
	void testDeleteApiOk()throws Exception{
		
		Mockito.doNothing().when(userServiceMock).deleteUser(1);
		
		this.mockMvc.perform(delete("/v1/users/1"))
		.andDo(print()).andExpect(status().isNoContent());
		
		Mockito.verify(userServiceMock).deleteUser(1);
	}
	
	public static String asJsonString(final Object obj) throws JsonProcessingException {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final String jsonContent = mapper.writeValueAsString(obj);
			return jsonContent;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	

}
