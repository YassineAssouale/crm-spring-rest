package crm.spring.rest.service;

import java.util.List;

import crm.spring.rest.model.User;

public interface UserService {
	/**
	 * Get a user by id
	 * @param id the id
	 * @return the User
	 */
	User getUserById(Integer id);
	
	/**
	 * Get users
	 * @return users
	 */
	List<User> getAllUsersSortedByUsernameAscending();
	
	/**
	 * Get a user by username
	 * @param username
	 * @return
	 */
	User getUserByUsername(String username);
	
	/**
	 * Create a user
	 * @param user
	 * @return user
	 */
	User createUser(User user);
	
	/**
	 * Update a user
	 * @param user the user to update
	 */
	User updateUser(User user);
	
	/**
	 * 
	 * @param userId
	 * @param mail
	 */
	void patchUserMail(Integer userId, String mail);
	
	/**
	 * Delete a user
	 * @param user id the user to delete
	 */
	void deleteUser(Integer id);
}
