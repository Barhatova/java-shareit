package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.ShortItemBookingDto;

import java.util.List;

@Slf4j
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    Integer id;
    String name;
    String description;
    Boolean available;
    ShortItemBookingDto lastBooking;
    ShortItemBookingDto nextBooking;
    List<CommentDto> comments;
    Integer requestId;
}