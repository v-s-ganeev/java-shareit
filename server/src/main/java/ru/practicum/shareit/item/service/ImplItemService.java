package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.ItemDto;
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
    private final CommentRepository commentRepository;

    @Override
    public ItemDto addItem(ItemDto itemDto, Integer userId) {
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
    public List<ItemDto> getUserItems(Integer userId, PageRequest pageRequest) {
        return itemRepository.findAllByOwnerIdOrderById(userId, pageRequest)
                .stream()
                .map(ItemMapper::toItemDto)
                .map(this::checkLastAndNestBookings)
                .map(this::addCommentsForItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getNeedItems(String searchString, PageRequest pageRequest) {
        if (searchString.isBlank()) return new ArrayList<>();
        return itemRepository.searchByText(searchString, pageRequest)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private ItemDto checkLastAndNestBookings(ItemDto itemDto) {
        Booking last = bookingRepository.findFirstByItemIdAndStatusNotLikeAndStartIsBeforeOrStartEqualsOrderByStartDesc(itemDto.getId(), BookingStatus.REJECTED, LocalDateTime.now(), LocalDateTime.now());
        Booking next = bookingRepository.findFirstByItemIdAndStatusNotLikeAndStartIsAfterOrderByStart(itemDto.getId(), BookingStatus.REJECTED, LocalDateTime.now());
        if (last != null) itemDto.setLastBooking(BookingMapper.toBookingDtoToOwner(last));
        if (next != null) itemDto.setNextBooking(BookingMapper.toBookingDtoToOwner(next));
        return itemDto;
    }

    private ItemDto addCommentsForItemDto(ItemDto itemDto) {
        List<Comment> comments = commentRepository.findAllByItem_idOrderByCreatedDesc(itemDto.getId());
        itemDto.setComments(CommentMapper.toCommentDto(comments));
        return itemDto;
    }

}
