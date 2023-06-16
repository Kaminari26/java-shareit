package ru.practicum.shareit.user;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class UserService implements IUserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public UserDto add(UserDto userDto) {
        User user = UserMapper.toDtoUser(userDto);
        HashMap<Long, User> users = userStorage.getUsers();
        for (User str : users.values()) {
            if (str.getEmail().equals(user.getEmail())) {
                throw new ValidationException("Почта уже используется");
            }
        }
        return UserMapper.toUserDto(userStorage.addUser(user));
    }

    @Override
    public UserDto update(UserDto userDto, Long userId) {
        HashMap<Long, User> users = userStorage.getUsers();
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
                    if (!str.getId().equals(userId)) {
                        throw new ValidationException("Почта уже используется");
                    }
                    break;
                }
            }
            upUser.setEmail(user.getEmail());
        }
        return UserMapper.toUserDto(userStorage.updateUser(userId));
    }

    @Override
    @SneakyThrows
    public UserDto get(Long userId) {
        HashMap<Long, User> users = userStorage.getUsers();
        if (users.get(userId) == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        return UserMapper.toUserDto(userStorage.getUser(userId));
    }

    @Override
    public void deleted(Long userId) {
        userStorage.deleteUser(userId);
    }

    @Override
    public List<UserDto> getUsers() {
        HashMap<Long, User> users = userStorage.getUsers();
        List<UserDto> userDtos = new ArrayList<>();
        for (User user : users.values()) {
            userDtos.add(UserMapper.toUserDto(user));
        }
        return userDtos;
    }

}
