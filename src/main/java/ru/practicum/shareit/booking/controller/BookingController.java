package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                    @RequestBody NewBookingRequest bookingRequest) {
        if (bookingRequest == null) {
            throw new NotFoundException("Новое бронирование не указано");
        }
        return bookingService.createBooking(userId, bookingRequest);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader("X-Sharer-User-Id") Integer userId,
                              @PathVariable Integer bookingId,
                              @RequestParam Boolean approved) {
        return bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                     @PathVariable Integer bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDto> getAllBookingByUserId(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                        @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllBookingByUserId(userId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getAllBookingsByOwner(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                        @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllBookingsByOwner(userId, state);
    }
}
