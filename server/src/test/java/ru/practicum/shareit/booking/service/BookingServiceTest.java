package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceTest {

    private UserService userService;
    private ItemService itemService;
    private BookingService bookingService;
    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;

    User user;
    User owner;
    Item item;
    BookingDto bookingDto;
    Booking booking;
    BookingDto currentBookingDto;
    Booking currentBooking;

    BookingDto pastBookingDto;
    Booking pastBooking;
    BookingDto futureBookingDto;
    Booking futureBooking;
    BookingDto waitingBookingDto;
    Booking waitingBooking;
    BookingDto rejectedBookingDto;
    Booking rejectedBooking;
    NewBookingRequestDto bookingRequestDto;
    Boolean approve;
    User booker;

    @BeforeEach
    void setUp() {
        bookingRepository = mock(BookingRepository.class);
        itemService = mock(ItemService.class);
        userService = mock(UserService.class);
        bookingService = new BookingServiceImpl(bookingRepository, userService, itemService);

        LocalDateTime now = LocalDateTime.now();

        user = User.builder()
                .id(1)
                .name("name1")
                .email("name1@yandex.ru")
                .build();

        owner = User.builder()
                .id(2)
                .name("name2")
                .email("name2@yandex.ru")
                .build();

        item = Item.builder()
                .id(1)
                .name("name3")
                .description("desc3")
                .ownerId(2)
                .available(true)
                .build();

        bookingDto = BookingDto.builder()
                .id(10)
                .item(ItemMapper.itemMapToDto(item))
                .booker(UserMapper.userMapToDto(user))
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .status(BookingStatus.WAITING)
                .build();

        bookingRequestDto = NewBookingRequestDto.builder()
                .itemId(item.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .build();

        booking = Booking.builder()
                .id(2)
                .item(item)
                .booker(user)
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .status(bookingDto.getStatus())
                .build();

        currentBookingDto = bookingDto.toBuilder()
                .id(2)
                .start(now.minusDays(1))
                .end(now.plusDays(1))
                .status(BookingStatus.APPROVED)
                .build();

        currentBooking = Booking.builder()
                .id(currentBookingDto.getId())
                .item(item)
                .booker(user)
                .start(currentBookingDto.getStart())
                .end(currentBookingDto.getEnd())
                .status(currentBookingDto.getStatus())
                .build();

        pastBookingDto = bookingDto.toBuilder()
                .id(3)
                .start(now.minusDays(1000))
                .end(now.minusDays(999))
                .status(BookingStatus.APPROVED)
                .build();

        pastBooking = Booking.builder()
                .id(pastBookingDto.getId())
                .item(item)
                .booker(user)
                .start(pastBookingDto.getStart())
                .end(pastBookingDto.getEnd())
                .status(pastBookingDto.getStatus())
                .build();

        futureBookingDto = bookingDto.toBuilder()
                .id(4)
                .start(now.minusDays(999))
                .end(now.minusDays(1000))
                .status(BookingStatus.APPROVED)
                .build();

        futureBooking = Booking.builder()
                .id(futureBookingDto.getId())
                .item(item)
                .booker(user)
                .start(futureBookingDto.getStart())
                .end(futureBookingDto.getEnd())
                .status(futureBookingDto.getStatus())
                .build();

        waitingBookingDto = bookingDto.toBuilder()
                .id(5)
                .start(now.plusDays(1))
                .end(now.minusDays(2))
                .status(BookingStatus.WAITING)
                .build();

        waitingBooking = Booking.builder()
                .id(waitingBookingDto.getId())
                .item(item)
                .booker(user)
                .start(waitingBookingDto.getStart())
                .end(waitingBookingDto.getEnd())
                .status(waitingBookingDto.getStatus())
                .build();

        rejectedBookingDto = bookingDto.toBuilder()
                .id(6)
                .start(now.plusDays(100))
                .end(now.plusDays(101))
                .status(BookingStatus.REJECTED)
                .build();

        rejectedBooking = Booking.builder()
                .id(rejectedBookingDto.getId())
                .item(item)
                .booker(user)
                .start(rejectedBookingDto.getStart())
                .end(rejectedBookingDto.getEnd())
                .status(rejectedBookingDto.getStatus())
                .build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void test_createBooking_returnSavedBookingDto() {
        when(userService.getUserById(anyInt())).thenReturn(user);
        when(itemService.getItemById(anyInt())).thenReturn(item);
        when(userService.getUserById(anyInt())).thenReturn(owner);

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBookingForResponse = bookingService.createBooking(user.getId(), bookingRequestDto);

        assertNotNull(savedBookingForResponse);
        assertEquals(bookingRequestDto.getStart(), savedBookingForResponse.getStart());
        assertEquals(bookingRequestDto.getEnd(), savedBookingForResponse.getEnd());
        assertEquals(bookingRequestDto.getItemId(), savedBookingForResponse.getItem().getId());
    }

    @Test
    void test_getWithStatusById_returnBookingForResponse() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        BookingDto outputBooking = bookingService.getBookingById(owner.getId(), booking.getId());

        assertEquals(booking.getId(), outputBooking.getId());
        assertEquals(booking.getBooker().getId(), outputBooking.getBooker().getId());
        assertEquals(booking.getItem().getName(), outputBooking.getItem().getName());
        assertEquals(booking.getStart(), outputBooking.getStart());
        assertEquals(booking.getEnd(), outputBooking.getEnd());
        assertEquals(booking.getStatus(), outputBooking.getStatus());
    }

    @Test
    void test_getWithStatusById_whenRequestByWrongUser() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.getBookingById(1000, booking.getId()));
    }

    @Test
    void test_approve() {
        BookingDto approvedBooking = bookingService.approve(booking.getId(), owner.getId(), true);

        assertThat(approvedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);

        Booking updatedBooking = bookingRepository.findById(booking.getId()).orElseThrow();
        assertThat(updatedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void test_getByUserId_returnAllBookings() {
        when(userService.getUserById(anyInt())).thenReturn(user);
        when(bookingRepository.getByBookerIdOrderByStartDesc(anyInt()))
                .thenReturn(List.of(booking));
        Collection<BookingDto> collection = bookingService.getAllBookingByUserId(user.getId(), "ALL");
        List<BookingDto> result = collection.stream().toList();

        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
        assertEquals(booking.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(booking.getItem().getName(), result.get(0).getItem().getName());
        assertEquals(booking.getStart(), result.get(0).getStart());
        assertEquals(booking.getEnd(), result.get(0).getEnd());
        assertEquals(booking.getStatus(), result.get(0).getStatus());
    }

    @Test
    void test_getByUserId_returnAllCurrentBookings() {
        when(userService.getUserById(anyInt())).thenReturn(user);
        when(bookingRepository.getByBookerIdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(anyInt(), any(), any()))
                .thenReturn(List.of(currentBooking));
        Collection<BookingDto> collection = bookingService.getAllBookingByUserId(user.getId(), "CURRENT");
        List<BookingDto> result = collection.stream().toList();

        assertEquals(1, result.size());
        assertEquals(currentBooking.getId(), result.get(0).getId());
        assertEquals(currentBooking.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(currentBooking.getItem().getName(), result.get(0).getItem().getName());
        assertEquals(currentBooking.getStart(), result.get(0).getStart());
        assertEquals(currentBooking.getEnd(), result.get(0).getEnd());
        assertEquals(currentBooking.getStatus(), result.get(0).getStatus());
    }

    @Test
    void test_getByUserId_returnAllPastBookings() {
        when(userService.getUserById(anyInt())).thenReturn(user);
        when(bookingRepository.getByBookerIdAndEndIsBeforeOrderByStartDesc(anyInt(), any()))
                .thenReturn(List.of(pastBooking));
        Collection<BookingDto> collection = bookingService.getAllBookingByUserId(user.getId(), "PAST");
        List<BookingDto> result = collection.stream().toList();

        assertEquals(1, result.size());
        assertEquals(pastBooking.getId(), result.get(0).getId());
        assertEquals(pastBooking.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(pastBooking.getItem().getName(), result.get(0).getItem().getName());
        assertEquals(pastBooking.getStart(), result.get(0).getStart());
        assertEquals(pastBooking.getEnd(), result.get(0).getEnd());
        assertEquals(pastBooking.getStatus(), result.get(0).getStatus());
    }

    @Test
    void test_getByUserId_returnAllFutureBookings() {
        when(userService.getUserById(anyInt())).thenReturn(user);
        when(bookingRepository.getByBookerIdAndStartIsAfterOrderByStartDesc(anyInt(), any()))
                .thenReturn(List.of(futureBooking));
        Collection<BookingDto> collection = bookingService.getAllBookingByUserId(user.getId(), "FUTURE");
        List<BookingDto> result = collection.stream().toList();

        assertEquals(1, result.size());
        assertEquals(futureBooking.getId(), result.get(0).getId());
        assertEquals(futureBooking.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(futureBooking.getItem().getName(), result.get(0).getItem().getName());
        assertEquals(futureBooking.getStart(), result.get(0).getStart());
        assertEquals(futureBooking.getEnd(), result.get(0).getEnd());
        assertEquals(futureBooking.getStatus(), result.get(0).getStatus());
    }

    @Test
    void test_getByUserId_returnAllWaitingBookings() {
        when(userService.getUserById(anyInt())).thenReturn(user);
        when(bookingRepository.getByBookerIdAndStartIsAfterAndStatusIsOrderByStartDesc(anyInt(), any(), any()))
                .thenReturn(List.of(waitingBooking));
        Collection<BookingDto> collection = bookingService.getAllBookingByUserId(user.getId(), "WAITING");
        List<BookingDto> result = collection.stream().toList();

        assertEquals(1, result.size());
        assertEquals(waitingBooking.getId(), result.get(0).getId());
        assertEquals(waitingBooking.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(waitingBooking.getItem().getName(), result.get(0).getItem().getName());
        assertEquals(waitingBooking.getStart(), result.get(0).getStart());
        assertEquals(waitingBooking.getEnd(), result.get(0).getEnd());
        assertEquals(waitingBooking.getStatus(), result.get(0).getStatus());
    }

    @Test
    void test_getByUserId_returnAllRejectedBookings() {
        when(userService.getUserById(anyInt())).thenReturn(user);
        when(bookingRepository.getByBookerIdAndStatusIsOrderByStartDesc(anyInt(), any()))
                .thenReturn(List.of(rejectedBooking));
        Collection<BookingDto> collection = bookingService.getAllBookingByUserId(user.getId(), "REJECTED");
        List<BookingDto> result = collection.stream().toList();

        assertEquals(1, result.size());
        assertEquals(rejectedBooking.getId(), result.get(0).getId());
        assertEquals(rejectedBooking.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(rejectedBooking.getItem().getName(), result.get(0).getItem().getName());
        assertEquals(rejectedBooking.getStart(), result.get(0).getStart());
        assertEquals(rejectedBooking.getEnd(), result.get(0).getEnd());
        assertEquals(rejectedBooking.getStatus(), result.get(0).getStatus());
    }

    @Test
    void test_getByOwnerId_returnAllBookings() {
        when(userService.getUserById(anyInt())).thenReturn(owner);
        when(bookingRepository.getByItemOwnerId(anyInt()))
                .thenReturn(List.of(booking));
        Collection<BookingDto> collection = bookingService.getAllBookingsByOwner(owner.getId(), "ALL");
        List<BookingDto> result = collection.stream().toList();

        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
        assertEquals(booking.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(booking.getItem().getName(), result.get(0).getItem().getName());
        assertEquals(booking.getStart(), result.get(0).getStart());
        assertEquals(booking.getEnd(), result.get(0).getEnd());
        assertEquals(booking.getStatus(), result.get(0).getStatus());
    }

    @Test
    void test_getByOwnerId_returnAllCurrentBookings() {
        when(userService.getUserById(anyInt())).thenReturn(owner);
        when(bookingRepository.getCurrentBookingsOwner(anyInt(), any()))
                .thenReturn(List.of(currentBooking));
        Collection<BookingDto> collection = bookingService.getAllBookingsByOwner(owner.getId(), "CURRENT");
        List<BookingDto> result = collection.stream().toList();

        assertEquals(1, result.size());
        assertEquals(currentBooking.getId(), result.get(0).getId());
        assertEquals(currentBooking.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(currentBooking.getItem().getName(), result.get(0).getItem().getName());
        assertEquals(currentBooking.getStart(), result.get(0).getStart());
        assertEquals(currentBooking.getEnd(), result.get(0).getEnd());
        assertEquals(currentBooking.getStatus(), result.get(0).getStatus());
    }

    @Test
    void test_getByOwnerId_returnAllPastBookings() {
        when(userService.getUserById(anyInt())).thenReturn(owner);
        when(bookingRepository.getPastBookingsOwner(anyInt(), any()))
                .thenReturn(List.of(pastBooking));
        Collection<BookingDto> collection = bookingService.getAllBookingsByOwner(owner.getId(), "PAST");
        List<BookingDto> result = collection.stream().toList();

        assertEquals(1, result.size());
        assertEquals(pastBooking.getId(), result.get(0).getId());
        assertEquals(pastBooking.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(pastBooking.getItem().getName(), result.get(0).getItem().getName());
        assertEquals(pastBooking.getStart(), result.get(0).getStart());
        assertEquals(pastBooking.getEnd(), result.get(0).getEnd());
        assertEquals(pastBooking.getStatus(), result.get(0).getStatus());
    }

    @Test
    void test_getByOwnerId_returnAllFutureBookings() {
        when(userService.getUserById(anyInt())).thenReturn(owner);
        when(bookingRepository.getFutureBookingsOwner(anyInt(), any()))
                .thenReturn(List.of(futureBooking));
        Collection<BookingDto> collection = bookingService.getAllBookingsByOwner(owner.getId(), "FUTURE");
        List<BookingDto> result = collection.stream().toList();

        assertEquals(1, result.size());
        assertEquals(futureBooking.getId(), result.get(0).getId());
        assertEquals(futureBooking.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(futureBooking.getItem().getName(), result.get(0).getItem().getName());
        assertEquals(futureBooking.getStart(), result.get(0).getStart());
        assertEquals(futureBooking.getEnd(), result.get(0).getEnd());
        assertEquals(futureBooking.getStatus(), result.get(0).getStatus());
    }

    @Test
    void test_getByOwnerId_returnAllWaitingBookings() {
        when(userService.getUserById(anyInt())).thenReturn(owner);
        when(bookingRepository.getWaitingBookingsOwner(anyInt(), any(), any()))
                .thenReturn(List.of(waitingBooking));
        Collection<BookingDto> collection = bookingService.getAllBookingsByOwner(owner.getId(), "WAITING");
        List<BookingDto> result = collection.stream().toList();

        assertEquals(1, result.size());
        assertEquals(waitingBooking.getId(), result.get(0).getId());
        assertEquals(waitingBooking.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(waitingBooking.getItem().getName(), result.get(0).getItem().getName());
        assertEquals(waitingBooking.getStart(), result.get(0).getStart());
        assertEquals(waitingBooking.getEnd(), result.get(0).getEnd());
        assertEquals(waitingBooking.getStatus(), result.get(0).getStatus());
    }

    @Test
    void test_getByOwnerId_returnAllRejectedBookings() {
        when(userService.getUserById(anyInt())).thenReturn(owner);
        when(bookingRepository.getRejectedBookingsOwner(anyInt(), any()))
                .thenReturn(List.of(rejectedBooking));
        Collection<BookingDto> collection = bookingService.getAllBookingsByOwner(owner.getId(), "REJECTED");
        List<BookingDto> result = collection.stream().toList();
        assertEquals(1, result.size());
        assertEquals(rejectedBooking.getId(), result.get(0).getId());
        assertEquals(rejectedBooking.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(rejectedBooking.getItem().getName(), result.get(0).getItem().getName());
        assertEquals(rejectedBooking.getStart(), result.get(0).getStart());
        assertEquals(rejectedBooking.getEnd(), result.get(0).getEnd());
        assertEquals(rejectedBooking.getStatus(), result.get(0).getStatus());
    }

    @Test
    void test_booking_returnBookingDto() {
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        assertEquals(booking.getItem().getId(), bookingDto.getId());
        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertEquals(booking.getBooker().getName(), bookingDto.getBooker().getName());
        assertEquals(booking.getBooker().getId(), bookingDto.getBooker().getId());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
    }
}