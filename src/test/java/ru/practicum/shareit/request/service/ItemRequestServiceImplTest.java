package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
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
import static org.mockito.ArgumentMatchers.anyList;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RequestRepository requestRepository;

    private User user;
    private ItemRequest itemRequest;
    private Item item;

    @BeforeEach
    void setup() {

        user = new User();
        user.setId(1L);
        user.setName("user");
        user.setEmail("user@user.com");

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("test description");
        itemRequest.setCreated(LocalDateTime.of(2023, 1, 4, 12, 0, 1));
        itemRequest.setRequester(user);

        item = new Item();
        item.setId(2L);
        item.setName("tool");
        item.setDescription("test tool");
        item.setAvailable(true);
        item.setOwner(1L);
        item.setRequest(itemRequest);
    }

    @Test
    void createRequestTest() {

        Mockito
                .when(userRepository.findById(user.getId()))
                .thenReturn(Optional.ofNullable(user));
        Mockito
                .when(requestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequest);

        ItemRequestDto requestDto = itemRequestService.createRequest(user.getId(), ItemRequestMapper.toRequestDto(itemRequest));

        assertEquals(requestDto.getDescription(), itemRequest.getDescription());
        assertEquals(requestDto.getCreated().toString(), itemRequest.getCreated().toString());

        Throwable throwable = assertThrows(UserNotFoundException.class,
                () -> itemRequestService.createRequest(2L, ItemRequestMapper.toRequestDto(itemRequest)));
        assertEquals("Пользователь под номером " + 2 + " не найден.", throwable.getMessage());
    }

    @Test
    void getUserRequestsTest() {

        Mockito
                .when(userRepository.findById(user.getId()))
                .thenReturn(Optional.ofNullable(user));
        Mockito
                .when(requestRepository.findAllByRequester_Id(user.getId()))
                .thenReturn(List.of(itemRequest));
        Mockito
                .when(itemRepository.findAllByRequest(anyList()))
                .thenReturn(List.of(item));

        List<ItemRequestDto> itemRequestDto = itemRequestService.getUserRequests(user.getId());

        assertEquals(itemRequestDto.get(0).getDescription(), itemRequest.getDescription());
        assertEquals(itemRequestDto.get(0).getId(), itemRequest.getId());

        Throwable throwable = assertThrows(UserNotFoundException.class,
                () -> itemRequestService.getUserRequests(2L));
        assertEquals("Пользователь под номером " + 2 + " не найден.", throwable.getMessage());
    }

    @Test
    void getAllRequestsTest() {

        Mockito
                .when(userRepository.findById(user.getId()))
                .thenReturn(Optional.ofNullable(user));
        Mockito
                .when(requestRepository.findAllPageable(user.getId(), PageRequest.of(0, 1)))
                .thenReturn(List.of(itemRequest));
        Mockito
                .when(itemRepository.findAllByRequest(anyList()))
                .thenReturn(List.of(item));

        List<ItemRequestDto> itemRequestDto = itemRequestService.getAllRequests(user.getId(), 0, 1);

        assertEquals(itemRequestDto.get(0).getDescription(), itemRequest.getDescription());
        assertEquals(itemRequestDto.get(0).getId(), itemRequest.getId());

        Throwable throwable = assertThrows(UserNotFoundException.class,
                () -> itemRequestService.getAllRequests(2L, 0, 1));
        assertEquals("Пользователь под номером " + 2 + " не найден.", throwable.getMessage());
    }

    @Test
    void getRequestTest() {

        Mockito
                .when(userRepository.findById(user.getId()))
                .thenReturn(Optional.ofNullable(user));
        Mockito
                .when(requestRepository.findById(itemRequest.getId()))
                .thenReturn(Optional.ofNullable(itemRequest));
        Mockito
                .when(itemRepository.findAllByRequest_Id(itemRequest.getId()))
                .thenReturn(List.of(item));

        ItemRequestDto itemRequestDto = itemRequestService.getRequest(user.getId(), itemRequest.getId());

        assertEquals(itemRequestDto.getId(), itemRequest.getId());
        assertEquals(itemRequestDto.getDescription(), itemRequest.getDescription());
        assertEquals(itemRequestDto.getCreated().toString(), itemRequest.getCreated().toString());

        Throwable wrongUserId = assertThrows(UserNotFoundException.class,
                () -> itemRequestService.getRequest(2L, itemRequest.getId()));
        assertEquals("Пользователь под номером " + 2 + " не найден.", wrongUserId.getMessage());

        Throwable wrongRequestId = assertThrows(RequestNotFoundException.class,
                () -> itemRequestService.getRequest(user.getId(), 3L));
        assertEquals("Запрос не найден.", wrongRequestId.getMessage());
    }
}