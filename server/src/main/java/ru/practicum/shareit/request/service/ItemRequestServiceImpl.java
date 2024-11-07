package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;

import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;

import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    UserService userService;
    ItemRequestRepository itemRequestRepository;
    ItemRepository itemRepository;

    public ItemRequestDto createItemRequest(Integer userId, ItemRequestDto request) {
        ItemRequest itemRequest = ItemRequest.builder()
                .description(request.getDescription())
                .creator((userService.getUserById(userId)))
                .created(LocalDateTime.now())
                .build();
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    public ItemRequestDto getRequestById(Integer userId, Integer requestId) {
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос на аренду с id = " + requestId + " не найден"));
        itemRequest.setItems(itemRepository.getAllByItemRequest(itemRequest));
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setRequestor(UserMapper.userMapToDto(userService.getUserById(userId)));
        return itemRequestDto;
    }

    @Override
    public Collection<ItemRequestDto> getUserRequests(Integer userId) {
        userService.getUserById(userId);
        Collection<ItemRequestDto> list = new ArrayList<>();
        Collection<ItemRequest> findAllByCreatorIdOrderByCreatedDesc = itemRequestRepository.getByCreatorIdOrderByCreatedDesc(userId);
        findAllByCreatorIdOrderByCreatedDesc.forEach(i -> {
            i.setItems(itemRepository.getAllByItemRequest(i));
            ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(i);
            list.add(itemRequestDto);
        });
        return list;
    }

    @Override
    public Collection<ItemRequestDto> getRequests(Integer userId) {
        userService.getUserById(userId);
        Collection<ItemRequestDto> list = new ArrayList<>();
        Collection<ItemRequest> getAllByRequesterIdIsNot = itemRequestRepository.getByCreatorIdIsNot(userId);
        getAllByRequesterIdIsNot.forEach(itemRequest -> {
            itemRequest.setItems(itemRepository.getAllByItemRequest(itemRequest));
            ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
            list.add(itemRequestDto);
        });
        return list;
    }
}