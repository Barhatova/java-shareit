package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewUserRequest {
    String name;
    String email;

    public boolean hasName() {
        return isNotBlank(name);
    }

    public boolean hasEmail() {
        return isNotBlank(email);
    }

    boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }
}