package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exeption.BadRequestException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.UserVerificationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {"db.name=test"})
public class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserService userService;
    @Mock
    private ItemService itemService;
    @InjectMocks
    private BookingServiceImpl bookingService;

    Booking bookingToSave;
    Booking bookingToReturn;
    Item item;
    User booker;
    User owner;
    BookingDto bookingDto;

    @BeforeEach
    void setup() {
        owner = new User(
                1L,
                "name",
                "email@yandex.ru");

        booker = new User(
                2L,
                "name",
                "email1@yandex.ru");

        item = new Item(
                "name1",
                "description1",
                true);
        item.setId(1L);
        item.setOwner(owner);

        bookingDto = new BookingDto(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                1L,
                null,
                null,
                null
        );

        bookingToSave = new Booking();
        bookingToSave.setId(1L);
        bookingToSave.setItem(item);
        bookingToSave.setBooker(booker);
        bookingToSave.setStatus(Status.WAITING);

        bookingToReturn = new Booking();
        bookingToReturn.setId(1L);
        bookingToReturn.setItem(item);
        bookingToReturn.setBooker(booker);
    }

    @Test
    void createNewBooking_shouldReturnBooking_whenInvoked() {
        Mockito.when(itemService.getItemIfExist(anyLong()))
                .thenReturn(item);
        Mockito.when(userService.getUserIfExist(anyLong()))
                .thenReturn(booker);
        Mockito.when(bookingRepository.save(any(Booking.class)))
                .thenReturn(bookingToReturn);

        BookingDto savedBooking = bookingService.saveBooking(2L, bookingDto);

        assertEquals(1L, savedBooking.getId());
        verify(bookingRepository, atMostOnce()).save(any(Booking.class));
    }

    @Test
    void createNewBooking_shouldThrowsUserVerificationException_whenBookerIsOwner() {
        Mockito.when(itemService.getItemIfExist(anyLong()))
                .thenReturn(item);
        Mockito.when(userService.getUserIfExist(anyLong()))
                .thenReturn(owner);

        assertThrows(
                UserVerificationException.class,
                () -> bookingService.saveBooking(1L, bookingDto)
        );
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createNewBooking_shouldThrowsBadRequestException_whenItemIsNotAvailable() {
        item.setAvailable(false);

        Mockito.when(itemService.getItemIfExist(anyLong()))
                .thenReturn(item);
        Mockito.when(userService.getUserIfExist(anyLong()))
                .thenReturn(booker);

        assertThrows(
                BadRequestException.class,
                () -> bookingService.saveBooking(2L, bookingDto)
        );
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void approveBooking_shouldReturnApprovedBooking_whenApproved() {
        Mockito.when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(bookingToSave));

        Mockito.when(userService.getUserIfExist(1L))
                .thenReturn(owner);

        Mockito.when(itemService.getItemIfExist(anyLong()))
                .thenReturn(item);

        bookingToReturn.setStatus(Status.APPROVED);

        Mockito.when(bookingRepository.save(bookingToSave))
                .thenReturn(bookingToReturn);

        BookingDto approvedBooking = bookingService.approveBooking(1L, 1L, true);

        assertEquals(Status.APPROVED, approvedBooking.getStatus());
        verify(bookingRepository, atMostOnce()).save(any(Booking.class));
    }

    @Test
    void approveBooking_shouldReturnRejectedBooking_whenRejected() {

        Mockito.when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(bookingToSave));

        Mockito.when(userService.getUserIfExist(1L))
                .thenReturn(owner);

        Mockito.when(itemService.getItemIfExist(anyLong()))
                .thenReturn(item);

        bookingToReturn.setStatus(Status.REJECTED);

        Mockito.when(bookingRepository.save(bookingToSave))
                .thenReturn(bookingToReturn);

        BookingDto approvedBooking = bookingService.approveBooking(1L, 1L, false);

        assertEquals(Status.REJECTED, approvedBooking.getStatus());
        verify(bookingRepository, atMostOnce()).save(any(Booking.class));
    }

    @Test
    void approveBooking_shouldThrowsBadRequestException_whenAlreadyRejectedOrApproved() {
        bookingToSave.setStatus(Status.REJECTED);

        Mockito.when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(bookingToSave));

        Mockito.when(userService.getUserIfExist(1L))
                .thenReturn(owner);

        Mockito.when(itemService.getItemIfExist(anyLong()))
                .thenReturn(item);

        assertThrows(
                BadRequestException.class,
                () -> bookingService.approveBooking(1L, 1L, false)
        );
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void getBooking_shouldReturnBookingDto_whenFound() {
        Mockito.when(userService.getUserIfExist(1L))
                .thenReturn(owner);
        Mockito.when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(bookingToSave));

        BookingDto actualBookingDto = bookingService.getBookingById(1L, 1L);

        assertEquals(1L, actualBookingDto.getId());
    }

    @Test
    void getBooking_shouldThrowsNotFoundException_whenNotFound() {
        Mockito.when(userService.getUserIfExist(1L))
                .thenReturn(owner);
        Mockito.when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> bookingService.getBookingById(1L, 1L)
        );
    }

    @Test
    void getUserBookings_shouldReturnListOfBookings_whenInvoked() {
        List<BookingDto> actualDto;

        Mockito.when(userService.getUserIfExist(2L))
                .thenReturn(booker);
        Mockito.when(bookingRepository.findAllByBookerIdIsOrderByIdDesc(any(), any()))
                .thenReturn(List.of(bookingToReturn));

        actualDto = bookingService.getUserBookings(2L, "ALL", 1L, 1);
        assertEquals(1, actualDto.size());

        Mockito.when(bookingRepository.findAllPastBookings(any(), any()))
                .thenReturn(List.of(bookingToReturn));
        actualDto = bookingService.getUserBookings(2L, "PAST", 1L, 1);
        assertEquals(1, actualDto.size());

        Mockito.when(bookingRepository.findAllFutureBookings(any(), any()))
                .thenReturn(List.of(bookingToReturn));
        actualDto = bookingService.getUserBookings(2L, "FUTURE", 1L, 1);
        assertEquals(1, actualDto.size());

        Mockito.when(bookingRepository.findAllCurrentBookings(any(), any()))
                .thenReturn(List.of(bookingToReturn));
        actualDto = bookingService.getUserBookings(2L, "CURRENT", 1L, 1);
        assertEquals(1, actualDto.size());

        Mockito.when(bookingRepository.findAllWaitingBookings(any()))
                .thenReturn(List.of(bookingToReturn));
        actualDto = bookingService.getUserBookings(2L, "WAITING", 1L, 1);
        assertEquals(1, actualDto.size());

        Mockito.when(bookingRepository.findAllRejectedBookings(any()))
                .thenReturn(List.of(bookingToReturn));
        actualDto = bookingService.getUserBookings(2L, "REJECTED", 1L, 1);
        assertEquals(1, actualDto.size());
    }

    @Test
    void getOwnerBookings_shouldReturnListOfBookings_whenInvoked() {
        List<BookingDto> actualDto;
        when(userService.getUserIfExist(1L))
                .thenReturn(owner);

        when(bookingRepository.findAllOwnerBookings(any(), any()))
                .thenReturn(List.of(bookingToReturn));
        actualDto = bookingService.getBookingsByOwner(1L, "ALL", 1L, 1);
        assertEquals(1, actualDto.size());

        when(bookingRepository.findAllOwnerPastBookings(any(), any()))
                .thenReturn(List.of(bookingToReturn));
        actualDto = bookingService.getBookingsByOwner(1L, "PAST", 1L, 1);
        assertEquals(1, actualDto.size());

        when(bookingRepository.findAllOwnerFutureBookings(any(), any()))
                .thenReturn(List.of(bookingToReturn));
        actualDto = bookingService.getBookingsByOwner(1L, "FUTURE", 1L, 1);
        assertEquals(1, actualDto.size());

        when(bookingRepository.findAllOwnerCurrentBookings(any(), any()))
                .thenReturn(List.of(bookingToReturn));
        actualDto = bookingService.getBookingsByOwner(1L, "CURRENT", 1L, 1);
        assertEquals(1, actualDto.size());

        when(bookingRepository.findAllOwnerWaitingBookings(any()))
                .thenReturn(List.of(bookingToReturn));
        actualDto = bookingService.getBookingsByOwner(1L, "WAITING", 1L, 1);
        assertEquals(1, actualDto.size());

        when(bookingRepository.findAllOwnerRejectedBookings(any()))
                .thenReturn(List.of(bookingToReturn));
        actualDto = bookingService.getBookingsByOwner(1L, "REJECTED", 1L, 1);
        assertEquals(1, actualDto.size());
    }

    @Test
    void checkIfBookingExist_shouldThrowsNotFoundException_whenNotFound() {
        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> bookingService.getBookingIfExist(1L)
        );
    }
}
