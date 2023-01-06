package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private CommentRepository commentRepository;

    private User user;
    private Item item;
    private Comment comment;

    @BeforeEach
    void setup() {
        user = new User();
        user.setName("user");
        user.setEmail("user@user.com");

        item = new Item();
        item.setName("item");
        item.setDescription("test item");
        item.setAvailable(true);
        item.setOwner(1L);

        comment = new Comment();
        comment.setText("cool");
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.of(2023, 1, 4, 12, 0, 1));
    }

    @Test
    void findByItemIdTest() {

        em.persist(user);
        em.persist(item);
        em.persist(comment);

        List<Comment> comments = commentRepository.findByItemId(item.getId());

        assertEquals(1, comments.size());
        assertEquals(comment.getText(), comments.get(0).getText());
        assertEquals(comment.getAuthor().getName(), comments.get(0).getAuthor().getName());
        assertEquals(comment.getItem().getName(), comments.get(0).getItem().getName());
        assertEquals(comment.getCreated().toString(), comments.get(0).getCreated().toString());
    }

    @Test
    void findAllByItem_Owner() {

        item.setOwner(2L);
        em.persist(user);
        em.persist(item);
        em.persist(comment);

        List<Comment> comments = commentRepository.findByItemId(item.getOwner());

        assertEquals(1, comments.size());
        assertEquals(comment.getText(), comments.get(0).getText());
        assertEquals(comment.getAuthor().getName(), comments.get(0).getAuthor().getName());
        assertEquals(comment.getItem().getName(), comments.get(0).getItem().getName());
        assertEquals(comment.getCreated().toString(), comments.get(0).getCreated().toString());
    }
}