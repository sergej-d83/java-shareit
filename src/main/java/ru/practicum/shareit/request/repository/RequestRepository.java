package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequester_Id(Long userId);

    @Query("select ir from ItemRequest ir where ir.requester.id <> ?1 order by ir.created desc")
    List<ItemRequest> findAllPageable(Long userId, Pageable pageable);
}
