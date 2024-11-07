package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.UpdateItemRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                              @RequestBody ItemDto item) {
        if (item == null) {
            throw new NotFoundException("Не указан инструмент для добавления");
        }
        return itemService.createItem(userId,item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                              @RequestBody UpdateItemRequestDto request,
                              @PathVariable Integer itemId) {
        return itemService.updateItem(itemId,userId,request);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getByText(@RequestParam String text) {
        return itemService.getByText(text);
    }

    @GetMapping
    public Collection<ItemDto> getAllItemsUser(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getAllItemsUser(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                            @PathVariable Integer itemId) {
        return itemService.getById(itemId, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                    @PathVariable Integer itemId,
                                    @RequestBody CommentDto commentDto) {
        return itemService.addComment(itemId, userId, commentDto);
    }
}