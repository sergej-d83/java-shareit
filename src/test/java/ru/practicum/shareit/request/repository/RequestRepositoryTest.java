package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RequestRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RequestRepository requestRepository;

    private User user;
    private ItemRequest itemRequest;

    @BeforeEach
    void setup() {

        user = new User();
        user.setName("user");
        user.setEmail("user@user.com");

        itemRequest = new ItemRequest();
        itemRequest.setDescription("need tool");
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.of(2023,1,4,12,0,1));
    }

    @Test
    void contentLoads() {
        assertNotNull(entityManager);
        assertNotNull(requestRepository);
    }

    @Test
    void findAllByRequester_IdTest() {

        entityManager.persist(user);
        entityManager.persist(itemRequest);

        List<ItemRequest> itemRequests = requestRepository.findAllByRequester_Id(itemRequest.getRequester().getId());

        assertEquals(1, itemRequests.size());
        assertEquals(itemRequest.getDescription(), itemRequests.get(0).getDescription());
    }

    @Test
    void findAllPageableTest() {

        entityManager.persist(user);
        entityManager.persist(itemRequest);

        List<ItemRequest> itemRequests = requestRepository.findAllPageable(2L, Pageable.unpaged());

        assertEquals(1, itemRequests.size());
        assertEquals(itemRequest.getDescription(), itemRequests.get(0).getDescription());
    }
}