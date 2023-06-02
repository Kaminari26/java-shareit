package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface IUserService {
    UserDto add(UserDto userDto);

    UserDto update(UserDto userDto, Long userId);

    UserDto get(Long userId);

    void deleted(Long userId);

    List<UserDto> getUsers();
}
