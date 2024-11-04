package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.ShortItemBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ItemMapperTest {
    ItemRequest itemRequest;
    Booking lastBooking;
    Booking nextBooking;
    ShortItemBookingDto lastBooking1;
    ShortItemBookingDto nextBooking1;

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

    @Test
    void test_mapToItem() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(2);
        itemDto.setName("name");
        itemDto.setDescription("desc");
        itemDto.setAvailable(true);
        itemDto.setLastBooking(lastBooking1);
        itemDto.setNextBooking(nextBooking1);

        Item result = ItemMapper.mapToItem(itemDto);

        assertNotNull(result);
        assertEquals(itemDto.getId(), result.getId());
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        assertEquals(itemDto.getAvailable(), result.getAvailable());
        assertEquals(itemDto.getLastBooking(), result.getLastBooking());
        assertEquals(itemDto.getNextBooking(), result.getNextBooking());
    }
}
