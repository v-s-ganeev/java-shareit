package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImplUserService implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto addUser(UserDto userDto) {
        User user = userRepository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto editUser(UserDto userDto, Integer userId) {
        User userInDb = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) userInDb.setEmail(userDto.getEmail());
        if (userDto.getName() != null && !userDto.getName().isBlank()) userInDb.setName(userDto.getName());
        return UserMapper.toUserDto(userRepository.save(userInDb));
    }

    @Override
    @Transactional
    public void deleteUser(int userId) {
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional
    public UserDto getUser(int userId) {
        return UserMapper.toUserDto(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден")));
    }

    @Override
    @Transactional
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
