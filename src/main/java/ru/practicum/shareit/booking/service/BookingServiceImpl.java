package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.BookingStatusMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public BookingDto createBooking(Integer bookerId, NewBookingRequest bookingRequest) {
        if (bookingRequest.getEnd().isBefore(bookingRequest.getStart())) {
            throw new DataException("Дата завершения бронирования не может быть раньше даты начала бронирования");
        }
        User booker = userService.getUserById(bookerId);
        Item item = itemService.getItemById(bookingRequest.getItemId());
        User owner = userService.getUserById(item.getOwnerId());
        if (owner.getId() == bookerId) {
            throw new BookingException("Пользователь не может арендовать собственную вещь");
        }
        if (item.getAvailable()) {
            Booking booking = Booking.builder()
                    .start(bookingRequest.getStart())
                    .end(bookingRequest.getEnd())
                    .item(item)
                    .booker(booker)
                    .status(BookingStatus.WAITING)
                    .build();
            return BookingMapper.toBookingDto(bookingRepository.save(booking));
        } else {
            throw new NotAvailableException("Вещь уже в аренде");
        }
    }

    @Transactional
    @Override
    public BookingDto approve(Integer userId, Integer bookingId, Boolean approved) {
        BookingDto booking = getBookingById(userId, bookingId);
        Integer ownerId = itemService.getOwnerId(booking.getItem().getId());
        if (ownerId.equals(userId) && booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new AlreadyExistsException("Бронирование уже одобрено владельцем инструмента");
        }
        if (!ownerId.equals(userId)) {
            throw new BookingException("Пользователь с id {}" + userId + "не является владельцем вещи");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
            bookingRepository.save(BookingStatus.APPROVED, bookingId);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
            bookingRepository.save(BookingStatus.REJECTED, bookingId);
        }
        return booking;
    }

    @Override
    public BookingDto getBookingById(Integer userId, Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(("Бронирование с id {}" + bookingId + "не найдено")));
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwnerId().equals(userId)) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new ValidationException("Пользователь с id {}" + userId + "не является владельцем");
        }
    }

    @SneakyThrows
    @Override
    public Collection<BookingDto> getAllBookingByUserId(Integer userId, String state) {
        userService.getUserById(userId);
        LocalDateTime now = LocalDateTime.now();
        BookingStatus bookingStatus = BookingStatusMapper.toBookingStatus(state);
        switch (bookingStatus) {
            case ALL:
                return BookingMapper.toBookingDto(bookingRepository.getByBookerIdOrderByStartDesc(userId));
            case CURRENT:
                return BookingMapper.toBookingDto(bookingRepository.getByBookerIdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(userId, now, now));
            case PAST:
                return BookingMapper.toBookingDto(bookingRepository.getByBookerIdAndEndIsBeforeOrderByStartDesc(userId, now));
            case FUTURE:
                return BookingMapper.toBookingDto(bookingRepository.getByBookerIdAndStartIsAfterOrderByStartDesc(userId, now));
            case WAITING:
                BookingMapper.toBookingDto(bookingRepository.getByBookerIdAndStartIsAfterAndStatusIsOrderByStartDesc(userId, now, BookingStatus.WAITING));
            case REJECTED:
                BookingMapper.toBookingDto(bookingRepository.getByBookerIdAndStatusIsOrderByStartDesc(userId, BookingStatus.REJECTED));
        }
        throw new BadRequestException("Введен некорректный запрос");
    }

    @SneakyThrows
    @Override
    public Collection<BookingDto> getAllBookingsByOwner(Integer ownerId, String state) {
        userService.getUserById(ownerId);
        LocalDateTime now = LocalDateTime.now();
        BookingStatus bookingStatus = BookingStatusMapper.toBookingStatus(state);
        switch (bookingStatus) {
            case ALL:
                return BookingMapper.toBookingDto(bookingRepository.getByItemOwnerId(ownerId));
            case CURRENT:
                return BookingMapper.toBookingDto(bookingRepository.getCurrentBookingsOwner(ownerId, now));
            case PAST:
                return BookingMapper.toBookingDto(bookingRepository.getPastBookingsOwner(ownerId, now));
            case FUTURE:
                return BookingMapper.toBookingDto(bookingRepository.getFutureBookingsOwner(ownerId, now));
            case WAITING:
                BookingMapper.toBookingDto(bookingRepository.getWaitingBookingsOwner(ownerId, now, BookingStatus.WAITING));
            case REJECTED:
                BookingMapper.toBookingDto(bookingRepository.getRejectedBookingsOwner((ownerId), BookingStatus.REJECTED));
        }
        throw new BadRequestException("Введен некорректный запрос");
    }
}
