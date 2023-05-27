package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/users")
public class UserController {
    private final IUserService userService;

    @Autowired
    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Пришел запрос Post /users");
        UserDto userDtoReady = userService.add(userDto);
        log.info("Отправлен ответ " + userDtoReady);
        return userDtoReady;
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable Long userId) {
        log.info("Обновление пользователя с id = " + userId);
        UserDto userDtoReady = userService.update(userDto, userId);
        log.info("Отправлен ответ " + userDtoReady);
        return userDtoReady;
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        log.info("Запрос пользователя Get " + userId);
        UserDto userDtoReady = userService.get(userId);
        log.info("Отправлен ответ " + userDtoReady);
        return userDtoReady;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("Удаление пользователя " + userId);
        userService.deleted(userId);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        log.info("Список пользователей");
        List<UserDto> usersDto = userService.getUsers();
        log.info("Отправлен ответ" + usersDto);
        return usersDto;
    }
}
