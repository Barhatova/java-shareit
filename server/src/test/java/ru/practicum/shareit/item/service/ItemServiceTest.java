package ru.practicum.shareit.item.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
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
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.UpdateItemRequestDto;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceTest {
    private final CommentRepository commentRepository;
    private final ItemRequestService itemRequestService;
    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;
    private final EntityManager em;
    ItemRequest itemReq;
    UserDto ownerDto;
    User owner;
    UserDto requesterDto;
    User requester;
    UserDto bookerDto;
    User booker;
    UserDto userDto;
    User userForTest;
    LocalDateTime now;
    LocalDateTime nowPlus10min;
    LocalDateTime nowPlus10hours;
    Item item;
    ItemDto itemDto;
    ItemRequestDto itemRequestDto;
    CommentDto commentDto;
    UpdateItemRequestDto updateItemRequestDto;

    Booking bookingFromBd;
    TypedQuery<Item> query;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        nowPlus10min = now.plusMinutes(10);
        nowPlus10hours = now.plusHours(10);

        ownerDto = UserDto.builder()
                .name("name ownerDto1")
                .email("ownerDto1@mans.gf")
                .build();

        owner = User.builder()
                .id(ownerDto.getId())
                .name(ownerDto.getName())
                .email(ownerDto.getEmail())
                .build();

        requesterDto = UserDto.builder()
                .name("name requesterDto101")
                .email("requesterDto101@mans.gf")
                .build();

        requester = User.builder()
                .id(requesterDto.getId())
                .name(requesterDto.getName())
                .email(requesterDto.getEmail())
                .build();

        userDto = UserDto.builder()
                .name("name userDtoForTest")
                .email("userDtoForTest@userDtoForTest.zx")
                .build();

        userForTest = User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();

        bookerDto = UserDto.builder()
                .name("booker")
                .email("booker@wa.dzd")
                .build();

        booker = User.builder()
                .name(bookerDto.getName())
                .email(bookerDto.getEmail())
                .build();

        itemReq = ItemRequest.builder()
                .description("description for request 1")
                .creator(requester)
                .created(now)
                .build();

        item = Item.builder()
                .name("name for item 1")
                .description("description for item 1")
                .ownerId(owner.getId())
                .available(true)
                .build();

        itemDto = ItemDto.builder()
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();

        updateItemRequestDto = UpdateItemRequestDto.builder()
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .description(item.getDescription())
                .requestor(UserMapper.userMapToDto(requester))
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
    void test_createItem_returnSavedItemDto() {
        User savedOwnerDto1 = userService.createUser(owner);

        query = em.createQuery("Select i from Item i", Item.class);
        List<Item> beforeSave = query.getResultList();

        assertEquals(0, beforeSave.size());

        ItemDto savedItemDto = itemService.createItem(savedOwnerDto1.getId(), itemDto);
        List<Item> afterSave = query.getResultList();

        assertEquals(1, afterSave.size());
        assertEquals(savedItemDto.getId(), afterSave.get(0).getId());
        assertEquals(savedItemDto.getRequestId(), afterSave.get(0).getItemRequest());
        assertEquals(savedItemDto.getDescription(), afterSave.get(0).getDescription());
        assertEquals(savedItemDto.getName(), afterSave.get(0).getName());
    }

    @Test
    void test_createItemRequest_returnNotFoundRecordInBD() {
        Integer requesterId = 1001;
        assertThrows(NotFoundException.class,
                () -> itemRequestService.createItemRequest(requesterId, itemRequestDto));
    }

    @Test
    void test_createItem_returnNotFoundException() {
        assertThrows(NotFoundException.class, () -> itemService.createItem(10000, itemDto));
    }

    @Test
    void test_getItemsByUserId_returnItemDtoList() {
        User savedOwnerDto1 = userService.createUser(owner);
        ItemDto savedItemDto = itemService.createItem(savedOwnerDto1.getId(), itemDto);
        Collection<ItemDto> collection = itemService.getAllItemsUser(savedOwnerDto1.getId());
        List<ItemDto> itemDtos = collection.stream().toList();

        assertEquals(1, itemDtos.size());
        assertEquals(savedItemDto.getId(), itemDtos.get(0).getId());
        assertEquals(savedItemDto.getName(), itemDtos.get(0).getName());
        assertEquals(savedItemDto.getDescription(), itemDtos.get(0).getDescription());
        assertEquals(savedItemDto.getRequestId(), itemDtos.get(0).getRequestId());
        assertEquals(savedItemDto.getAvailable(), itemDtos.get(0).getAvailable());
    }

    @Test
    void test_getItemsByUserId_returnException() {
        assertThrows(NotFoundException.class, () -> itemService.getAllItemsUser(1000));
    }

    @Test
    void test_updateInStorage_returnItemFromDb() {
        User savedOwnerDto1 = userService.createUser(owner);
        ItemDto savedItemDtoBeforeUpd = itemService.createItem(savedOwnerDto1.getId(), itemDto);

        Collection<ItemDto> collection = itemService.getAllItemsUser(savedOwnerDto1.getId());
        List<ItemDto> itemDtos = collection.stream().toList();

        assertEquals(1, itemDtos.size());
        assertEquals(savedItemDtoBeforeUpd.getId(), itemDtos.get(0).getId());
        assertEquals(savedItemDtoBeforeUpd.getName(), itemDtos.get(0).getName());
        assertEquals(savedItemDtoBeforeUpd.getDescription(), itemDtos.get(0).getDescription());
        assertEquals(savedItemDtoBeforeUpd.getRequestId(), itemDtos.get(0).getRequestId());
        assertEquals(savedItemDtoBeforeUpd.getAvailable(), itemDtos.get(0).getAvailable());

        UpdateItemRequestDto updatedItem = UpdateItemRequestDto.builder()
                .name("new name")
                .description("new description")
                .available(true)
                .build();

        ItemDto savedUpdItem =
                itemService.updateItem(savedItemDtoBeforeUpd.getId(), savedOwnerDto1.getId(), updatedItem);

        assertNotEquals(savedItemDtoBeforeUpd.getName(), savedUpdItem.getName());
        assertNotEquals(savedItemDtoBeforeUpd.getDescription(), savedUpdItem.getDescription());
        assertEquals(savedItemDtoBeforeUpd.getId(), savedUpdItem.getId());
        assertEquals(savedItemDtoBeforeUpd.getAvailable(), savedUpdItem.getAvailable());
    }

    @Test
    void test_getItemById_returnItemFromDb() {
        User savedOwnerDto1 = userService.createUser(owner);
        ItemDto savedItemDtoBeforeUpd = itemService.createItem(savedOwnerDto1.getId(), itemDto);
        Item itemDtoFromBd = itemService.getItemById(savedItemDtoBeforeUpd.getId());

        assertEquals(savedItemDtoBeforeUpd.getId(), itemDtoFromBd.getId());
        assertEquals(savedItemDtoBeforeUpd.getName(), itemDtoFromBd.getName());
        assertEquals(savedItemDtoBeforeUpd.getDescription(), itemDtoFromBd.getDescription());

        assertEquals(savedItemDtoBeforeUpd.getAvailable(), itemDtoFromBd.getAvailable());
    }

    @Test
    void test_SearchItemsByText() {
        User savedOwnerDto1 = userService.createUser(owner);
        ItemDto savedItemDto01 = itemService.createItem(savedOwnerDto1.getId(), itemDto);

        User savedRequester = userService.createUser(requester);
        ItemDto itemDto02 = itemDto.toBuilder().name("new item").description("new description").build();

        ItemDto savedItemDto02 = itemService.createItem(savedOwnerDto1.getId(), itemDto02);

        Collection<ItemDto> collection = itemService.getByText("nEw");
        List<ItemDto> itemDtoList = collection.stream().toList();

        assertNotNull(itemDtoList);
        assertEquals(1, itemDtoList.size());
        assertEquals(itemDto02.getDescription(), itemDtoList.stream().findFirst().get().getDescription());
    }

    @Test
    void test_commentToDto_returnCommentDto() {
        Comment comment = Comment.builder()
                .id(0)
                .author(booker)
                .created(now)
                .text("comment")
                .item(item).build();
        CommentDto commentDto1 = CommentMapper.toDto(comment);
        assertEquals(comment.getId(), commentDto1.getId());
        assertEquals(comment.getText(), commentDto1.getText());
        assertEquals(comment.getAuthor().getName(), commentDto1.getAuthorName());
        assertEquals(comment.getCreated(), commentDto1.getCreated());
    }

    @Test
    void test_saveComment_thenReturnNotFoundRecordInBD() {
        CommentDto commentDto = CommentDto.builder()
                .id(1)
                .text("comment 1")
                .authorName("name user for test 2")
                .created(now.minusDays(5))
                .build();

        assertThrows(NotFoundException.class, () -> itemService.addComment(1000, 1, commentDto));
    }

    @Test
    void test_saveComment_thenReturnComment() {
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
                .build();
        List<Booking> list = new ArrayList<>();
        list.add(bookingFromBd);

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

        UserService userService1 = mock(UserService.class);
        ItemRepository itemRepository = mock(ItemRepository.class);
        CommentRepository commentRepository2 = mock(CommentRepository.class);
        BookingRepository bookingRepository2 = mock(BookingRepository.class);
        ItemRequestService itemRequestService = mock(ItemRequestService.class);

        ItemService itemService2 = new ItemServiceImpl(itemRepository, userService1, bookingRepository2, commentRepository2, itemRequestService);

        when(userService1.getUserById(anyInt()))
                .thenReturn(userForTest2);
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(itemFromBd));
        when(commentRepository2.save(any()))
                .thenReturn(outputComment);
        when(bookingRepository2.getByItemIdAndBookerIdAndStatusIsAndEndIsBefore(any(), any(), any(), any())).thenReturn(list);

        CommentDto outputCommentDto =
                itemService2.addComment(itemFromBd.getId(), userForTest2.getId(), inputCommentDto);

        assertEquals(commentDto.getText(), outputCommentDto.getText());
        assertEquals(commentDto.getAuthorName(), outputCommentDto.getAuthorName());
        assertEquals(commentDto.getId(), outputCommentDto.getId());
        assertNotEquals(commentDto.getCreated(), outputCommentDto.getCreated());
    }
}
