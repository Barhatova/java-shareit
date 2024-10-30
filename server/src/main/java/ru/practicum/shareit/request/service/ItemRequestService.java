package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestDto createRequest(Integer requesterId, ItemRequestDto requestDto);

    ItemRequestDto getRequestById(Integer userId, Integer requestId);

    Collection<ItemRequestDto> getAllRequests(Integer userId);

    Collection<ItemRequestDto> getUserRequests(Integer userId);
}