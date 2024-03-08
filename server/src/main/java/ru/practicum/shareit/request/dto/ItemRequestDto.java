package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemRequestDto {
    private Integer id;
    private String description;
    private Integer requestorId;
    private LocalDateTime created;
    private List<ItemDto> items;
}
