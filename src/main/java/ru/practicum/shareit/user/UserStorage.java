package ru.practicum.shareit.user;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class UserStorage {
    HashMap<Long, User> users = new HashMap<>();
    private Long userId = 0L;

    public UserDto addUser(UserDto userDto) {
        User user = UserMapper.toDtoUser(userDto);
        for (User str : users.values()) {
            if (str.getEmail().equals(user.getEmail())) {
                throw new ValidationException("Почта уже используется");
            }
        }
        userId++;
        user.setId(userId);
        users.put(user.getId(), user);
        return UserMapper.toUserDto(users.get(userId));
    }

    public UserDto updateUser(UserDto userDto, Long userId) {
        User user = UserMapper.toDtoUser(userDto);
        if (!users.containsKey(userId)) {
            throw new NullPointerException("Пользователь не найден");
        }
        User upUser = users.get(userId);
        if (user.getName() != null) {
            upUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            for (User str : users.values()) {
                if (str.getEmail().equals(user.getEmail())) {
                    if (str.getId().equals(userId)) {
                        throw new ValidationException("Почта уже используется");
                    }
                    break;
                }
            }
            upUser.setEmail(user.getEmail());
        }
        return UserMapper.toUserDto(users.get(userId));
    }

    @SneakyThrows
    public UserDto getUser(Long userId) {
        if (users.get(userId) == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        return UserMapper.toUserDto(users.get(userId));
    }

    public void deleteUser(Long userId) {
        users.remove(userId);
    }

    public List<UserDto> getUsers() {
        List<UserDto> userDtos = new ArrayList<>();
        for (User user : users.values()) {
            userDtos.add(UserMapper.toUserDto(user));
        }

        return userDtos;
    }
}
