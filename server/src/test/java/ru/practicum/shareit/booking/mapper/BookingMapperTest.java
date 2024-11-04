package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ShortItemBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class BookingMapperTest {
    ItemRequest itemRequest;
    Booking lastBooking;
    Booking nextBooking;

    @Test
    void test_toBookingDto() {
        User user = new User();
        user.setId(1);
        user.setName("name");
        user.setEmail("name@yandex.ru");

        Item item = new Item();
        item.setId(2);
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwnerId(5);
        item.setItemRequest(itemRequest);
        item.setLastBooking(lastBooking);
        item.setNextBooking(nextBooking);

        Booking booking = new Booking();
        booking.setId(1);
        booking.setBooker(user);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now());
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);

        BookingDto result = BookingMapper.toBookingDto(booking);

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getBooker().getId(), result.getBooker().getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getStatus(), result.getStatus());
    }

    @Test
    void test_toItemBookingDto() {
        ShortItemBookingDto shortItemBookingDto = new ShortItemBookingDto();

        Booking result = Booking.builder()
                .id(shortItemBookingDto.getId())
                .start(shortItemBookingDto.getStart())
                .end(shortItemBookingDto.getEnd())
                .build();

        assertNotNull(result);
        assertEquals(shortItemBookingDto.getId(), result.getId());
        assertEquals(shortItemBookingDto.getStart(), result.getStart());
        assertEquals(shortItemBookingDto.getEnd(), result.getEnd());
    }
}
