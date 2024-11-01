package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") Integer requesterId,
                                        @RequestBody ItemRequestDto itemRequestDto) {
        return requestService.createRequest(requesterId, itemRequestDto);
    }

    @GetMapping("{requestId}")
    public ItemRequestDto getById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                  @PathVariable Integer requestId) {
        return requestService.getById(userId, requestId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> getRequests(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return requestService.getRequests(userId);
    }

    @GetMapping
    public Collection<ItemRequestDto> getUserRequests(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return requestService.getUserRequests(userId);
    }
}
