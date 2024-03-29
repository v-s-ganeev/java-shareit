package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    @Query("select i from Item i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            "or upper(i.description) like upper(concat('%', ?1, '%')) " +
            "and i.available = true")
    List<Item> searchByText(String text, PageRequest pageRequest);

    List<Item> findAllByOwnerIdOrderById(Integer ownerId, PageRequest pageRequest);

    List<Item> findAllByRequestId(Integer requestId);

    @Query("SELECT i " +
            "FROM Item i " +
            "WHERE i.requestId IN ?1")
    List<Item> getAllItemsByRequestsId(List<Integer> requestsId);
}
