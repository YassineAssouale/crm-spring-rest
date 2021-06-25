package spring.rest.repository;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
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
import crm.spring.rest.model.User;
import crm.spring.rest.repository.UserRepository;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { AppConfig.class }, loader = AnnotationConfigContextLoader.class)
@Transactional
@DirtiesContext
@TestMethodOrder(OrderAnnotation.class)
@ActiveProfiles(profiles = "test")
class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@Test
	@Order(1)
	void testFindByUsernameAndPassword() {
		User user = userRepository.findByUsernameAndPassword("mgilbert", "1234");
		Assertions.assertNotNull(user, "No user found for username and password");
	}

	@Test
	@Order(2)
	void testFindById() {
		Optional<User> user = userRepository.findById(1);
		Assertions.assertNotNull(user.get(), "No user found for id 1");
	}

	@Test
	@Order(3)
	void testFindAll() {
		List<User> users = userRepository.findAll();
		Assertions.assertEquals(1, users.size(), "Wrong number of users");
	}
	
	@Test
	@Order(4)
	void testCreate() {
		User newUser = new User();
		newUser.setUsername("mtest");
		newUser.setPassword("mtest");
		newUser.setMail("mtest@test.fr");

		List<User> users = userRepository.findAll();
		int numberOfUsersBeforeCreation = users.size();

		userRepository.save(newUser);

		List<User> usersAfterCreation = userRepository.findAll();
		int numberOfUsersAfterCreation = usersAfterCreation.size();
		Assertions.assertEquals(numberOfUsersBeforeCreation + 1, numberOfUsersAfterCreation);
	}

	@Test
	@Order(5)
	void testUpdate() {

		User user = userRepository.findByMail("marc.gilbert@wijin.tech");
		user.setMail("nouveauMail@test.fr");

		userRepository.save(user);

		Optional<User> updatedUser = userRepository.findById(1);
		Assertions.assertEquals("nouveauMail@test.fr", updatedUser.get().getMail());
	}

	@Test
	@Order(6)
	void testDelete() {
		Optional<User> user = userRepository.findById(1);
		if(user.isEmpty()) {
			fail();
		}
		
		userRepository.delete(user.get());
		
		Optional<User> deletedUser = userRepository.findById(1);
		Assertions.assertTrue(deletedUser.isEmpty(), "Deleted order must be null");
	}

}
