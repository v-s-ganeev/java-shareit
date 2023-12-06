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
        if (userDto.getId() != null) {
            throw new ValidationException("Поле id не пустое");
        }
        checkUser(userDto);
        User user = userStorage.addUser(UserMapper.toUser(userDto));
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

    private void checkUser(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new ValidationException("email не может быть пустым");
        }
        if (!userDto.getEmail().contains("@")) throw new ValidationException("Некорректный email");
        if (userDto.getName() == null || userDto.getName().isBlank()) {
            throw new ValidationException("login не может быть пустым");
        }
    }
}
