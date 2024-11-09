package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.UpdateItemRequestDto;

import java.util.Collection;

public interface ItemService {
    ItemDto createItem(Integer itemId, ItemDto item);

    ItemDto updateItem(Integer itemId, Integer userId, UpdateItemRequestDto request);

    Collection<ItemDto> getByText(String text);

    Item getItemById(Integer itemId);

    Collection<ItemDto> getAllItemsUser(Integer userId);

    Integer getOwnerId(Integer itemId);

    ItemDto getById(Integer itemId, Integer userId);

    CommentDto addComment(Integer itemId, Integer userId, CommentDto commentDto);
}
