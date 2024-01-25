package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImplItemService implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto addItem(ItemDto itemDto, Integer userId) {
        if (itemDto.getName() == null || itemDto.getName().isBlank() || itemDto.getDescription() == null || itemDto.getDescription().isBlank() || itemDto.getAvailable() == null) {
            throw new ValidationException("Поля Name, Description и Available обязательны к заполнению");
        }
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public CommentDto addComment(CommentDto commentDto, Integer userId, Integer itemId) {
        if (bookingRepository.findFirstByItemIdAndBooker_IdAndStatusNotLikeAndEndIsBeforeOrderByStartDesc(itemId, userId, BookingStatus.REJECTED, LocalDateTime.now()) == null) {
            throw new ValidationException("Оставлять отзыв можно только после окончания аренды вещи.");
        }
        if (commentDto.getText().isBlank() || commentDto.getText() == null)
            throw new ValidationException("Комментарий не может быть пустой");
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setAuthor(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден")));
        comment.setItem(itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена")));
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public ItemDto editItem(ItemDto itemDto, Integer itemId, Integer userId) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        Item itemInDb = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена"));
        if (itemInDb.getOwner() != owner)
            throw new NotFoundException("Вещь может редактировать только ее владелец");
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) itemInDb.setName(itemDto.getName());
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank())
            itemInDb.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) itemInDb.setAvailable(itemDto.getAvailable());
        return ItemMapper.toItemDto(itemRepository.save(itemInDb));
    }

    @Override
    public ItemDto getItem(Integer itemId, Integer userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена"));
        Boolean isOwner = false;
        if (item.getOwner().getId() == userId) isOwner = true;
        ItemDto itemDto = ItemMapper.toItemDto(item);
        if (isOwner) checkLastAndNestBookings(itemDto);
        return addCommentsForItemDto(itemDto);
    }

    @Override
    public List<ItemDto> getUserItems(Integer userId) {
        return itemRepository.getItemsByOwnerId(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .map(this::checkLastAndNestBookings)
                .map(this::addCommentsForItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getNeedItems(String searchString) {
        if (searchString.isBlank()) return new ArrayList<>();
        return itemRepository.searchByText(searchString)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private ItemDto checkLastAndNestBookings(ItemDto itemDto) {
        Booking last = bookingRepository.findFirstByItemIdAndStatusNotLikeAndStartIsBeforeOrStartEqualsOrderByStartDesc(itemDto.getId(), BookingStatus.REJECTED, LocalDateTime.now(), LocalDateTime.now());
        Booking next = bookingRepository.findFirstByItemIdAndStatusNotLikeAndStartIsAfterOrderByStart(itemDto.getId(), BookingStatus.REJECTED, LocalDateTime.now());
        if (last != null) itemDto.setLastBooking(bookingMapper.toBookingDtoToOwner(last));
        if (next != null) itemDto.setNextBooking(bookingMapper.toBookingDtoToOwner(next));
        return itemDto;
    }

    private ItemDto addCommentsForItemDto(ItemDto itemDto) {
        List<Comment> comments = commentRepository.findAllByItem_idOrderByCreatedDesc(itemDto.getId());
        itemDto.setComments(CommentMapper.toCommentDto(comments));
        return itemDto;
    }

}
