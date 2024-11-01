package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    UserService userService;

    @Autowired
    MockMvc mockMvc;
    User user;
    UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User(1, "name", "name@yandex.ru");
        userDto = new UserDto(user.getId(), user.getName(), user.getEmail());

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @SneakyThrows
    @Test
    void test_createUserToBD_ThenReturnSavedUser() {
        when(userService.createUser(any()))
                .thenReturn(user);

        mockMvc.perform(post("/users")
                        .content(new ObjectMapper().writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect((jsonPath("$.email").value(user.getEmail())));
    }

    @SneakyThrows
    @Test
    void test_updateInStorage_returnUserDto() {
        when(userService.updateUser(anyInt(), any()))
                .thenReturn(user);
        String result = mockMvc.perform(patch("/users/{userId}", userDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect((jsonPath("$.email").value(user.getEmail())))
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(userDto), result);
    }

    @SneakyThrows
    @Test
    void test_getAllUsersFromStorage_thenResponseStatusOkWithUserCollection() {
        when(userService.getAllUser())
                .thenReturn(List.of(user));

        String result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(userDto)), result);
    }

    @SneakyThrows
    @Test
    void test_getUserById() {
        when(userService.getUserById(anyInt()))
                .thenReturn(user);
        String result = mockMvc.perform(get("/users/{userId}", user.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(user), result);
    }

    @SneakyThrows
    @Test
    void test_deleteUserFromDB_ThenReturnOk() {
        mockMvc.perform(delete("/users/{id}", user.getId()))
                .andExpect(status().isOk());
        verify(userService, times(1)).deleteUserById(user.getId());
    }
}