package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;

import java.util.Collection;

public interface BookingService {
    BookingDto createBooking(Integer userId, NewBookingRequest bookingRequest);

    BookingDto approve(Integer userId, Integer bookingId, Boolean approved);

    BookingDto getBookingById(Integer userId, Integer bookingId);

    Collection<BookingDto> getAllBookingByUserId(Integer userId, String state);

    Collection<BookingDto> getAllBookingsByOwner(Integer userId, String state);
}
