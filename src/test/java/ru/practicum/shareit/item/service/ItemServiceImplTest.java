package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.InvalidDataException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.UserNotOwnItem;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Autowired
    private ItemService itemService;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private RequestRepository requestRepository;

    private User user;
    private User owner;
    private Item item;
    private Booking booking;
    private Comment comment;
    private ItemRequest request;

    @BeforeEach
    void setup() {

        user = new User();
        user.setId(1L);
        user.setName("user");
        user.setEmail("user@user.com");

        owner = new User();
        owner.setId(2L);
        owner.setName("owner");
        owner.setEmail("owner@owner.com");

        request = new ItemRequest();
        request.setId(1L);
        request.setDescription("tool");
        request.setCreated(LocalDateTime.now());
        request.setRequester(user);

        item = new Item();
        item.setId(1L);
        item.setName("tool");
        item.setDescription("test tool");
        item.setAvailable(true);
        item.setRequest(request);

        comment = new Comment();
        comment.setId(1L);
        comment.setAuthor(user);
        comment.setText("cool");
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.WAITING);
    }

    @Test
    void addItemTest() {

        Mockito
                .when(userRepository.findAll())
                .thenReturn(List.of(owner));

        Mockito
                .when(requestRepository.findById(request.getId()))
                .thenReturn(Optional.ofNullable(request));
        Mockito
                .when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        ItemDto itemDto = itemService.addItem(ItemMapper.toItemDto(item), owner.getId());

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertEquals(item.getRequest().getId(), itemDto.getRequestId());

        Throwable userIdNegative = assertThrows(InvalidDataException.class,
                () -> itemService.addItem(ItemMapper.toItemDto(item), -1L));
        assertEquals("Неправильный номер пользователя: " + -1L, userIdNegative.getMessage());

        Throwable wrongUserId = assertThrows(UserNotFoundException.class,
                () -> itemService.addItem(ItemMapper.toItemDto(item), 3L));
        assertEquals("Пользователь под номером " + 3L + " не найден.", wrongUserId.getMessage());

        item.setAvailable(null);

        Throwable availableIsNull = assertThrows(InvalidDataException.class,
                () -> itemService.addItem(ItemMapper.toItemDto(item), 2L));
        assertEquals("Статус доступности предмета к аренде не указан.", availableIsNull.getMessage());

        item.setAvailable(true);
        item.setName("");

        Throwable itemNameBlank = assertThrows(InvalidDataException.class,
                () -> itemService.addItem(ItemMapper.toItemDto(item), 2L));
        assertEquals("Название предмета не указанно.", itemNameBlank.getMessage());

        item.setName("tool");
        item.setDescription(null);

        Throwable descriptionNull = assertThrows(InvalidDataException.class,
                () -> itemService.addItem(ItemMapper.toItemDto(item), 2L));
        assertEquals("Описание предмета не указанно.", descriptionNull.getMessage());

        item.setDescription("test tool");
    }

    @Test
    void updateItemTest() {

        Mockito
                .when(userRepository.findAll())
                .thenReturn(List.of(owner));

        Mockito
                .when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        Item updatedItem = new Item();
        updatedItem.setName("updated tool");
        updatedItem.setDescription("updated test tool");
        item.setOwner(owner.getId());

        ItemDto itemDto = itemService.updateItem(ItemMapper.toItemDto(updatedItem), item.getId(), owner.getId());

        assertEquals(itemDto.getName(), updatedItem.getName());
        assertEquals(itemDto.getDescription(), updatedItem.getDescription());

        item.setOwner(3L);
        Throwable notOwner = assertThrows(UserNotOwnItem.class,
                () -> itemService.updateItem(itemDto, item.getId(), owner.getId()));
        assertEquals("Предмет номер " + item.getId() + " не принадлежит пользователю номер " + owner.getId(), notOwner.getMessage());

        Throwable throwable = assertThrows(InvalidDataException.class,
                () -> itemService.updateItem(itemDto, item.getId(), -1L));
        assertEquals("Номер предмета и пользователя должны быть больше нуля.", throwable.getMessage());

        item.setOwner(owner.getId());
        Throwable userNotFound = assertThrows(UserNotFoundException.class,
                () -> itemService.updateItem(itemDto, item.getId(), 3L));
        assertEquals("Пользователь под номером " + 3 + " не найден.", userNotFound.getMessage());

    }

    @Test
    void getItemTest() {

        Mockito
                .when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(commentRepository.findByItemId(anyLong()))
                .thenReturn(List.of(comment));
        Mockito
                .when(bookingRepository.findFirstByItem_IdAndStartIsBefore(anyLong(), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(Optional.ofNullable(booking));
        Mockito
                .when(bookingRepository.findFirstByItem_IdAndStartIsAfter(anyLong(), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(Optional.ofNullable(booking));

        item.setOwner(owner.getId());

        ItemDto itemDto = itemService.getItem(item.getId(), owner.getId());

        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertEquals(comment.getCreated(), itemDto.getComments().get(0).getCreated());
        assertEquals(comment.getText(), itemDto.getComments().get(0).getText());
        assertEquals(comment.getAuthor().getName(), itemDto.getComments().get(0).getAuthorName());
        assertEquals(booking.getId(), itemDto.getLastBooking().getId());
        assertEquals(booking.getBooker().getId(), itemDto.getLastBooking().getBookerId());

        Throwable throwable = assertThrows(ItemNotFoundException.class,
                () -> itemService.getItem(2L, owner.getId()));
        assertEquals("Предмет под номером " + 2 + " не найден.", throwable.getMessage());
    }

    @Test
    void getItemsByUserIdTest() {

        Mockito
                .when(userRepository.findAll())
                .thenReturn(List.of(owner));

        Mockito
                .when(itemRepository.findAllByOwner(owner.getId(), PageRequest.of(0, 1)))
                .thenReturn(List.of(item));
        Mockito
                .when(commentRepository.findAllByItem_Owner(owner.getId()))
                .thenReturn(List.of(comment));
        Mockito
                .when(bookingRepository.findAllByItem_OwnerAndState(owner.getId(), Status.APPROVED.name(), PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        item.setOwner(owner.getId());
        booking.setStatus(Status.APPROVED);

        List<ItemDto> itemDtos = itemService.getItemsByUserId(owner.getId(), 0, 1);

        assertEquals(1, itemDtos.size());
        assertEquals(item.getName(), itemDtos.get(0).getName());
        assertEquals(item.getDescription(), itemDtos.get(0).getDescription());
        assertEquals(comment.getAuthor().getName(), itemDtos.get(0).getComments().get(0).getAuthorName());
        assertEquals(comment.getText(), itemDtos.get(0).getComments().get(0).getText());

        Throwable throwable = assertThrows(UserNotFoundException.class,
                () -> itemService.getItemsByUserId(3L, 0, 1));
        assertEquals("Пользователь с номером " + 3 + " не найден.", throwable.getMessage());
    }

    @Test
    void searchItemsTest() {

        Mockito
                .when(itemRepository.searchItems(any(String.class), any(PageRequest.class)))
                .thenReturn(List.of(item));

        List<ItemDto> itemDtos = itemService.searchItems("tool", 0, 1);

        assertEquals(1, itemDtos.size());
        assertEquals(item.getName(), itemDtos.get(0).getName());
        assertEquals(item.getDescription(), itemDtos.get(0).getDescription());
        assertEquals(item.getAvailable(), itemDtos.get(0).getAvailable());
    }

    @Test
    void createCommentTest() {

        Mockito
                .when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(bookingRepository.findAllByBooker_IdAndEndIsBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        Mockito
                .when(commentRepository.save(any()))
                .thenReturn(comment);

        CommentDto commentDto = itemService.createComment(user.getId(), item.getId(), CommentMapper.toCommentDto(comment));

        assertEquals(comment.getAuthor().getName(), commentDto.getAuthorName());
        assertEquals(comment.getText(), commentDto.getText());
    }
}