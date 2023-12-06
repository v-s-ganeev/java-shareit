package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserStorage {

    User addUser(User user);

    User editUser(User user);

    void deleteUser(Integer userId);

    User getUser(Integer userId);

    List<User> getAllUsers();
}
