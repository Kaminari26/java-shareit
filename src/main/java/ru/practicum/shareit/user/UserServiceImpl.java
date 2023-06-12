package ru.practicum.shareit.user;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;


@Service
@Primary
public class UserServiceImpl implements IUserService {
    private final UserRepository repository;

    @Autowired
    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDto add(UserDto userDto) {
        User user = repository.save(UserMapper.toDtoUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(UserDto userDto, Long userId) {
        User userUpdated = UserMapper.toDtoUser(userDto);
        User user = repository.getReferenceById(userId);
        if (userUpdated.getName() != null) {
            user.setName(userUpdated.getName());
        }
        if (userUpdated.getEmail() != null) {
            user.setEmail(userUpdated.getEmail());
        }
        repository.save(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto get(Long userId) {
        User user = repository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        return UserMapper.toUserDto(user);

    }

    @Override
    public void deleted(Long userId) {
        repository.deleteById(userId);
    }

    @Override
    public List<UserDto> getUsers() {
        return UserMapper.mapToUserDto(repository.findAll());
    }
}
