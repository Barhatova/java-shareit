package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequestDto;

import java.util.Collection;

public interface BookingService {
    BookingDto createBooking(Integer userId, NewBookingRequestDto bookingRequest);

    BookingDto approve(Integer bookingId, Integer userId, Boolean approve);

    BookingDto getBookingById(Integer bookingId, Integer userId);

    Collection<BookingDto> getAllBookingByUserId(Integer userId, String state);

    Collection<BookingDto> getAllBookingsByOwner(Integer userId, String state);
}