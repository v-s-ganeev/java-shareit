package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingDtoOutputToOwner;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDto {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDtoOutputToOwner lastBooking;
    private BookingDtoOutputToOwner nextBooking;
    private List<CommentDto> comments;
    private Integer requestId;
}
