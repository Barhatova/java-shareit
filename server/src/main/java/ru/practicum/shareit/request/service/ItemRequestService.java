package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(Integer userId, ItemRequestDto requestDto);

    ItemRequestDto getRequestById(Integer userId, Integer requestId);

    Collection<ItemRequestDto> getUserRequests(Integer userId);

    Collection<ItemRequestDto> getRequests(Integer userId);
}