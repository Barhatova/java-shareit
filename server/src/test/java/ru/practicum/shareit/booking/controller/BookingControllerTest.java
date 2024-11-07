package ru.practicum.shareit.booking.controller;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    BookingService bookingService;
    @Autowired
    MockMvc mockMvc;

    BookingDto bookingDto;
    User owner;
    User booker;
    Item item;
    ItemDto itemDto;
    LocalDateTime now;
    LocalDateTime nowPlus10H;
    LocalDateTime nowPlus20H;

    @BeforeEach
    void setup() {
        now = LocalDateTime.now();
        nowPlus10H = LocalDateTime.now().plusHours(10);
        nowPlus20H = LocalDateTime.now().plusHours(20);

        booker = User.builder()
                .id(101)
                .name("name1")
                .email("name1@yandex.ru")
                .build();

        bookingDto = BookingDto.builder()
                .id(1)
                .item(itemDto)
                .start(nowPlus10H)
                .end(nowPlus20H)
                .status(BookingStatus.WAITING)
                .build();

        owner = User.builder()
                .id(1)
                .name("name2")
                .email("name2@yandex.ru")
                .build();

        item = Item.builder()
                .id(1)
                .name("name3")
                .description("desc")
                .ownerId(owner.getId())
                .available(true)
                .build();
    }

    @SneakyThrows
    @Test
    void test_ReturnBookingForResponse() {
        BookingDto bookingDto1ForResponse =  BookingDto.builder()
                .id(1)
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(itemDto)
                .booker(UserMapper.userMapToDto(booker))
                .status(BookingStatus.WAITING).build();

        when(bookingService.createBooking(anyInt(), any())).thenReturn(bookingDto1ForResponse);

        String result = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", booker.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDto)))

                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(bookingDto1ForResponse), result);
    }

    @SneakyThrows
    @Test
    void test_UpdateByOwner() {
        BookingDto bookingDto1ForResponse = BookingDto.builder()
                .id(1)
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(itemDto)
                .booker(UserMapper.userMapToDto(booker))
                .status(BookingStatus.WAITING).build();

        when(bookingService.approve(anyInt(), anyInt(), any()))
                .thenReturn(bookingDto1ForResponse);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", bookingDto1ForResponse.getId())
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", owner.getId())
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(mapper.writeValueAsString(bookingDto1ForResponse), result);
    }

    @SneakyThrows
    @Test
    void test_GetWithStatusById() {
        BookingDto bookingDto1ForResponse =  BookingDto.builder()
                .id(1)
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(itemDto)
                .booker(UserMapper.userMapToDto(booker))
                .status(BookingStatus.WAITING).build();

        when(bookingService.getBookingById(any(), any()))
                .thenReturn(bookingDto1ForResponse);
        String result = mockMvc.perform(get("/bookings/{bookingId}", bookingDto1ForResponse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", booker.getId()))

                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(bookingDto1ForResponse), result);
    }

    @SneakyThrows
    @Test
    void test_GetByUserId() {
        BookingDto bookingDto1ForResponse =  BookingDto.builder()
                .id(1)
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(itemDto)
                .booker(UserMapper.userMapToDto(booker))
                .status(BookingStatus.WAITING).build();

        when(bookingService.getAllBookingByUserId(anyInt(), any()))
                .thenReturn(List.of(bookingDto1ForResponse));

        String result = mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", booker.getId())
                        .param("state", "ALL")

                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(List.of(bookingDto1ForResponse)), result);
    }

    @SneakyThrows
    @Test
    void test_GetByOwnerId() {
        BookingDto bookingDto1ForResponse =  BookingDto.builder()
                .id(1)
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(itemDto)
                .booker(UserMapper.userMapToDto(booker))
                .status(BookingStatus.WAITING).build();

        when(bookingService.getAllBookingsByOwner(anyInt(), any()))
                .thenReturn(List.of(bookingDto1ForResponse));

        String result = mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", owner.getId())
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(List.of(bookingDto1ForResponse)), result);
    }
}