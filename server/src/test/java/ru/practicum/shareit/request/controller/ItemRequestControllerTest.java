package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {
    @InjectMocks
    ItemRequestController itemRequestController;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    MockMvc mockMvc;
    User owner;
    User booker;
    ItemRequest itemRequest;
    ItemRequestDto itemRequestDto;
    BookingDto bookingDtoForCreate;
    User owner1;
    User booker1;
    User request;
    Item item;
    ItemDto itemDto;
    LocalDateTime now;
    LocalDateTime nowPlus10Hours;
    LocalDateTime nowPlus20Hours;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        nowPlus10Hours = now.plusHours(10);
        nowPlus20Hours = now.plusHours(20);

        owner1 = User.builder()
                .id(1)
                .name("imya usera 1 owner")
                .email("owner1@m.ri")
                .build();

        booker1 = User.builder()
                .id(101)
                .name("imya usera 101 booker")
                .email("booker@pochta.tu")
                .build();

        request = User.builder()
                .id(51)
                .name("name requester")
                .email("requester@yaschik.po")
                .build();

        item = Item.builder()
                .id(1)
                .name("nazvanie veschi 1")
                .description("opisanie veschi 1")
                .ownerId(owner1.getId())
                .available(true)
                .itemRequest(itemRequest)
                .build();
        itemDto = ItemMapper.itemMapToDto(item);

        itemRequest = ItemRequest.builder()
                .id(1)
                .description("Book")
                .creator(request)
                .created(now)
                .items(List.of(item))
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requestor(UserMapper.userMapToDto(request))
                .build();

        bookingDtoForCreate = BookingDto.builder()
                .id(1)
                .item(itemDto)
                .booker(UserMapper.userMapToDto(booker1))
                .start(nowPlus10Hours)
                .end(nowPlus20Hours)
                .status(BookingStatus.WAITING)
                .build();
    }

    @SneakyThrows
    @Test
    void test_createRequest() {
        when(itemRequestService.createItemRequest(anyInt(), any()))
                .thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", itemRequest.getCreator().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class));
    }

    @SneakyThrows
    @Test
    void test_getItemRequestsByUserId() {
        ItemRequestDto itemRequestDtoWithAnswersForOutput = ItemRequestDto.builder()
                .id(1)
                .description("Book")
                .created(LocalDateTime.now())
                .build();
        when(itemRequestService.getUserRequests(anyInt()))
                .thenReturn(List.of(itemRequestDtoWithAnswersForOutput));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(objectMapper.writeValueAsString(List.of(itemRequestDtoWithAnswersForOutput))));
        verify(itemRequestService, times(1)).getUserRequests(1);
    }

    @SneakyThrows
    @Test
    void test_getAllRequests() {
        ItemRequestDto itemRequestDtoWithAnswers = ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestor(null)
                .created(now)
                .build();

        when(itemRequestService.getRequests(anyInt()))
                .thenReturn(List.of(itemRequestDtoWithAnswers));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(objectMapper.writeValueAsString(List.of(itemRequestDtoWithAnswers))));
        verify(itemRequestService, times(1)).getRequests(anyInt());
    }

    @SneakyThrows
    @Test
    void test_getItemRequestById() {
        ItemRequestDto itemRequestDtoWithAnswers = ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestor(null)
                .created(now)
                .build();

        when(itemRequestService.getRequestById(anyInt(), anyInt()))
                .thenReturn(itemRequestDtoWithAnswers);

        mockMvc.perform(get("/requests/{requestId}", itemRequestDtoWithAnswers.getId())
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(objectMapper.writeValueAsString(itemRequestDtoWithAnswers)));
        verify(itemRequestService, times(1)).getRequestById(anyInt(), anyInt());
    }
}
