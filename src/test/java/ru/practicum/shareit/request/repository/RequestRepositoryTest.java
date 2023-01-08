package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class RequestRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RequestRepository requestRepository;

    private User user;
    private User requester;
    private ItemRequest itemRequest;

    @BeforeEach
    void setup() {

        user = new User();
        user.setName("user");
        user.setEmail("user@user.com");

        requester = new User();
        requester.setName("requester");
        requester.setEmail("requester@user.com");

        itemRequest = new ItemRequest();
        itemRequest.setDescription("need tool");
        itemRequest.setRequester(requester);
        itemRequest.setCreated(LocalDateTime.of(2023, 1, 4, 12, 0, 1));
    }

    @Test
    void contentLoads() {
        assertNotNull(entityManager);
        assertNotNull(requestRepository);
    }

    @Test
    void findAllByRequester_IdTest() {

        entityManager.persist(user);
        entityManager.persist(requester);
        entityManager.persist(itemRequest);

        List<ItemRequest> itemRequests = requestRepository.findAllByRequester_Id(itemRequest.getRequester().getId());

        assertEquals(1, itemRequests.size());
        assertEquals(itemRequest.getDescription(), itemRequests.get(0).getDescription());
    }

    @Test
    void findAllPageableTest() {

        entityManager.persist(user);
        entityManager.persist(requester);
        entityManager.persist(itemRequest);

        List<ItemRequest> itemRequests = requestRepository.findAllPageable(user.getId(), PageRequest.of(0, 1));

        assertEquals(1, itemRequests.size());
        assertEquals(itemRequest.getDescription(), itemRequests.get(0).getDescription());
    }
}