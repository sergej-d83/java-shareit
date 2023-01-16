package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    @Override
    public ItemRequestDto createRequest(Long userId, ItemRequestDto itemRequestDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь под номером " + userId + " не найден."));

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());

        return ItemRequestMapper.toRequestDto(requestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь под номером " + userId + " не найден."));

        List<ItemRequest> requests = requestRepository.findAllByRequester_Id(userId);
        List<Item> items = itemRepository.findAllByRequest(requests);

        List<ItemRequestDto> requestDtos;
        requestDtos = addItemsToRequests(requests, items);

        return requestDtos;
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, int from, int size) {

        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь под номером " + userId + " не найден."));

        List<ItemRequest> requests = requestRepository.findAllPageable(userId, PageRequest.of(from, size));
        List<Item> items = itemRepository.findAllByRequest(requests);

        List<ItemRequestDto> requestDtos;
        requestDtos = addItemsToRequests(requests, items);

        return requestDtos;
    }

    @Override
    public ItemRequestDto getRequest(Long userId, Long requestId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь под номером " + userId + " не найден."));

        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException("Запрос не найден."));

        List<Item> items = itemRepository.findAllByRequest_Id(requestId);

        ItemRequestDto requestDto = ItemRequestMapper.toRequestDto(itemRequest);
        requestDto.setItems(items.stream().map(ItemRequestMapper::toRequestDtoItem).collect(Collectors.toList()));

        return requestDto;
    }

    private List<ItemRequestDto> addItemsToRequests(List<ItemRequest> requests, List<Item> items) {

        List<ItemRequestDto> requestDtos = requests.stream().map(ItemRequestMapper::toRequestDto).collect(Collectors.toList());

        for (ItemRequestDto request : requestDtos) {
            request.setItems(items
                    .stream()
                    .filter(i -> i.getRequest().getId().equals(request.getId()))
                    .map(ItemRequestMapper::toRequestDtoItem)
                    .collect(Collectors.toList())
            );
        }

        return requestDtos;
    }
}
