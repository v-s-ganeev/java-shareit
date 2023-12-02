package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto addUser(UserDto userDto);

    UserDto editUser(UserDto userDto, Integer userId);

    void deleteUser(int userId);

    UserDto getUser(int userId);

    List<UserDto> getAllUsers();
}
