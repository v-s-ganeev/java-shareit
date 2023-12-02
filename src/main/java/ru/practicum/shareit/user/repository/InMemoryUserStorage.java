package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class InMemoryUserStorage implements UserStorage {

    private int id = 0;
    private Map<Integer, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        if (user.getId() != null) {
            throw new ValidationException("Поле id не пустое");
        }
        if (users.values().stream().anyMatch(checkedUser -> checkedUser.getEmail().equals(user.getEmail()))) {
            throw new DuplicateEmailException("Пользователь с таким email уже существует");
        }
        user.setId(++id);
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь: {}", user);
        return user;
    }

    @Override
    public User editUser(User user) {
        User userInDb = users.get(user.getId());
        if (userInDb == null)
            throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
        if (user.getEmail() != null && users.values().stream().anyMatch(checkedUser -> checkedUser.getEmail().equals(user.getEmail()) && checkedUser.getId() != user.getId())) {
            throw new DuplicateEmailException("Пользователь с таким email уже существует");
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) userInDb.setEmail(user.getEmail());
        if (user.getName() != null && !user.getName().isBlank()) userInDb.setName(user.getName());
        log.info("Внесены изменения в пользователя: {}", userInDb);
        return userInDb;
    }

    @Override
    public void deleteUser(Integer userId) {
        if (users.get(userId) == null) throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        users.remove(userId);
    }

    @Override
    public User getUser(Integer userId) {
        if (users.get(userId) == null) throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        return users.get(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}
