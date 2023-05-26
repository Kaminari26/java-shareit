package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

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
        return userStorage.addUser(userDto);
    }

    @Override
    public UserDto update(UserDto userDto, Long userId) {
        return userStorage.updateUser(userDto, userId);
    }

    @Override
    public UserDto get(Long userId) {
        return userStorage.getUser(userId);
    }

    @Override
    public void deleted(Long userId) {
        userStorage.deleteUser(userId);
    }

    @Override
    public List<UserDto> getUsers() {
        return userStorage.getUsers();
    }

}
