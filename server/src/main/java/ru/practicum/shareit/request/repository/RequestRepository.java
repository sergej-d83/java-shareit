package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequester_Id(Long userId);

    @Query(value = "select * from requests ir " +
            "where ir.requester_id <> ?1 order by ir.created desc", nativeQuery = true)
    List<ItemRequest> findAllPageable(Long userId, Pageable pageable);
}
