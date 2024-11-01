package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.NewUserRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("test")
class UserServiceImplTest {

    private final UserService userService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private User user1;
    private NewUserRequest newUserRequest;
    private UserDto userDto;
    private User user2;
    private User userNull;
    private UserDto userDtoAllFieldsNull;
    private UserDto userDtoNull;
    private User userAllFieldsNull;


    @BeforeEach
    void setUp() {
        user1 = new User(null, "name1", "email@emal.tr1");
        user2 = new User(null, "name2", "email@emal.tr2");
        newUserRequest = new NewUserRequest("name_update", "email@emal.tr_update");

        userNull = null;
        userDto = UserDto.builder()
                .name("name userDto1")
                .email("userDto1@mans.gf")
                .build();

        userDtoAllFieldsNull = new UserDto();
        userDtoNull = null;
        userAllFieldsNull = new User();
    }

    @Test
    void getUserById_WhenAllIsOk() {
        User savedUser = userService.createUser(user1);
        User user = userService.getUserById(savedUser.getId());

        assertNotNull(user.getId());
        assertEquals(user.getName(), user1.getName());
        assertEquals(user.getEmail(), user1.getEmail());
    }

    @Test
    void getUserById_whenUserNotFoundInDb_return() {
        User savedUser = userService.createUser(user1);

        assertThrows(NotFoundException.class,
                () -> userService.getUserById(9000));
    }

    @SneakyThrows
    @Test
    void getAllUsers() {
        List<User> users = List.of(user1, user2);
        userService.createUser(user1);
        userService.createUser(user2);
        Collection<User> result = userService.getAllUser();

        assertEquals(users.size(), result.size());
        for (User user : users) {
            assertThat(result, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(user.getName())),
                    hasProperty("email", equalTo(user.getEmail()))
            )));
        }
    }

    @Test
    void addToStorage() {
        userService.createUser(user1);
        Collection<User> users = userService.getAllUser();
        boolean result = false;
        Integer id = users.stream()
                .filter(u -> u.getEmail().equals(user1.getEmail()))
                .findFirst()
                .map(User::getId).orElse(null);

        User userFromDb = userService.getUserById(id);

        assertEquals(1, users.size());
        assertEquals(user1.getName(), userFromDb.getName());
        assertEquals(user1.getEmail(), userFromDb.getEmail());
    }

    @Test
    void updateInStorage_whenAllIsOkAndNameIsNull_returnUpdatedUser() {
        User createdUser = userService.createUser(user1);

        Collection<User> beforeUpdateUsers = userService.getAllUser();
        Integer id = beforeUpdateUsers.stream()
                .filter(u -> u.getEmail().equals(user1.getEmail()))
                .findFirst()
                .map(User::getId).orElse(null);
        assertNotNull(id);
        assertEquals(id, createdUser.getId());

        User userFromDbBeforeUpdate = userService.getUserById(id);

        assertEquals(userFromDbBeforeUpdate.getName(), user1.getName());
        assertEquals(userFromDbBeforeUpdate.getEmail(), user1.getEmail());

        userService.updateUser(createdUser.getId(), newUserRequest);

        User userFromDbAfterUpdate = userService.getUserById(id);

        assertEquals(userFromDbBeforeUpdate.getId(), userFromDbAfterUpdate.getId());
        assertEquals(userFromDbAfterUpdate.getName(), newUserRequest.getName());
        assertEquals(userFromDbAfterUpdate.getEmail(), newUserRequest.getEmail());
    }

    @Test
    void updateInStorage_whenAllIsOk_returnUpdatedUser() {
        User createdUser = userService.createUser(user1);

        Collection<User> beforeUpdateUsers = userService.getAllUser();
        Integer id = beforeUpdateUsers.stream()
                .filter(u -> u.getEmail().equals(user1.getEmail()))
                .findFirst()
                .map(User::getId).orElse(null);
        assertNotNull(id);
        assertEquals(id, createdUser.getId());

        User userFromDbBeforeUpdate = userService.getUserById(id);

        assertEquals(userFromDbBeforeUpdate.getName(), user1.getName());
        assertEquals(userFromDbBeforeUpdate.getEmail(), user1.getEmail());

        userService.updateUser(createdUser.getId(), newUserRequest);

        User userFromDbAfterUpdate = userService.getUserById(id);

        assertEquals(userFromDbBeforeUpdate.getId(), userFromDbAfterUpdate.getId());
        assertEquals(userFromDbAfterUpdate.getName(), newUserRequest.getName());
        assertEquals(userFromDbAfterUpdate.getEmail(), newUserRequest.getEmail());
    }

    @Test
    void removeFromStorage() {
        User savedUser = userService.createUser(user1);
        Collection<User> beforeDelete = userService.getAllUser();
        assertEquals(1, beforeDelete.size());
        userService.deleteUserById(savedUser.getId());
        Collection<User> afterDelete = userService.getAllUser();
        assertEquals(0, afterDelete.size());
    }

    @Test
    void userMapperTest_mapToModel_whenAllIsOk() {
        User user1 = UserMapper.mapToUser(userDto);
        assertEquals(userDto.getId(), user1.getId());
        assertEquals(userDto.getName(), user1.getName());
        assertEquals(userDto.getEmail(), user1.getEmail());
    }

    @Test
    void userMapperTest_mapToModel_whenAllFieldsAreNull() {
        User userNull = UserMapper.mapToUser(userDtoAllFieldsNull);
        assertEquals(userDtoAllFieldsNull.getId(), userNull.getId());
        assertEquals(userDtoAllFieldsNull.getName(), userNull.getName());
        assertEquals(userDtoAllFieldsNull.getEmail(), userNull.getEmail());
    }

    @Test
    void userMapperTest_mapToDto_whenAllFieldsAreNull() {
        UserDto userDtoNull = UserMapper.userMapToDto(userAllFieldsNull);
        assertEquals(userAllFieldsNull.getId(), userDtoNull.getId());
        assertEquals(userAllFieldsNull.getName(), userDtoNull.getName());
        assertEquals(userAllFieldsNull.getEmail(), userDtoNull.getEmail());
    }
}
