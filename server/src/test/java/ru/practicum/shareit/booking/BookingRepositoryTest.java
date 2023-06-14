package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@TestPropertySource(properties = {"db.name=test"})
public class BookingRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    Booking bookingToSave;
    Booking lastBooking;
    Booking nextBooking;
    User booker;
    User owner;
    Item itemToSave;
    Item item1;
    Item item2;

    @BeforeEach
    void setup() {

        booker = userRepository.save(new User(
                null,
                "user 1",
                "user1@email"));

        owner = userRepository.save(new User(
                null,
                "user 2",
                "user2@email"));

        itemToSave = new Item(
                "name1",
                "description1",
                true);
        itemToSave.setOwner(owner);
        item1 = itemRepository.save(itemToSave);

        itemToSave = new Item(
                "name2",
                "description2",
                true);
        itemToSave.setOwner(owner);
        item2 = itemRepository.save(itemToSave);

        bookingToSave = new Booking(
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                Status.APPROVED);
        bookingToSave.setBooker(booker);
        bookingToSave.setItem(item1);
        lastBooking = bookingRepository.save(bookingToSave);

        bookingToSave = new Booking(
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(1),
                Status.WAITING);
        bookingToSave.setBooker(booker);
        bookingToSave.setItem(item1);
        nextBooking = bookingRepository.save(bookingToSave);
    }

    @AfterEach
    void afterEach() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    @Test
    void findAllByBookerIdOrderByIdDesc_shouldReturnListOfBookings_whenFound() {
        List<Booking> actualBookings = bookingRepository
                .findAllByBookerIdIsOrderByIdDesc(booker.getId(), PageRequest.of(0, 10));

        assertEquals(2, actualBookings.size());
    }

    @Test
    void findAllByBookerIdOrderByIdDesc_shouldReturnEmptyList_whenNotFound() {
        List<Booking> actualBookings = bookingRepository
                .findAllByBookerIdIsOrderByIdDesc(owner.getId(), PageRequest.of(0, 10));

        assertTrue(actualBookings.isEmpty());
    }

    @Test
    void findAllRejected_shouldReturnListOfBookings_whenInvoked() {
        lastBooking.setStatus(Status.REJECTED);
        bookingRepository.save(lastBooking);

        List<Booking> actualBookings = bookingRepository.findAllRejectedBookings(booker.getId());
        assertEquals(1, actualBookings.size());
    }

    @Test
    void findAllWaiting_shouldReturnListOfBookings_whenInvoked() {
        List<Booking> actualBookings = bookingRepository.findAllWaitingBookings(booker.getId());
        assertEquals(1, actualBookings.size());
    }

    @Test
    void findAllFutureBookings_shouldReturnListOfBookings_whenInvoked() {
        List<Booking> actualBookings = bookingRepository.findAllFutureBookings(booker.getId(), LocalDateTime.now());
        assertEquals(1, actualBookings.size());
    }

    @Test
    void findAllPastBookings_shouldReturnListOfBookings_whenInvoked() {
        List<Booking> actualBookings = bookingRepository.findAllPastBookings(booker.getId(), LocalDateTime.now());
        assertEquals(1, actualBookings.size());
    }

    @Test
    void findAllCurrentBookings_shouldReturnListOfBookings_whenInvoked() {
        nextBooking.setStart(LocalDateTime.now().minusDays(1));
        bookingRepository.save(nextBooking);

        List<Booking> actualBookings = bookingRepository.findAllCurrentBookings(booker.getId(), LocalDateTime.now());
        assertEquals(1, actualBookings.size());
    }

    @Test
    void findAllOwnersBookings_shouldReturnListOfBookings_whenInvoked() {
        List<Booking> actualBookings = bookingRepository
                .findAllOwnerBookings(owner.getId(), PageRequest.of(0, 10));
        assertEquals(2, actualBookings.size());
    }

    @Test
    void findAllOwnersPastBookings_shouldReturnListOfBookings_whenInvoked() {
        List<Booking> actualBookings = bookingRepository
                .findAllOwnerPastBookings(owner.getId(), LocalDateTime.now());
        assertEquals(1, actualBookings.size());
    }

    @Test
    void findAllOwnersFutureBookings_shouldReturnListOfBookings_whenInvoked() {
        List<Booking> actualBookings = bookingRepository
                .findAllOwnerFutureBookings(owner.getId(), LocalDateTime.now());
        assertEquals(1, actualBookings.size());
    }

    @Test
    void findAllOwnersCurrentBookings_shouldReturnListOfBookings_whenInvoked() {
        nextBooking.setStart(LocalDateTime.now().minusDays(1));
        bookingRepository.save(nextBooking);

        List<Booking> actualBookings = bookingRepository
                .findAllOwnerCurrentBookings(owner.getId(), LocalDateTime.now());
        assertEquals(1, actualBookings.size());
    }

    @Test
    void findAllOwnersRejected_shouldReturnListOfBookings_whenInvoked() {
        lastBooking.setStatus(Status.REJECTED);
        bookingRepository.save(lastBooking);

        List<Booking> actualBookings = bookingRepository
                .findAllOwnerRejectedBookings(owner.getId());
        assertEquals(1, actualBookings.size());
    }

    @Test
    void findAllOwnersWaiting_shouldReturnListOfBookings_whenInvoked() {
        List<Booking> actualBookings = bookingRepository
                .findAllOwnerWaitingBookings(owner.getId());
        assertEquals(1, actualBookings.size());
    }


}
