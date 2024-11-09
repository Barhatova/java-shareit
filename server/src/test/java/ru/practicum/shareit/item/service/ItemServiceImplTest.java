package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.UpdateItemRequestDto;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("test")
class ItemServiceImplTest {

    ItemRequest itemRequest;
    ItemService itemService;
    UserRepository userRepository;
    ItemRepository itemRepository;
    CommentRepository commentRepository;
    LocalDateTime now = LocalDateTime.now();
    UserService userService;
    ItemDto itemDto;
    UpdateItemRequestDto updateItemRequestDto;
    Item item;
    User user;

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
    }

    @Test
    void test_getItemWithBookingAndComment() {
        CommentDto inputCommentDto = CommentDto.builder()
                .id(1)
                .text("new comment for test")
                .build();

        User owner2 = User.builder()
                .id(2)
                .name("name1")
                .email("name1@yandex.ru")
                .build();

        User userForTest2 = User.builder()
                .id(1)
                .name("name2")
                .email("name2@yandex.ru")
                .build();

        Item item1 = Item.builder()
                .id(1)
                .name("item1")
                .description("desc item1")
                .ownerId(owner2.getId())
                .build();

        Booking bookingFromBd = Booking.builder()
                .id(1)
                .item(item1)
                .booker(userForTest2)
                .start(now.minusDays(10))
                .end(now.minusDays(5))
                .build();

        Item item = Item.builder()
                .id(1)
                .name("name")
                .description("desc")
                .ownerId(owner2.getId())
                .available(true)
                .build();

        CommentDto commentDto = CommentDto.builder()
                .id(1)
                .text("text1")
                .authorName("name1")
                .created(now.minusDays(5))
                .build();

        Comment outputComment = Comment.builder()
                .id(1)
                .author(userForTest2)
                .text("text2")
                .item(item)
                .build();

        UserRepository userRepositoryJpa2 = mock(UserRepository.class);
        ItemRepository itemRepositoryJpa2 = mock(ItemRepository.class);
        CommentRepository commentRepository2 = mock(CommentRepository.class);

        when(userRepositoryJpa2.findById(any())).thenReturn(Optional.of(userForTest2));
        when(itemRepositoryJpa2.findById(any())).thenReturn(Optional.of(item));
        when(commentRepository2.save(any())).thenReturn(outputComment);
    }

    @Test
    void test_getItemById() {
        Integer itemId = 3;

        Assertions.assertThrows(RuntimeException.class,
                        () -> itemService.getItemById(itemId));
    }

    @Test
    void test_getOwnerById() {
        Integer ownerId = 3;

        Assertions.assertThrows(RuntimeException.class,
                () -> itemService.getOwnerId(ownerId));
    }
}
