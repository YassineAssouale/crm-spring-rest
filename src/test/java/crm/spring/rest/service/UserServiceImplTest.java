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
import crm.spring.rest.model.User;
import crm.spring.rest.repository.UserRepository;
import crm.spring.rest.service.impl.UserServiceImpl;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@WebAppConfiguration
@ContextConfiguration(classes = { WebConfig.class, AppConfig.class }, loader = AnnotationConfigWebContextLoader.class)
@ActiveProfiles(profiles = "test")
class UserServiceImplTest {
	
	@Mock
	private UserRepository userRepositoryMock;
	
	@InjectMocks
	private UserServiceImpl userService;
	
	private User user;
	
	private List<User> users;
	
	@BeforeEach
	void setup() {
		user = new User();
		user.setId(1);
		user.setUsername("usernameTest");
		user.setPassword("passwordTest");
		
		users = new ArrayList<>();		
		users.add(user);
	}

	@Test
	void testGetAll() {
		Mockito.when(userRepositoryMock.findAll(Sort.by("username").ascending())).thenReturn(users);
		
		List<User> users = userService.getAllUsersSortedByUsernameAscending();
		assertEquals(1, users.size());
		
		Mockito.verify(userRepositoryMock).findAll(Sort.by("username").ascending());
	}
	
	@Test
	void testGetById() {
		Mockito.when(userRepositoryMock.findById(1)).thenReturn(Optional.of(user));
		User myUser = userService.getUserById(1);
		assertEquals(myUser, user);
		Mockito.verify(userRepositoryMock).findById(1);
	}
	
	@Test
	void testCreate() {
		Mockito.when(userRepositoryMock.save(user)).thenReturn(user);
		User createdUser = userService.createUser(user);
		assertNotNull(createdUser);
		Mockito.verify(userRepositoryMock).save(user);
	}
	
	@Test
	void testUpdate() {
		user.setUsername("usernameUpdateTest");
		Mockito.when(userRepositoryMock.findById(1)).thenReturn(Optional.of(user));
		Mockito.when(userRepositoryMock.save(user)).thenReturn(user);
		userService.updateUser(user);
		
		User modifiedUser = userService.getUserById(1);
		
		assertEquals("usernameUpdateTest", modifiedUser.getUsername());
		Mockito.verify(userRepositoryMock, VerificationModeFactory.times(2)).findById(1);
		Mockito.verify(userRepositoryMock).save(user);
	}
	
	@Test
	void testDelete() {
		Mockito.when(userRepositoryMock.findById(1)).thenReturn(Optional.of(user));
		Mockito.doNothing().when(userRepositoryMock).delete(user);
		userService.deleteUser(1);
		
		Mockito.verify(userRepositoryMock).findById(1);
		Mockito.verify(userRepositoryMock).delete(user);
	}
	
}
