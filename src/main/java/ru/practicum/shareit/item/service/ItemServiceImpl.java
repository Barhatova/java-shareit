package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto createItem(int userId, NewItemRequest newItemRequest) {
        getUser(userId);
        validateItem(newItemRequest);
        Item item = ItemMapper.mapToItem(newItemRequest);
        item.setOwnerId(userId);
        return ItemMapper.mapToDto(itemStorage.createItem(item));
    }

    @Override
    public ItemDto updateItem(int userId, int itemId, NewItemRequest newItemRequest) {
        Item oldItem = checkItemOwner(userId, itemId);
        if (newItemRequest.hasName()) {
            oldItem.setName(newItemRequest.getName());
        }
        if (newItemRequest.hasDescription()) {
            oldItem.setDescription(newItemRequest.getDescription());
        }
        if (newItemRequest.hasAvailable()) {
            oldItem.setAvailable(newItemRequest.getAvailable());
        }
        return ItemMapper.mapToDto(itemStorage.updateItem(oldItem));
    }

    @Override
    public void deleteItem(int itemId) {
        itemStorage.deleteItem(itemId);
    }

    @Override
    public Collection<ItemDto> getByText(String text) {
        if (text.isEmpty() || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemStorage.getByText(text).stream()
                .map(ItemMapper::mapToDto)
                .toList();
    }

    @Override
    public ItemDto getItemById(int itemId) {
        return ItemMapper.mapToDto(getItem(itemId));
    }

    @Override
    public Collection<ItemDto> getOwnerItems(int ownerId) {
        getUser(ownerId);
        return itemStorage.getOwnerItems(ownerId).stream()
                .map(ItemMapper::mapToDto)
                .toList();
    }

    private void getUser(int userId) {
        userStorage.getUserById(userId)
                .orElseThrow(() -> {
                    log.warn("Пользователь с id не найден {}", userId);
                    return new NotFoundException(String.format("Пользователь не найден", userId));
                });
    }

    private Item checkItemOwner(int userId, int itemId) {
        getUser(userId);
        if (getItem(itemId).getOwnerId() != userId) {
            log.warn("Пользователь {} не имеет вещь {}", userId, itemId);
            throw new NotFoundException(
                    String.format("Пользователь не имеет вещь", userId, itemId));
        }
        return getItem(itemId);
    }

    private void validateItem(NewItemRequest item) {
        if (item.getName() == null || item.getName().isBlank()) {
            log.warn("Наименование вещи некорректно {}", item);
            throw new ValidationException("Наименование вещи некорректно");
        }
        if (item.getDescription() == null) {
            log.warn("Описание вещи некорректно {}", item);
            throw new ValidationException("Описание вещи некорректно");
        }
        if (item.getAvailable() == null) {
            log.warn("Информация о доступности вещи некорректна {}", item);
            throw new ValidationException("Информация о доступности вещи некорректна");
        }
    }

    private Item getItem(int itemId) {
        return itemStorage.getItemById(itemId)
                .orElseThrow(() -> {
                    log.warn("Вещь по идентификатору {} не найдена", itemId);
                    return new NotFoundException(String.format("Вещь по идентификатору не найдена", itemId));
                });
    }
}
