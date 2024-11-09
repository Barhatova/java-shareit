package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.Marker;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    Integer id;

    @NotBlank(groups = Marker.Create.class)
    @Size(min = 1, max = 64, groups = {Marker.Create.class, Marker.Update.class})
    String name;

    @NotBlank(groups = Marker.Create.class)
    @Email(groups = {Marker.Create.class, Marker.Update.class})
    @Size(min = 1, max = 64, groups = {Marker.Create.class, Marker.Update.class})
    String email;
}