package ru.practicum.ewmservice.users.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.ewmservice.users.dto.UserDto;
import ru.practicum.ewmservice.users.model.User;

@Component
public class UserMapper {
    private final ModelMapper modelMapper;

    public UserMapper() {
        modelMapper = new ModelMapper();
    }

    public UserDto convertToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    public User convertFromDto(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }
}