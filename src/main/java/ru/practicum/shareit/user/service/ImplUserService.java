package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImplUserService implements UserService {

    @Qualifier("InMemoryUserStorage")
    private final UserStorage userStorage;

    @Override
    public UserDto addUser(UserDto userDto) {
        User user = checkUser(UserMapper.toUser(userDto));
        userStorage.addUser(user);
        return UserMapper.toUserDto(user);
    }

    public UserDto editUser(UserDto userDto, Integer userId) {
        User user = UserMapper.toUser(userDto);
        user.setId(userId);
        return UserMapper.toUserDto(userStorage.editUser(user));
    }

    public void deleteUser(int userId) {
        userStorage.deleteUser(userId);
    }

    public UserDto getUser(int userId) {
        return UserMapper.toUserDto(userStorage.getUser(userId));
    }

    public List<UserDto> getAllUsers() {
        return userStorage.getAllUsers()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    private User checkUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("email не может быть пустым");
        }
        if (!user.getEmail().contains("@")) throw new ValidationException("Некорректный email");
        if (user.getName() == null || user.getName().isBlank()) {
            throw new ValidationException("login не может быть пустым");
        }
        return user;
    }
}
