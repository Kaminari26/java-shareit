package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@Slf4j
@RequestMapping(path = "/users")
public class UserController {
    IUserService userService;

    @Autowired
    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Пришел запрос Post /users");
        return userService.add(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable Long userId) {
        log.info("Обновление пользователя с id = " + userId);
        return userService.update(userDto, userId);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        log.info("Запрос пользователя Get " + userId);
        return userService.get(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("Удаление пользователя " + userId);
        userService.deleted(userId);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        log.info("Список пользователей");
        return userService.getUsers();

    }
}
