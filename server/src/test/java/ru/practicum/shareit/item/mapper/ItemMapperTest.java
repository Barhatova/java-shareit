package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ItemMapperTest {
    ItemRequest itemRequest;
    Booking lastBooking;
    Booking nextBooking;

    @Test
    void test_itemMapToDto() {
        Item item = new Item();
        item.setId(2);
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwnerId(5);
        item.setItemRequest(itemRequest);
        item.setLastBooking(lastBooking);
        item.setNextBooking(nextBooking);

        ItemDto result = ItemMapper.itemMapToDto(item);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());
        assertEquals(item.getItemRequest(), result.getRequestId());
        assertEquals(item.getLastBooking(), result.getLastBooking());
        assertEquals(item.getNextBooking(), result.getNextBooking());
    }
}
