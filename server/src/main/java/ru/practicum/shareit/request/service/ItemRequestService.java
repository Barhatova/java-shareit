package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestDto createRequest(Integer requesterId, ItemRequestDto requestDto);

    ItemRequestDto getById(Integer userId, Integer requestId);

    Collection<ItemRequestDto> getRequests(Integer userId);

    Collection<ItemRequestDto> getUserRequests(Integer userId);
}