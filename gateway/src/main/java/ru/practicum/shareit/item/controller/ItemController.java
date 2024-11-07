package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.Marker;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collections;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private static final String OWNER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @Validated({Marker.Create.class}) @RequestBody ItemDto itemDto) {
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @Validated({Marker.Update.class}) @RequestBody ItemDto itemDto,
                                             @PathVariable Integer itemId) {
        return itemClient.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getByText(@RequestParam String text) {
        if (text.isBlank()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return itemClient.getByText(text);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsUser(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemClient.getAllItemsUser(userId);
    }

    @GetMapping("{itemId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                           @PathVariable Integer itemId) {
        return itemClient.getById(itemId, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(OWNER_ID_HEADER) Integer userId,
                                                @PathVariable Integer itemId,
                                                @Valid @RequestBody CommentDto commentDto) {
        return itemClient.addComment(itemId, userId, commentDto);
    }
}