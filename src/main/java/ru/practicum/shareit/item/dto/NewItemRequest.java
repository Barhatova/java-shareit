package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewItemRequest {
    @Size(min = 1)
    String name;
    @Size(min = 1)
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

    boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }
}