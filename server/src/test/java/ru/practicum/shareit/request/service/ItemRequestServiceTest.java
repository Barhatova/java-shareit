package ru.practicum.shareit.request.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceTest {
    private final ItemRequestService itemRequestService;
    private final UserService userService;
    private final EntityManager em;
    ItemRequest itemRequest;
    UserDto ownerDto;
    UserDto requesterDto;
    User owner;
    User requester;
    LocalDateTime now;
    LocalDateTime nowPlus10min;
    LocalDateTime nowPlus10hours;
    Item item1;
    ItemRequestDto itemRequestDto;
    TypedQuery<ItemRequest> query;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        nowPlus10min = now.plusMinutes(10);
        nowPlus10hours = now.plusHours(10);

        ownerDto = UserDto.builder()
                .name("name1")
                .email("name1@yandex.ru")
                .build();

        requesterDto = UserDto.builder()
                .name("name2")
                .email("name2@yandex.ru")
                .build();

        owner = User.builder()
                .id(ownerDto.getId())
                .name(ownerDto.getName())
                .email(ownerDto.getEmail())
                .build();

        requester = User.builder()
                .id(requesterDto.getId())
                .name(requesterDto.getName())
                .email(requesterDto.getEmail())
                .build();

        itemRequest = ItemRequest.builder()
                .description("desc")
                .creator(requester)
                .created(now)
                .build();

        item1 = Item.builder()
                .name("name1")
                .description("desc1")
                .ownerId(owner.getId())
                .available(true)
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .description(item1.getDescription())
                .requestor(requesterDto)
                .created(now)
                .build();
    }

    @Test
    void test_createItemRequest() {
        User savedOwnerDto1 = userService.createUser(owner);
        query = em.createQuery("Select ir from ItemRequest ir", ItemRequest.class);
        List<ItemRequest> beforeSave = query.getResultList();

        assertEquals(0, beforeSave.size());

        ItemRequestDto savedItemRequest =
                itemRequestService.createItemRequest(savedOwnerDto1.getId(), itemRequestDto);
        List<ItemRequest> afterSave = query.getResultList();

        assertEquals(1, afterSave.size());
        assertEquals(savedItemRequest.getId(), afterSave.get(0).getId());
        assertEquals(savedItemRequest.getCreated(), afterSave.get(0).getCreated());
        assertEquals(savedItemRequest.getDescription(), afterSave.get(0).getDescription());
    }

    @Test
    void test_createItemRequest_returnNotFoundRecordInBD() {
        Integer requesterId = 1001;
        assertThrows(NotFoundException.class,
                () -> itemRequestService.createItemRequest(requesterId, itemRequestDto));
    }

    @Test
    void test_getItemRequestsByUserId() {
        User savedUserDto = userService.createUser(requester);
        ItemRequestDto savedItemRequest =
                itemRequestService.createItemRequest(savedUserDto.getId(), itemRequestDto);

        query = em.createQuery("Select ir from ItemRequest ir", ItemRequest.class);

        Collection<ItemRequestDto> collection =
                itemRequestService.getUserRequests(savedUserDto.getId());
        List<ItemRequestDto> itemsFromDb = collection.stream().toList();

        assertEquals(1, itemsFromDb.size());

        assertEquals(savedItemRequest.getId(), itemsFromDb.get(0).getId());
        assertEquals(savedItemRequest.getRequestor().getId(), itemsFromDb.get(0).getRequestor().getId());
        assertEquals(savedItemRequest.getRequestor().getName(), itemsFromDb.get(0).getRequestor().getName());
        assertEquals(savedItemRequest.getCreated(), itemsFromDb.get(0).getCreated());
        assertEquals(itemRequestDto.getDescription(), itemsFromDb.get(0).getDescription());
    }

    @Test
    void test_getItemRequestsByUserId_returnNotFoundRecordInDb() {
        Integer requesterId = 1001;
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> itemRequestService.getUserRequests(requesterId));
        assertEquals(("Пользователя с таким id не существует"), ex.getMessage());
    }

    @Test
    void test_getAllRequestForSee_returnNotFoundRecordInDb() {
        Integer requesterId = 1001;
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> itemRequestService.getUserRequests(requesterId));
        assertEquals(("Пользователя с таким id не существует"), ex.getMessage());
    }


    @Test
    void test_getItemRequestById_returnItemRequestDtoWithAnswers() {
        User savedRequesterDto = userService.createUser(requester);
        User savedOwnerDto = userService.createUser(owner);
        User observer = userService.createUser(User.builder().name("name3").email("name3@yandex.ru").build());

        ItemRequestDto savedItRequest =
                itemRequestService.createItemRequest(savedRequesterDto.getId(), itemRequestDto);

        ItemRequestDto itRequestDtoFromDbObserver =
                itemRequestService.getRequestById(observer.getId(), savedItRequest.getId());

        assertEquals(savedItRequest.getId(), itRequestDtoFromDbObserver.getId());
        assertEquals(savedItRequest.getCreated(), itRequestDtoFromDbObserver.getCreated());
        assertEquals(savedItRequest.getDescription(), itRequestDtoFromDbObserver.getDescription());

        ItemRequestDto itemRequestDtoWithAnswerForOwner =
                itemRequestService.getRequestById(savedOwnerDto.getId(), savedItRequest.getId());

        assertEquals(savedItRequest.getId(), itemRequestDtoWithAnswerForOwner.getId());
        assertEquals(savedItRequest.getCreated(), itemRequestDtoWithAnswerForOwner.getCreated());
        assertEquals(savedItRequest.getDescription(), itemRequestDtoWithAnswerForOwner.getDescription());

        ItemRequestDto itReqDtoWithAnswerForRequester =
                itemRequestService.getRequestById(savedRequesterDto.getId(), savedItRequest.getId());

        assertEquals(savedItRequest.getId(), itReqDtoWithAnswerForRequester.getId());
        assertEquals(savedItRequest.getCreated(), itReqDtoWithAnswerForRequester.getCreated());
        assertEquals(savedItRequest.getDescription(), itReqDtoWithAnswerForRequester.getDescription());
    }

    @Test
    void test_getItemRequestById_returnNotFoundRecordInBD() {
        User savedRequesterDto = userService.createUser(requester);
        Integer requestId = 1001;
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestById(savedRequesterDto.getId(), requestId));
        assertEquals("Запрос на аренду с id = 1001 не найден", ex.getMessage());
    }

    @Test
    void test_getItemRequestById_returnNotFoundRecord() {
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestById(1001, 1));
        assertEquals("Запрос на аренду с id = 1 не найден",
                ex.getMessage());
    }
}
