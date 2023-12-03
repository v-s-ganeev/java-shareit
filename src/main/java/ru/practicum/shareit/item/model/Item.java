package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.ItemRequest;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Item {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private Integer ownerId;
    private ItemRequest request;

}
