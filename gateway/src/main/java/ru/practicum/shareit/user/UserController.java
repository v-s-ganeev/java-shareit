package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.validation.ValidationMarkers;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable Integer userId) {
        return userClient.getUser(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return userClient.getAllUsers();
    }

    @PostMapping
    public ResponseEntity<Object> addUser(@Validated(ValidationMarkers.Create.class) @RequestBody UserRequestDto requestDto) {
        return userClient.addUser(requestDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> editUser(@PathVariable Integer userId, @Validated(ValidationMarkers.Update.class) @RequestBody UserRequestDto requestDto) {
        return userClient.editUser(userId, requestDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Integer userId) {
        return userClient.deleteUser(userId);
    }
}
