package ru.practicum.shareit.itemRequestTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ItemRequestServiceTest {
    @Autowired
    private ItemRequestService service;

    @MockBean
    private ItemRequestRepository itemRequestRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ItemRepository itemRepository;

    private ItemRequest itemRequest = ItemRequest.builder().id(1).description("itemRequestDescription").requestorId(1).created(LocalDateTime.now()).build();
    private ItemRequestDto itemRequestDto = ItemRequestDto.builder().id(1).description("itemRequestDescription").requestorId(1).created(LocalDateTime.now()).build();
    private User user = User.builder().id(1).name("user").email("user@user.ru").build();
    private User owner = User.builder().id(2).name("owner").email("owner@owner.ru").build();
    private Item item = Item.builder().id(1).name("itemName").description("itemDescription").owner(owner).available(true).build();
    private PageRequest pageRequest = PageRequest.of(0, 10);

    @Test
    void addItemRequest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto itemRequestDtoOutput = service.addItemRequest(itemRequestDto, 1);

        assertNotNull(itemRequestDtoOutput);
        assertEquals(itemRequestDtoOutput.getDescription(), itemRequestDto.getDescription());
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void getMyItemRequests() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequestorIdOrderByCreated(anyInt())).thenReturn(List.of(itemRequest));
        when(itemRepository.findAllByRequestId(anyInt())).thenReturn(List.of(item));

        List<ItemRequestDto> itemRequests = service.getMyItemRequests(1);

        assertNotNull(itemRequests);
        assertEquals(itemRequests.size(), 1);
        verify(itemRequestRepository, times(1)).findAllByRequestorIdOrderByCreated(anyInt());
        verify(itemRepository, times(1)).findAllByRequestId(anyInt());
    }

    @Test
    void getAlItemRequests() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequestorIdIsNotOrderByCreated(anyInt(), any(PageRequest.class))).thenReturn(List.of(itemRequest));
        when(itemRepository.findAllByRequestId(anyInt())).thenReturn(List.of(item));

        List<ItemRequestDto> itemRequests = service.getAllItemRequests(1, pageRequest);

        assertNotNull(itemRequests);
        assertEquals(itemRequests.size(), 1);
        verify(itemRequestRepository, times(1)).findAllByRequestorIdIsNotOrderByCreated(anyInt(), any(PageRequest.class));
        verify(itemRepository, times(1)).findAllByRequestId(anyInt());
    }

    @Test
    void getItemRequestById() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyInt())).thenReturn(Optional.of(itemRequest));

        ItemRequestDto itemRequestDtoOutput = service.getItemRequestById(1, 1);

        assertNotNull(itemRequestDtoOutput);
        assertEquals(itemRequestDtoOutput.getDescription(), itemRequest.getDescription());
        verify(itemRequestRepository, times(1)).findById(anyInt());
    }
}
