package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    UserRepository repository;


    @Test
    void addNewUserTest() {
        Mockito.when(repository.save(Mockito.any(User.class))).thenReturn(new User(1L, "Vasya", "Pupkin@yandex.ru"));

        UserServiceImpl userService = new UserServiceImpl(repository);
        UserDto userDto = userService.add(new UserDto(null, "Vasya", "Pupkin@yandex.ru"));

        Assertions.assertEquals("Vasya", userDto.getName());
        Assertions.assertEquals("Pupkin@yandex.ru", userDto.getEmail());
        Assertions.assertEquals(1L, userDto.getId());
    }

    @Test
    void updateUserTestOk() {
        Mockito.when(repository.save(Mockito.any(User.class))).thenReturn(new User(1L, "Petya", "1123@yandex.ru"));
        Mockito.when(repository.getReferenceById(Mockito.anyLong())).thenReturn(new User(1L, "Vasya", "Pupkin@yandex.ru"));

        UserServiceImpl userService = new UserServiceImpl(repository);
        UserDto userDto = userService.update(new UserDto(null, "Petya", "1123@yandex.ru"), 1L);

        Assertions.assertEquals("Petya", userDto.getName());
        Assertions.assertEquals("1123@yandex.ru", userDto.getEmail());
        Assertions.assertEquals(1L, userDto.getId());
    }


    @Test
    void getUserOkTest() {
        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(new User(1L, "Vasya", "Pupkin@yandex.ru")));

        UserServiceImpl userService = new UserServiceImpl(repository);
        UserDto userDto = userService.get(1L);

        Assertions.assertEquals("Vasya", userDto.getName());
        Assertions.assertEquals("Pupkin@yandex.ru", userDto.getEmail());
        Assertions.assertEquals(1L, userDto.getId());
    }

    @Test
    void getUserThrowTest() {
        Mockito.when(repository.findById(Mockito.anyLong())).thenThrow(new UserNotFoundException("Пользователь не найден"));
        UserServiceImpl userService = new UserServiceImpl(repository);

        final UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.get(555L));

        Assertions.assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void deleted() {
        UserServiceImpl userService = new UserServiceImpl(repository);
        userService.deleted(2L);
        Mockito.verify(repository, Mockito.times(1)).deleteById(Mockito.anyLong());

    }

    @Test
    void getUsers() {
        List<User> users = new ArrayList<>();
        users.add(new User(1L, "Vasya", "Pupkin@yandex.ru"));
        users.add(new User(2L, "Petya", "Pupkinovich@yandex.ru"));
        Mockito.when(repository.findAll()).thenReturn(users);

        UserServiceImpl userService = new UserServiceImpl(repository);

        List<UserDto> list = userService.getUsers();
        Assertions.assertEquals("[UserDto(id=1, name=Vasya, email=Pupkin@yandex.ru), UserDto(id=2, name=Petya, email=Pupkinovich@yandex.ru)]", list.toString());
    }
}