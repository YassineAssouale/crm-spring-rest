package crm.spring.rest.api.v1;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import crm.spring.rest.api.v1.dto.UserDto;
import crm.spring.rest.exception.UnknownResourceException;
import crm.spring.rest.mapper.UserMapper;
import crm.spring.rest.service.UserService;

@RestController
@RequestMapping("/v1/users")
@CrossOrigin(value ={"*"}, allowedHeaders = {"*"})
public class UserApi {
	
	Logger log = LoggerFactory.getLogger(UserApi.class);
	
	@Autowired
	UserService userService;
	
	@Autowired
	UserMapper userMapper;
	
	@GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
	@Transactional
	public ResponseEntity<List<UserDto>> getAll(){
		return ResponseEntity.ok(userService.getAllUsersSortedByUsernameAscending()
				.stream().map(userMapper::mapUserToUserDto)
				.collect(Collectors.toList()));
	}
	
	@GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
	@Transactional
	public ResponseEntity<UserDto> getById(@PathVariable final Integer id){
		try {
			return ResponseEntity.ok(userMapper.mapUserToUserDto(userService.getUserById(id)));
		} catch (UnknownResourceException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User Not Found!");
		}
	}
	
	@PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE }, 
			produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto){
			log.debug("Attempting to create user with username {}", userDto.getUsername());
			UserDto newUser = userMapper.mapUserToUserDto(userService.createUser(userMapper.mapUserDtoToUser(userDto)));
			return ResponseEntity.created(URI.create("/v1/users/" + newUser.getId())).body(newUser);
	}
	
	@DeleteMapping(path = "{id}",consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
	public ResponseEntity<Void> deleteUser(@PathVariable final Integer id){
		try {
			log.debug("Preparing to Delete user with id {}",id);
			userService.deleteUser(id);
			return ResponseEntity.noContent().build();
		} catch (UnknownResourceException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User Not Found!");
		}
	}
	
	@PutMapping(path = "{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE }, 
			produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<Void> updateUser(@PathVariable final Integer id, @RequestBody UserDto userDto){
		try {
			log.debug("Updating user {}", id);
			userDto.setId(id);
			userService.updateUser(userMapper.mapUserDtoToUser(userDto));
			return ResponseEntity.noContent().build();
		} catch (UnknownResourceException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User Not Found!");
		}
	}
	
	@PatchMapping(path="{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE }, 
			produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<Void> patchUserMail(@PathVariable final Integer id, @RequestParam String mail){
		try {
			log.debug("Updating user {} mail", id);
			userService.patchUserMail(id, mail);
			log.debug("Successfully patched user {}", id);
			return ResponseEntity.noContent().build();
		} catch (UnknownResourceException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User Not Found!");
		}
	}
}
