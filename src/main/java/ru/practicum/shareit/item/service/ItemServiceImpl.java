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
    public ItemDto createItem(Integer userId, NewItemRequest newItemRequest) {
        getUser(userId);
        validateItem(newItemRequest);
        Item item = ItemMapper.mapToItem(newItemRequest);
        item.setOwnerId(userId);
        return ItemMapper.mapToDto(itemStorage.createItem(item));
    }

    @Override
    public ItemDto updateItem(Integer userId, Integer itemId, NewItemRequest newItemRequest) {
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
    public void deleteItem(Integer itemId) {
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
    public ItemDto getItemById(Integer itemId) {
        return ItemMapper.mapToDto(getItem(itemId));
    }

    @Override
    public Collection<ItemDto> getOwnerItems(Integer ownerId) {
        getUser(ownerId);
        return itemStorage.getOwnerItems(ownerId).stream()
                .map(ItemMapper::mapToDto)
                .toList();
    }

    private void getUser(Integer userId) {
        userStorage.getUserById(userId)
                .orElseThrow(() -> {
                    log.warn("Пользователь с id не найден {}", userId);
                    return new NotFoundException(String.format("Пользователь не найден", userId));
                });
    }

    private Item checkItemOwner(Integer userId, Integer itemId) {
        getUser(userId);
        Item item = getItem(itemId);
        if (item.getOwnerId() != userId) {
            log.warn("Пользователь {} не имеет вещь {}", userId, itemId);
            throw new NotFoundException(
                    String.format("Пользователь не имеет вещь", userId, itemId));
        }
        return item;
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

    private Item getItem(Integer itemId) {
        return itemStorage.getItemById(itemId)
                .orElseThrow(() -> {
                    log.warn("Вещь по идентификатору {} не найдена", itemId);
                    return new NotFoundException(String.format("Вещь по идентификатору не найдена", itemId));
                });
    }
}
