package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * TODO Sprint add-item-requests.
 */
@Getter
@Setter
public class ItemRequestDto {
    private UUID id;
    private String description;
    private User requestor;
    private LocalDateTime created;
}
