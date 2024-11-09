package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;

import ru.practicum.shareit.booking.dto.NewBookingRequestDto;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                 @RequestBody NewBookingRequestDto bookingRequest) {
        return bookingService.createBooking(userId,bookingRequest);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader("X-Sharer-User-Id") Integer userId,
                              @PathVariable Integer bookingId,
                              @RequestParam Boolean approved) {
        return bookingService.approve(userId,bookingId,approved);
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