package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.ShortItemBooking;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    @Null
    Integer id;
    @NotBlank
    @NotNull
    @Size(min = 1)
    String name;
    @NotNull
    String description;
    @NotNull
    Boolean available;
    ShortItemBooking lastBooking;
    ShortItemBooking nextBooking;
    List<CommentDto> comments;
    Integer requestId;
}