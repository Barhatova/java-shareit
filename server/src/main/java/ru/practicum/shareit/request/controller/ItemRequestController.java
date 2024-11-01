package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                     @RequestBody ItemRequestDto itemRequestDto) {
        log.debug("Добавление нового запроса на инструмент");
        return requestService.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping("{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                          @PathVariable Integer requestId) {
        log.debug("Получить запрос по id");
        return requestService.getRequestById(userId, requestId);
    }

    @GetMapping
    public Collection<ItemRequestDto> getUserRequests(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.debug("Получить список своих запросов вместе с данными об ответах на них");
        return requestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> getRequests(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.debug("Получить список запросов, созданных другими пользователями");
        return requestService.getRequests(userId);
    }
}