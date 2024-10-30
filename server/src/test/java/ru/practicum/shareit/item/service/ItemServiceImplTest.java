package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

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

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
    }

    @Test
    void getItemWithBookingAndComment() {
        CommentDto inputCommentDto = CommentDto.builder().id(1).text("new comment for test").build();

        User owner2 = User.builder()
                .id(2)
                .name("name for owner")
                .email("owner2@aadmf.wreew")
                .build();

        User userForTest2 = User.builder()
                .id(1)
                .name("name user for test 2")
                .email("userForTest2@ahd.ew")
                .build();

        Item zaglushka = Item.builder().id(1).name("zaglushka").description("desc item zaglushka")
                .ownerId(owner2.getId()).build();

        Booking bookingFromBd = Booking.builder()
                .id(1)
                .item(zaglushka)
                .booker(userForTest2)
                .start(now.minusDays(10))
                .end(now.minusDays(5))
                .build();///

        Item itemFromBd = Item.builder()
                .id(1)
                .name("name for item")
                .description("desc for item")
                .ownerId(owner2.getId())
                .available(true)
                .build();

        CommentDto commentDto = CommentDto.builder()
                .id(1)
                .text("comment 1")
                .authorName("name user for test 2")
                .created(now.minusDays(5))
                .build();

        Comment outputComment = Comment.builder()
                .id(1)
                .author(userForTest2)
                .text("comment 1")
                .item(itemFromBd)
                .build();

        UserRepository userRepositoryJpa2 = mock(UserRepository.class);
        ItemRepository itemRepositoryJpa2 = mock(ItemRepository.class);
        CommentRepository commentRepository2 = mock(CommentRepository.class);

        when(userRepositoryJpa2.findById(any())).thenReturn(Optional.of(userForTest2));
        when(commentRepository2.save(any())).thenReturn(outputComment);
    }
}
