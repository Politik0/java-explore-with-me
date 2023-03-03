package ru.practicum.ewmservice.users.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.users.dto.UserDto;
import ru.practicum.ewmservice.users.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/admin/users")
public class AdminUserController {
    private final UserService userService;

    @GetMapping // Получение информации о пользователях
    public List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids, @RequestParam(defaultValue = "0") int from,
                            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting all users, usersIds={}, from={}, size={}", ids.toString(), from, size);
        return userService.getUsers(ids, from, size);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping // Добавление нового пользователя
    public UserDto addNewUser(@RequestBody @Valid UserDto userDto) {
        log.info("Adding new user, userDto={}", userDto.toString());
        return userService.addNewUser(userDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{userId}") // Удаление пользователя
    public void deleteUser(@PathVariable long userId) {
        log.info("Deleting user, userId={}", userId);
        userService.deleteUser(userId);
    }
}
