package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;
    @Autowired
    MockMvc mockMvc;

    ItemDto itemDto;
    Item item;
    ItemDto itemWithBookingAndCommentsDto;
    User owner;
    User booker;

    ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        itemRequest = itemRequest.builder()
                .id(1001)
                .description("desc")
                .creator(owner)
                .created(LocalDateTime.now())
                .build();

        owner = User.builder()
                .id(1)
                .name("name")
                .email("mail@mall.ru")
                .build();

        booker = User.builder()
                .id(101)
                .name("name ")
                .email("booker@mail.ru")
                .build();

        item = Item.builder()
                .id(1)
                .name("Item №1")
                .description("Desc №1")
                .available(true)
                .itemRequest(itemRequest)
                .ownerId(owner.getId())
                .build();

        itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(itemRequest.getId())
                .build();

        itemWithBookingAndCommentsDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(null)
                .nextBooking(null)
                .comments(new ArrayList<>())
                .requestId(itemRequest.getId())
                .build();

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
    }

    @SneakyThrows
    @Test
    void testGetAll() {
        when(itemService.getAllItemsUser(anyInt()))
                .thenReturn(List.of(itemWithBookingAndCommentsDto));
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(item.getId()), Integer.class))
                .andExpect(jsonPath("$[0].description", is(item.getDescription()), String.class))
                .andExpect(jsonPath("$[0].name", is(item.getName()), String.class));
    }

    @SneakyThrows
    @Test
    void testSearchItemsByText() {
        when(itemService.getByText("found one item"))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "found one item")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemDto))));

        when(itemService.getByText("items not found"))
                .thenReturn(List.of());

        mockMvc.perform(get("/items/search")
                        .param("text", "items not found")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of())));
    }

    @SneakyThrows
    @Test
    void testAdd() {
        when(itemService.createItem(anyInt(), any()))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(item.getDescription()), String.class))
                .andExpect(jsonPath("$.requestId", is(itemRequest.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(item.getName()), String.class));
    }

    @SneakyThrows
    @Test
    void testUpdate_whenAllAreOk_aAndReturnUpdatedItem() {
        when(itemService.updateItem(anyInt(), anyInt(), any()))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class));
    }

    @SneakyThrows
    @Test
    void testUpdate_whenAllAreNotOk_aAndReturnExceptionNotFoundRecordInBD() {
        when(itemService.updateItem(anyInt(), anyInt(), any()))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(patch("/items/{itemId}", item.getId())
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(item))
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void addCommentToItem_whenAllIsOk_returnSavedComment() {
        CommentDto commentDto = CommentDto.builder()
                .id(1)
                .text("comment 1")
                .authorName("name user")
                .created(LocalDateTime.now().minusSeconds(5)).build();
        when(itemService.addComment(any(), any(), any())).thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", item.getId())
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Integer.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText()), String.class))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName()), String.class));
    }
}