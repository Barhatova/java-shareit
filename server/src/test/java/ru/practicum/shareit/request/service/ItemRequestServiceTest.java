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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceTest {
    private final ItemRequestService itemRequestService;
    private final UserService userService;
    private final EntityManager em;
    ItemRequest itemRequest1;
    UserDto ownerDto1;
    UserDto requesterDto101;
    User owner1;
    User requester101;
    LocalDateTime now;
    LocalDateTime nowPlus10min;
    LocalDateTime nowPlus10hours;
    Item item1;
    ItemRequestDto itemRequestDto1;
    TypedQuery<ItemRequest> query;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        nowPlus10min = now.plusMinutes(10);
        nowPlus10hours = now.plusHours(10);

        ownerDto1 = UserDto.builder()
                .name("name userDto1")
                .email("userDto1@mans.gf")
                .build();

        requesterDto101 = UserDto.builder()
                .name("name userDto2")
                .email("userDto2@mans.gf")
                .build();

        owner1 = User.builder()
                .id(ownerDto1.getId())
                .name(ownerDto1.getName())
                .email(ownerDto1.getEmail())
                .build();

        requester101 = User.builder()
                .id(requesterDto101.getId())
                .name(requesterDto101.getName())
                .email(requesterDto101.getEmail())
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

        itemRequestDto1 = ItemRequestDto.builder()
                .description(item1.getDescription())
                .requester(requesterDto101)
                .created(now)
                .build();
    }

   /* @Test
    void createRequest() {
        User savedOwnerDto1 = userService.createUser(owner1);
        query = em.createQuery("Select ir from ItemRequest ir", ItemRequest.class);
        List<ItemRequest> beforeSave = query.getResultList();

        assertEquals(0, beforeSave.size());

        ItemRequestDto savedItemRequest =
                itemRequestService.createRequest(itemRequestDto1, savedOwnerDto1.getId());
        List<ItemRequest> afterSave = query.getResultList();

        assertEquals(1, afterSave.size());
        assertEquals(savedItemRequest.getId(), afterSave.get(0).getId());
        assertEquals(savedItemRequest.getCreated(), afterSave.get(0).getCreated());
        assertEquals(savedItemRequest.getDescription(), afterSave.get(0).getDescription());
    }

    @Test
    void addItemRequest_whenRequesterIdIsNull_returnNotFoundRecordInBD() {
        Integer requesterId = 1001;
        assertThrows(NotFoundException.class,
                () -> itemRequestService.createRequest(itemRequestDto1, requesterId));
    }

    @Test
    void getItemRequestsByUserId() {
        User savedUserDto = userService.createUser(requester101);
        ItemRequestDto savedItemRequest =
                itemRequestService.createRequest(itemRequestDto1, savedUserDto.getId());

        query = em.createQuery("Select ir from ItemRequest ir", ItemRequest.class);

        Collection<ItemRequestDto> collection =
                itemRequestService.getUserRequests(savedUserDto.getId());
        List<ItemRequestDto> itemsFromDb = collection.stream().toList();
        assertEquals(1, itemsFromDb.size());
        assertEquals(savedItemRequest.getId(), itemsFromDb.get(0).getId());
        assertEquals(savedItemRequest.getRequester().getId(), itemsFromDb.get(0).getRequester().getId());
        assertEquals(savedItemRequest.getRequester().getName(), itemsFromDb.get(0).getRequester().getName());
        assertEquals(savedItemRequest.getCreated(), itemsFromDb.get(0).getCreated());
        assertEquals(itemRequestDto1.getDescription(), itemsFromDb.get(0).getDescription());
    }*/

    @Test
    void getItemRequestsByUserId_whenUserNotFound_returnNotFoundRecordInDb() {
        Integer requesterId = 1001;
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> itemRequestService.getUserRequests(requesterId));
        assertEquals(("Пользователя с таким id не существует"), ex.getMessage());
    }

    @Test
    void getAllRequestForSee_whenRequesterNotFound_returnNotFoundRecordInDb() {
        Integer requesterId = 1001;
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> itemRequestService.getUserRequests(requesterId));
        assertEquals(("Пользователя с таким id не существует"), ex.getMessage());
    }


  /*  @Test
    void getItemRequestById_whenAllIsOk_returnItemRequestDtoWithAnswers() {
        User savedRequesterDto = userService.createUser(requester101);
        User savedOwnerDto = userService.createUser(owner1);
        User observer = userService.createUser(User.builder().name("nablyudatel").email("1@re.hg").build());

        ItemRequestDto savedItRequest =
                itemRequestService.createRequest(itemRequestDto1, savedRequesterDto.getId());

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
        //Дл юзера 3.
        ItemRequestDto itReqDtoWithAnswerForRequester =
                itemRequestService.getRequestById(savedRequesterDto.getId(), savedItRequest.getId());

        assertEquals(savedItRequest.getId(), itReqDtoWithAnswerForRequester.getId());
        assertEquals(savedItRequest.getCreated(), itReqDtoWithAnswerForRequester.getCreated());
        assertEquals(savedItRequest.getDescription(), itReqDtoWithAnswerForRequester.getDescription());
    }*/

    @Test
    void getItemRequestById_whenRequestNotFound_returnNotFoundRecordInBD() {
        User savedRequesterDto = userService.createUser(requester101);
        Integer requestId = 1001;
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestById(savedRequesterDto.getId(), requestId));
        assertEquals("Запрос на аренду с id = 1001 не найден",
                ex.getMessage());
    }

    @Test
    void getItemRequestById_whenUserNotFound_returnNotFoundRecordInBD() {
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestById(1001, 1));
        assertEquals("Запрос на аренду с id = 1 не найден",
                ex.getMessage());
    }
}
