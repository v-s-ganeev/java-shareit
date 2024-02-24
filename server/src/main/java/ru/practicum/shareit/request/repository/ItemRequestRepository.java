package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

    List<ItemRequest> findAllByRequestorIdOrderByCreated(Integer requestorId);

    List<ItemRequest> findAllByRequestorIdIsNotOrderByCreated(Integer requestorId, PageRequest pageRequest);
}
