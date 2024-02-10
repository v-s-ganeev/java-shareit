package ru.practicum.shareit.itemTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ItemServiceTest {
    @Autowired
    private ItemService service;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private CommentRepository commentRepository;

    ItemDto itemDto = ItemDto.builder().id(1).name("item").description("itemDescription").available(true).build();
    User owner = User.builder().id(1).name("owner").email("owner@owner.ru").build();
    Item item = Item.builder().id(1).name("item").description("itemDescription").owner(owner).available(true).build();
    User user = User.builder().id(1).name("user").email("user@user.ru").build();
    Comment comment = Comment.builder().id(1).item(item).author(user).text("good item").created(LocalDateTime.now().plusDays(1)).build();
    CommentDto commentDto = CommentDto.builder().id(1).text("good item").authorName(user.getName()).created(LocalDateTime.now().plusDays(1)).build();
    PageRequest pageRequest = PageRequest.of(0, 10);

    @Test
    void addItem() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto itemDtoOutput = service.addItem(itemDto, 1);

        assertNotNull(itemDtoOutput);
        assertEquals(itemDtoOutput.getName(), itemDto.getName());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void addComment() {
        when(bookingRepository.findFirstByItemIdAndBooker_IdAndStatusNotLikeAndEndIsBeforeOrderByStartDesc(anyInt(), anyInt(), any(BookingStatus.class), any(LocalDateTime.class))).thenReturn(new Booking());
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto commentDtoOutput = service.addComment(commentDto, 2, 1);

        assertNotNull(commentDtoOutput);
        assertEquals(commentDtoOutput.getText(), commentDto.getText());
        verify(bookingRepository, times(1)).findFirstByItemIdAndBooker_IdAndStatusNotLikeAndEndIsBeforeOrderByStartDesc(anyInt(), anyInt(), any(BookingStatus.class), any(LocalDateTime.class));
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void editItemByOwner() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto itemDtoOutput = service.editItem(itemDto, 1, 1);

        assertNotNull(itemDtoOutput);
        assertEquals(itemDtoOutput.getName(), itemDto.getName());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void editItemByNotOwner() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> service.editItem(itemDto, 1, 9));
    }

    @Test
    void getItem() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemIdAndStatusNotLikeAndStartIsBeforeOrStartEqualsOrderByStartDesc(anyInt(), any(BookingStatus.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(null);
        when(bookingRepository.findFirstByItemIdAndStatusNotLikeAndStartIsAfterOrderByStart(anyInt(), any(BookingStatus.class), any(LocalDateTime.class))).thenReturn(null);
        when(commentRepository.findAllByItem_idOrderByCreatedDesc(anyInt())).thenReturn(List.of(comment));

        ItemDto itemDtoOutput = service.getItem(1, 1);

        assertNotNull(itemDtoOutput);
        assertEquals(itemDtoOutput.getName(), item.getName());
    }

    @Test
    void getUserItems() {
        when(bookingRepository.findFirstByItemIdAndStatusNotLikeAndStartIsBeforeOrStartEqualsOrderByStartDesc(anyInt(), any(BookingStatus.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(null);
        when(bookingRepository.findFirstByItemIdAndStatusNotLikeAndStartIsAfterOrderByStart(anyInt(), any(BookingStatus.class), any(LocalDateTime.class))).thenReturn(null);
        when(commentRepository.findAllByItem_idOrderByCreatedDesc(anyInt())).thenReturn(List.of(comment));
        when(itemRepository.getItemsByOwnerId(anyInt(), any(PageRequest.class))).thenReturn(List.of(item));

        List<ItemDto> items = service.getUserItems(1, pageRequest);

        assertNotNull(items);
        assertEquals(items.size(), 1);
    }

    @Test
    void getNeedItems() {
        when(itemRepository.searchByText(anyString(), any(PageRequest.class))).thenReturn(List.of(item));

        List<ItemDto> items = service.getNeedItems("need item", pageRequest);

        assertNotNull(items);
        assertEquals(items.size(), 1);
    }

}
