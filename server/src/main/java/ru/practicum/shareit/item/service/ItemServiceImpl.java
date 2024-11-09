package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.UpdateItemRequestDto;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestService itemRequestService;

    @Override
    public ItemDto createItem(Integer userId, ItemDto item) {
        validateItem(item);
        User user = userService.getUserById(userId);
        Item newItem = ItemMapper.mapToItem(item);
        newItem.setOwnerId(user.getId());
        newItem.setItemRequest(item.getRequestId() != null ? ItemRequestMapper.toItemRequest(itemRequestService.getRequestById(userId, item.getRequestId())) : null);
        itemRepository.save(newItem);
        return ItemMapper.itemMapToDto(newItem);
    }

    @Override
    public ItemDto updateItem(Integer itemId, Integer userId, UpdateItemRequestDto request) {
        Item updatedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("У пользователя нет вещи"));
        if (!Objects.equals(updatedItem.getOwnerId(), userId)) {
            throw new NotFoundException("У пользователя нет вещи");
        }
        ItemMapper.updateItemFields(updatedItem, request);
        return ItemMapper.itemMapToDto(itemRepository.findById(itemId).get());
    }

    @Override
    public Collection<ItemDto> getByText(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.searchAvailableItems(text).stream()
                .map(ItemMapper::itemMapToDto)
                .filter(itemDTO -> itemDTO.getAvailable().equals(true))
                .toList();
    }

    @Override
    public Item getItemById(Integer itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещи с id {}" + itemId + "не существует"));
    }

    @Override
    public  Collection<ItemDto> getAllItemsUser(Integer userId) {
        userService.getUserById(userId);
        List<ItemDto> item = itemRepository.getAllByOwnerId(userId).stream()
                .map(ItemMapper::itemMapToDto)
                .toList();
        List<ItemDto> list = new ArrayList<>();
        item.stream().map(this::updateBookings).forEach(i -> {
            CommentMapper.toDtoList(commentRepository.getAllByItemId(i.getId()));
            list.add(i);
        });
        return  list;
    }

    @Override
    public Integer getOwnerId(Integer itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("У пользователя нет вещи", itemId)))
                .getOwnerId();
    }

    @Override
    public ItemDto getById(Integer itemId, Integer userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещи с id {} " + itemId + "не существует"));
        ItemDto result = ItemMapper.itemMapToDto(item);
        if (Objects.equals(item.getOwnerId(), userId)) {
            updateBookings(result);
        }
        List<Comment> comments = commentRepository.getAllByItemId(result.getId());
        result.setComments(CommentMapper.toDtoList(comments));
        return result;
    }

    @Override
    public CommentDto addComment(Integer itemId, Integer userId, CommentDto commentDto) {
        Item item = getItemById(itemId);
        User user = userService.getUserById(userId);
        List<Booking> bookings = bookingRepository
                .getByItemIdAndBookerIdAndStatusIsAndEndIsBefore(itemId, userId, BookingStatus.APPROVED, LocalDateTime.now());
        if (!bookings.isEmpty() && bookings.get(0).getStart().isBefore(LocalDateTime.now())) {
            Comment comment = CommentMapper.toComment(commentDto);
            comment.setItem(item);
            comment.setAuthor(user);
            comment.setCreated(LocalDateTime.now());
            return CommentMapper.toDto(commentRepository.save(comment));
        } else {
            throw new NotAvailableException("Бронирование для пользователя с id {}" + userId + " и вещи с id {}" +
                    itemId + "не найдено");
        }
    }

    public ItemDto updateBookings(ItemDto itemDto) {
        LocalDateTime now = LocalDateTime.now();
        Collection<Booking> bookings = bookingRepository.getBookingsItem(itemDto.getId());
        Booking lastBooking = bookings.stream()
                .filter(obj -> !(obj.getStatus().equals(BookingStatus.REJECTED.REJECTED)))
                .filter(obj -> obj.getStart().isBefore(now))
                .min((obj1, obj2) -> obj2.getStart().compareTo(obj1.getStart())).orElse(null);
        Booking nextBooking = bookings.stream()
                .filter(obj -> !(obj.getStatus().equals(BookingStatus.REJECTED)))
                .filter(obj -> obj.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart)).orElse(null);
        if (lastBooking != null) {
            itemDto.setLastBooking(BookingMapper.toItemBookingDto(lastBooking));
        }
        if (nextBooking != null) {
            itemDto.setNextBooking(BookingMapper.toItemBookingDto(nextBooking));
        }
        return itemDto;
    }

    private void validateItem(ItemDto item) {
        if (item.getAvailable() == null) {
            throw new ValidationException("При добавлении нового инструмента он должен быть доступен");
        }
        if (item.getName() == null || item.getName().isBlank()) {
            throw new ValidationException("Название инструмента не задано");
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ValidationException("Описание инструмента не задано");
        }
    }
}