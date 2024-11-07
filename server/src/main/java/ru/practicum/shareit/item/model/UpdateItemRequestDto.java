package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.Booking;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class UpdateItemRequestDto {
    String name;
    String description;
    Boolean available;
    Booking lastBooking;
    Booking nextBooking;

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