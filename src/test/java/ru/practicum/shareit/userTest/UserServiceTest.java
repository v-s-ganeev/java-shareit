package ru.practicum.shareit.userTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService service;

    @MockBean
    private UserRepository userRepository;

    private UserDto userDto = UserDto.builder().name("user").email("user@user.ru").build();
    private User user = User.builder().id(1).name("user").email("user@user.ru").build();

    @Test
    void addUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto userDtoOutput = service.addUser(userDto);

        assertNotNull(userDtoOutput);
        assertEquals(userDtoOutput.getName(), user.getName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void editUser() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto userDtoOutput = service.editUser(userDto, anyInt());

        assertNotNull(userDtoOutput);
        assertEquals(userDtoOutput.getName(), user.getName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void deleteUser() {
        doThrow(new NotFoundException("User not found")).when(userRepository).deleteById(anyInt());

        assertThrows(NotFoundException.class, () -> service.deleteUser(anyInt()));
    }

    @Test
    void getUser() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        UserDto userDtoOutput = service.getUser(1);

        assertNotNull(userDtoOutput);
        assertEquals(userDtoOutput.getName(), user.getName());
        verify(userRepository, times(1)).findById(anyInt());
    }

    @Test
    void getAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> users = service.getAllUsers();

        assertNotNull(users);
        assertEquals(users.size(), 1);
        verify(userRepository, times(1)).findAll();
    }
}
