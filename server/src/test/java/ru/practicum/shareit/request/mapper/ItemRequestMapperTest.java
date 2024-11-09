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
    void test_toItemRequest() {
        ItemRequestDto requestDto = new ItemRequestDto();

        ItemRequest result = ItemRequest.builder()
                .id(requestDto.getId())
                .description(requestDto.getDescription())
                .build();

        assertNotNull(result);
        assertEquals(requestDto.getId(), result.getId());
        assertEquals(requestDto.getDescription(), result.getDescription());
    }
}