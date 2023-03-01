package ru.practicum.ewmservice.users.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewmservice.exception.DataExistException;
import ru.practicum.ewmservice.exception.ObjectNotFoundException;
import ru.practicum.ewmservice.users.dto.UserDto;
import ru.practicum.ewmservice.users.mapper.UserMapper;
import ru.practicum.ewmservice.users.model.User;
import ru.practicum.ewmservice.users.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        Page<User> users;
        if (ids != null) {
            users = userRepository.findByIdIn(ids, PageRequest.of(from / size, size));
        } else {
            users = userRepository.findAll(PageRequest.of(from / size, size));
        }
        List<UserDto> usersDto = users.stream()
                .map(userMapper::convertToDto)
                .collect(Collectors.toList());
        log.info("Users got from repository, users={}", usersDto);
        return usersDto;
    }

    @Override
    public UserDto addNewUser(UserDto userDto) {
        User user = userMapper.convertFromDto(userDto);
        try {
            User userSaved = userRepository.save(user);
            log.info("User saved, userSaved={}", userSaved);
            return userMapper.convertToDto(userSaved);
        } catch (RuntimeException e) {
            throw new DataExistException(String.format("User with email=%s exists.", user.getEmail()));
        }
    }

    @Override
    public void deleteUser(long userId) {
        getUserById(userId);
        userRepository.deleteById(userId);
        log.info("User deleted, userId={}", userId);
    }

    @Override
    public User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("User with id=%d was not found", userId)));
    }
}
