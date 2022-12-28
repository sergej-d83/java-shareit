package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemService itemService;
    private static final String USER_ID = "X-Sharer-User-id";

    @PostMapping
    public ItemDto addItem(@RequestHeader(USER_ID) Long userId, @RequestBody ItemDto itemDto) {
        return itemService.addItem(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(USER_ID) Long userId,
                                    @PathVariable Long itemId,
                                    @Valid @RequestBody CommentDto commentDto) {

        return itemService.createComment(userId, itemId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @RequestHeader(USER_ID) Long userId,
                              @PathVariable Long itemId) {

        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId, @RequestHeader(USER_ID) Long userId) {
        return itemService.getItem(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getItemsByUserId(@RequestHeader(USER_ID) Long userId,
                                          @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                          @Positive @RequestParam(defaultValue = "10") int size) {

        return itemService.getItemsByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text,
                                    @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                    @Positive @RequestParam(defaultValue = "10") int size) {

        return itemService.searchItems(text, from, size);
    }

}
