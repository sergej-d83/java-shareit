package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private static final String USER_ID = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader(USER_ID) Long userId,
                                        @RequestBody @Valid ItemRequestDto itemRequestDto) {

        return itemRequestService.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader(USER_ID) Long userId) {

        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(USER_ID) Long userId,
                                               @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") int from,
                                               @Positive @RequestParam(value = "size", defaultValue = "10") int size) {

        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(@RequestHeader(USER_ID) Long userId, @PathVariable Long requestId) {

        return itemRequestService.getRequest(userId, requestId);
    }
}
