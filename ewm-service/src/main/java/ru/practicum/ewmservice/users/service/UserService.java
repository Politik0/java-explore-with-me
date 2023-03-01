package ru.practicum.ewmservice.users.service;

import ru.practicum.ewmservice.users.dto.UserDto;
import ru.practicum.ewmservice.users.model.User;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(List<Long> ids, int from, int size);

    UserDto addNewUser(UserDto userDto);

    void deleteUser(long userId);

    User getUserById(long userId);
}
