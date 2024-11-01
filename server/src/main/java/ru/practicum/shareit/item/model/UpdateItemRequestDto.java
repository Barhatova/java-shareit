package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateItemRequestDto {
    String name;
    String description;
    Boolean available;

    public boolean hasItemName() {
        return ! (name == null || name.isBlank());
    }

    public boolean hasItemDescription() {
        return ! (description == null || description.isBlank());
    }

    public boolean hasItemAvailable() {
        return ! (available == null);
    }
}