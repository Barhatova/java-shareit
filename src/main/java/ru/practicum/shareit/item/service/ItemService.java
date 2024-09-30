package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;

import java.util.Collection;

public interface ItemService {
    ItemDto createItem(int userId, NewItemRequest newItemRequest);

    ItemDto updateItem(int userId, int itemId, NewItemRequest newItemRequest);

    void deleteItem(int itemId);

    Collection<ItemDto> getByText(String text);

    ItemDto getItemById(int itemId);

    Collection<ItemDto> getOwnerItems(int ownerId);
}
