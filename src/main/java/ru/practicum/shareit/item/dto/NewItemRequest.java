package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewItemRequest {
    String name;
    String description;
    Boolean available;

    public boolean hasName() {
        return getName() != null;
    }

    public boolean hasDescription() {
        return getDescription() != null;
    }

    public boolean hasAvailable() {
        return getAvailable() != null;
    }
}