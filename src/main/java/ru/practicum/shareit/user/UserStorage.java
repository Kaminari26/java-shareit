package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;

@Component
public class UserStorage {
    HashMap<Long, User> users = new HashMap<>();
    private Long userId = 0L;

    public User addUser(User user) {
        userId++;
        user.setId(userId);
        users.put(user.getId(), user);
        return users.get(userId);
    }

    public User updateUser(Long userId) {
        return users.get(userId);
    }


    public User getUser(Long userId) {
        return users.get(userId);
    }

    public void deleteUser(Long userId) {
        users.remove(userId);
    }

    public HashMap<Long, User> getUsers() {
        return users;
    }
}
