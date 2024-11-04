package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class CommentMapperTest {
    ItemRequest itemRequest;
    Booking lastBooking;
    Booking nextBooking;

    @Test
    void test_toDto() {
        User user = new User();
        user.setId(1);
        user.setName("name");
        user.setEmail("name@yandex.ru");

        Item item = new Item();
        item.setId(2);
        item.setName("name1");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwnerId(5);
        item.setItemRequest(itemRequest);
        item.setLastBooking(lastBooking);
        item.setNextBooking(nextBooking);

        Comment comment = new Comment();
        comment.setId(2);
        comment.setText("text");
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        CommentDto result = CommentMapper.toDto(comment);

        assertNotNull(result);
        assertEquals(comment.getId(), result.getId());
        assertEquals(comment.getText(), result.getText());
        assertEquals(comment.getItem().getId(), result.getItem().getId());
        assertEquals(comment.getCreated(), result.getCreated());
    }
}
