package crm.spring.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

import crm.spring.rest.api.v1.dto.UserDto;
import crm.spring.rest.model.User;

@Component
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
	
	UserDto mapUserToUserDto(User user);
	
	User mapUserDtoToUser(UserDto userDto);

}
