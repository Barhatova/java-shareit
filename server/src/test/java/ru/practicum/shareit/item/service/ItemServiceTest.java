package ru.practicum.shareit.item.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemServiceTest {
    final CommentRepository commentRepository;
    final ItemRequestService itemRequestService;
    final BookingRepository bookingRepository;
    final ItemService itemService;
    final UserService userService;
    final EntityManager em;
    ItemRequest itemRequest1;
    UserDto ownerDto1;
    User owner1;
    UserDto requesterDto101;
    User requester101;
    UserDto bookerDto;
    User booker;
    UserDto userDtoForTest;
    User userForTest;
    LocalDateTime now;
    LocalDateTime nowPlus10min;
    LocalDateTime nowPlus10hours;
    Item item1;
    ItemDto itemDto1;
    ItemRequestDto itemRequestDto1;
    CommentDto commentDto;
    NewItemRequest newItemRequest;

    Booking bookingFromBd;
    TypedQuery<Item> query;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        nowPlus10min = now.plusMinutes(10);
        nowPlus10hours = now.plusHours(10);

        ownerDto1 = UserDto.builder()
                .name("name ownerDto1")
                .email("ownerDto1@mans.gf")
                .build();

        owner1 = User.builder()
                .id(ownerDto1.getId())
                .name(ownerDto1.getName())
                .email(ownerDto1.getEmail())
                .build();

        requesterDto101 = UserDto.builder()
                .name("name requesterDto101")
                .email("requesterDto101@mans.gf")
                .build();

        requester101 = User.builder()
                .id(requesterDto101.getId())
                .name(requesterDto101.getName())
                .email(requesterDto101.getEmail())
                .build();

        userDtoForTest = UserDto.builder()
                .name("name userDtoForTest")
                .email("userDtoForTest@userDtoForTest.zx")
                .build();

        userForTest = User.builder()
                .name(userDtoForTest.getName())
                .email(userDtoForTest.getEmail())
                .build();

        bookerDto = UserDto.builder()
                .name("booker")
                .email("booker@wa.dzd")
                .build();

        booker = User.builder()
                .name(bookerDto.getName())
                .email(bookerDto.getEmail())
                .build();

        itemRequest1 = ItemRequest.builder()
                .description("description for request 1")
                .creator(requester101)
                .created(now)
                .build();

        item1 = Item.builder()
                .name("name for item 1")
                .description("description for item 1")
                .ownerId(owner1.getId())
                .available(true)
                .build();

        itemDto1 = ItemDto.builder()
                .name(item1.getName())
                .description(item1.getDescription())
                .available(item1.getAvailable())
                .build();

        newItemRequest = NewItemRequest.builder()
                .name(item1.getName())
                .description(item1.getDescription())
                .available(item1.getAvailable())
                .build();

        itemRequestDto1 = ItemRequestDto.builder()
                .description(item1.getDescription())
                .requester(UserMapper.userMapToDto(requester101))
                .created(now)
                .build();

        commentDto = CommentDto.builder()
                .id(1)
                .created(now)
                .text("comment 1")
                .authorName(userForTest.getName())
                .build();

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getItemsByUserId_whenUserNotFoundInBD_returnException() {
        assertThrows(NotFoundException.class, () -> itemService.getAllItemsUser(1000));
    }

    @Test
    void commentToDto_whenCommentIsOk_returnCommentDto() {
        Comment comment = Comment.builder()
                .id(15)
                .author(booker)
                .created(now)
                .text("comment")
                .item(item1).build();
        CommentDto commentDto1 = CommentMapper.toDto(comment);
        assertEquals(comment.getId(), commentDto1.getId());
        assertEquals(comment.getText(), commentDto1.getText());
        assertEquals(comment.getAuthor().getName(), commentDto1.getAuthorName());
        assertEquals(comment.getCreated(), commentDto1.getCreated());
    }

    @Test
    void saveComment_whenUserNotFound_thenReturnNotFoundRecordInBD() {
        CommentDto commentDto = CommentDto.builder()
                .id(1)
                .text("comment 1")
                .authorName("name user for test 2")
                .created(now.minusDays(5))
                .build();
        assertThrows(NotFoundException.class, () -> itemService.addComment(1000, 1, commentDto));
    }

   /* @Test
    void saveComment_whenItemNotFound_thenReturnNotFoundRecordInDb() {
        User savedUser1 = userService.createUser(owner1);
        User savedUser2 = userService.createUser(userForTest);
        CommentDto commentDto = CommentDto.builder()
                .authorName(savedUser2.getName())
                .authorName("comment from user 1")
                .created(now)
                .build();
        Integer notFoundItemId = 1001;
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> itemService.addComment(notFoundItemId, savedUser1.getId(), commentDto));
        assertEquals("Инструмента id = 1001 не существует", ex.getMessage());
    }*/
}
