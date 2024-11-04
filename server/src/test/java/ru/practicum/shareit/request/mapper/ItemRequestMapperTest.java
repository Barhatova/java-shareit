package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ItemRequestMapperTest {
    @Test
    void test_toItemRequestDto() {
        User user = new User();
        user.setId(1);
        user.setName("name");
        user.setEmail("name@yandex.ru");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("desc");
        itemRequest.setCreator(user);
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequestDto result = ItemRequestMapper.toItemRequestDto(itemRequest);

        assertNotNull(result);
        assertEquals(itemRequest.getId(), result.getId());
        assertEquals(itemRequest.getDescription(), result.getDescription());
        assertEquals(itemRequest.getCreated(), result.getCreated());
    }

    @Test
    void test_toItemRequestResponseDto() {
        User user = new User();
        user.setId(1);
        user.setName("name1");
        user.setEmail("name1@yandex.ru");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("desc");
        itemRequest.setCreator(user);
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequestDto result = ItemRequestMapper.toItemRequestDto(itemRequest);

        assertNotNull(result);
        assertEquals(itemRequest.getId(), result.getId());
        assertEquals(itemRequest.getDescription(), result.getDescription());
        assertEquals(itemRequest.getCreator().getId(), result.getRequestor().getId());
        assertEquals(itemRequest.getCreated(), result.getCreated());
    }
}