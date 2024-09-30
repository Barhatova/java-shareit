package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;
import java.util.Collection;
import java.util.Optional;

public interface ItemStorage {
    Item createItem(Item item);

    Item updateItem(Item item);

    void deleteItem(Integer itemId);

    Collection<Item> getByText(String text);

    Optional<Item> getItemById(Integer itemId);

    Collection<Item> getOwnerItems(Integer ownerId);
}