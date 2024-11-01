package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookingRepositoryTest {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private final User booker = User.builder()
            .name("name")
            .email("name@yandex.ru")
            .build();

    private final User owner = User.builder()
            .name("name1")
            .email("name1@yandex.ru")
            .build();

    private final Item item = Item.builder()
            .name("name3")
            .description("desc")
            .available(true)
            .ownerId(1)
            .build();

    private final Booking booking = Booking.builder()
            .item(item)
            .booker(booker)
            .status(BookingStatus.APPROVED)
            .start(LocalDateTime.now().minusHours(1))
            .end(LocalDateTime.now().plusDays(1))
            .build();

    private final Booking pastBooking = Booking.builder()
            .item(item)
            .booker(booker)
            .status(BookingStatus.APPROVED)
            .start(LocalDateTime.now().minusDays(2))
            .end(LocalDateTime.now().minusDays(1))
            .build();

    private final Booking futureBooking = Booking.builder()
            .item(item)
            .booker(booker)
            .status(BookingStatus.APPROVED)
            .start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now().plusDays(2))
            .build();

    private final Booking currentBooking = Booking.builder()
            .item(item)
            .booker(booker)
            .status(BookingStatus.APPROVED)
            .start(LocalDateTime.now().minusHours(1))
            .end(LocalDateTime.now().plusDays(1))
            .build();

    @BeforeEach
    public void init() {
        testEntityManager.persist(booker);
        testEntityManager.persist(owner);
        testEntityManager.persist(item);
        testEntityManager.flush();
        bookingRepository.save(booking);
        bookingRepository.save(pastBooking);
        bookingRepository.save(futureBooking);
    }

    @AfterEach
    public void test_deleteAll() {
        bookingRepository.deleteAll();
    }

    @Test
    void test_getByBookerIdOrderByStartDesc() {
        Collection<Booking> bookings = bookingRepository.getByBookerIdOrderByStartDesc(booker.getId());
        assertEquals(bookings.size(), 3);
    }

    @Test
    void test_getByBookerIdAndEndIsBeforeOrderByStartDesc() {
        Collection<Booking> bookings = bookingRepository
                .getByBookerIdAndEndIsBeforeOrderByStartDesc(booker.getId(), LocalDateTime.now());
        assertEquals(bookings.size(), 1);
    }

    @Test
    void test_getByBookerIdAndStatusIsOrderByStartDesc() {
        Collection<Booking> bookings = bookingRepository
                .getByBookerIdAndStatusIsOrderByStartDesc(booker.getId(), BookingStatus.APPROVED);
        assertEquals(bookings.size(), 3);

    }

    @Test
    void test_getByBooker_getByBookerIdAndStartIsAfterOrderByStartDesc() {
        Collection<Booking> bookings = bookingRepository.getByBookerIdAndStartIsAfterOrderByStartDesc(booker.getId(),
                LocalDateTime.now());
        assertEquals(1, bookings.size());
    }

    @Test
    void test_getByBooker_getByBookerIdAndEndIsBeforeOrderByStartDesc() {
        Collection<Booking> bookings = bookingRepository.getByBookerIdAndEndIsBeforeOrderByStartDesc(booker.getId(),
                LocalDateTime.now());
        assertEquals(1, bookings.size());
    }

    @Test
    void test_getByBooker_getByItemOwnerId() {
        Collection<Booking> bookings = bookingRepository.getByItemOwnerId(item.getOwnerId());
        assertEquals(3, bookings.size());
    }

    @Test
    void test_getByBooker_getBookingsItem() {
        Collection<Booking> bookings = bookingRepository.getBookingsItem(item.getId());
        assertEquals(3, bookings.size());
    }
}
