package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    ItemDto createItem(Integer userId, NewItemRequest item);

    ItemDto updateItem(Integer userId, Integer itemId, NewItemRequest item);

    Collection<ItemDto> getByText(String text);

    Item getItemById(Integer itemId);

    Collection<ItemDto> getAllItemsUser(Integer userId);

    int getOwnerId(Integer itemId);

    ItemDto getById(Integer userId, Integer itemId);

    CommentDto addComment(Integer userId, Integer itemId, CommentDto commentDto);
}
