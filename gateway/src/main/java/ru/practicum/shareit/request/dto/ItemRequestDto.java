package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.Marker;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestDto {
    Integer id;

    @Size(min = 1, max = 64, groups = {Marker.Create.class, Marker.Update.class})
    @NotBlank(groups = Marker.Create.class)
    String description;
}
