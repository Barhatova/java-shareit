package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class ItemStorageImpl implements ItemStorage {
    private final Map<Integer, Item> items;

    public ItemStorageImpl() {
        items = new HashMap<>();
    }

    @Override
    public Item createItem(Item item) {
        item.setId(getId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public void deleteItem(int itemId) {
        items.remove(itemId);
    }

    @Override
    public Collection<Item> getByText(String searchText) {
        String text = searchText.toLowerCase();
        return items.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text) ||
                        item.getDescription().toLowerCase().contains(text)))
                .filter(Item::isAvailable)
                .toList();
    }

    @Override
    public Optional<Item> getItemById(int itemId) {
        if (items.containsKey(itemId)) {
            return Optional.of(items.get(itemId));
        }
        return Optional.empty();
    }

    @Override
    public Collection<Item> getOwnerItems(int ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId() == ownerId)
                .toList();
    }

    private Integer getId() {
        return items.keySet().stream()
                .max(Integer::compareTo)
                .map(aLong -> aLong + 1)
                .orElse(Math.toIntExact(1L));
    }
}