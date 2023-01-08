package ru.practicum.shareit.item.repository;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository itemRepository;

    private User user;
    private Item item;
    private ItemRequest request;

    @BeforeEach
    void setup() {

        user = new User();
        user.setName("user");
        user.setEmail("user@user.com");

        request = new ItemRequest();
        request.setDescription("test request");
        request.setRequester(user);
        request.setCreated(LocalDateTime.of(2023, 1, 20, 12, 0, 1));

        item = new Item();
        item.setName("item");
        item.setDescription("test item");
        item.setAvailable(true);
        item.setOwner(1L);
        item.setRequest(request);

    }

    @Test
    void contextLoads() {
        assertNotNull(em);
        assertNotNull(itemRepository);
    }

    @Test
    void searchItemsTest() {

        em.persist(user);
        item.setOwner(user.getId());
        em.persist(item);
        em.persist(request);

        List<Item> items = itemRepository.searchItems("item", Pageable.unpaged());

        assertEquals(1, items.size());

        assertEquals(items.get(0).getName(), item.getName());
        assertEquals(items.get(0).getDescription(), item.getDescription());
        assertEquals(items.get(0).getAvailable(), item.getAvailable());
        assertEquals(items.get(0).getOwner(), item.getOwner());
    }

    @Test
    void findAllByOwnerTest() {

        em.persist(user);
        item.setOwner(user.getId());
        em.persist(item);
        em.persist(request);

        List<Item> items = itemRepository.findAllByOwner(item.getOwner(), Pageable.unpaged());

        assertEquals(1, items.size());
        assertEquals(item.getName(), items.get(0).getName());
        assertEquals(item.getDescription(), items.get(0).getDescription());
        assertEquals(item.getAvailable(), items.get(0).getAvailable());
        assertEquals(item.getOwner(), items.get(0).getOwner());
    }

    @Test
    void findAllByRequestTest() {

        em.persist(user);
        em.persist(request);
        item.setOwner(user.getId());
        em.persist(item);

        List<Item> items = itemRepository.findAllByRequest(List.of(request));

        assertEquals(1, items.size());
        assertEquals(item.getName(), items.get(0).getName());
        assertEquals(item.getDescription(), items.get(0).getDescription());
        assertEquals(item.getAvailable(), items.get(0).getAvailable());
        assertEquals(item.getOwner(), items.get(0).getOwner());
    }

    @Test
    void findAllByRequest_IdTest() {

        em.persist(user);
        em.persist(request);
        item.setOwner(user.getId());
        em.persist(item);

        List<Item> items = itemRepository.findAllByRequest_Id(item.getRequest().getId());

        assertEquals(1, items.size());
        assertEquals(item.getName(), items.get(0).getName());
        assertEquals(item.getDescription(), items.get(0).getDescription());
        assertEquals(item.getAvailable(), items.get(0).getAvailable());
        assertEquals(item.getOwner(), items.get(0).getOwner());
    }
}