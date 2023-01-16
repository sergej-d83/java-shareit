package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(" select i from Item i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%')) " +
            "and i.available = true")
    List<Item> searchItems(String text, Pageable pageable);

    List<Item> findAllByOwner(Long owner, Pageable pageable);

    @Query("select i from Item i where i.request in ?1")
    List<Item> findAllByRequest(List<ItemRequest> requests);

    List<Item> findAllByRequest_Id(Long requestId);

}
