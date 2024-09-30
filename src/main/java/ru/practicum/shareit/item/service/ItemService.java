package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import java.util.Collection;

public interface ItemService {
    ItemDto createItem(Integer userId, NewItemRequest newItemRequest);

    ItemDto updateItem(Integer userId, Integer itemId, NewItemRequest newItemRequest);

    void deleteItem(Integer itemId);
    Collection<ItemDto> getByText(String text);

    ItemDto getItemById(Integer itemId);

    Collection<ItemDto> getOwnerItems(Integer ownerId);
}
