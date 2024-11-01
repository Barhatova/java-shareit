package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Marker;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.UserDto;


@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Validated({Marker.Create.class}) @RequestBody UserDto userDto) {
        return userClient.createUser(userDto);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Object> updateUser(@Validated({Marker.Update.class}) @RequestBody UserDto userDto,
                                             @PathVariable("id") Integer userId) {
        return userClient.updateUser(userDto, userId);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deleteUserById(@PathVariable("id") Integer userId) {
        return userClient.deleteUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUser() {
        return userClient.getAllUser();
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> getUserById(@PathVariable("id") Integer userId) {
        return userClient.getUserById(userId);
    }


}