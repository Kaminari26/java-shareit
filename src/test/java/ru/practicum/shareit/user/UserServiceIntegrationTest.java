package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegrationTest {
    private final IUserService userService;

    @Test
    void shouldNotAddUserIfEmailDuplicate() {
        UserDto userRequest1 = new UserDto(null,"test","uniqueemail@mail.ru");
        UserDto userRequest2 = new UserDto(null,"test","uniqueemail@mail.ru");


        UserDto user1 = userService.add(userRequest1);

        assertThrows(DataIntegrityViolationException.class, () -> userService.add(userRequest2));

        UserDto foundUser1 = userService.get(1L);
        assertEquals("uniqueemail@mail.ru", user1.getEmail());
        assertNotNull(foundUser1);
        assertThrows(UserNotFoundException.class, () -> userService.get(9001L));
    }
}
