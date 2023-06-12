package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

@Repository
@EnableJpaRepositories("ru.practicum.shareit.user")
public interface UserRepository extends JpaRepository<User, Long> {
}
