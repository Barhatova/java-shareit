package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Builder
@AllArgsConstructor
public class ResponseItemRequestDto {
    private Integer id;
    private String description;
    private LocalDateTime created;
    private UserDto requester;
    private Collection<ItemDto> items;
}