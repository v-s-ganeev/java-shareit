package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoOutputToOwner;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDtoForOwner {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDtoOutputToOwner lastBooking;
    private BookingDtoOutputToOwner nextBooking;
    private Integer requestId;
}
