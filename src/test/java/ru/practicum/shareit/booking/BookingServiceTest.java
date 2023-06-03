package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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
    void createNewBooking_whenInvoked_thenReturnBooking() {
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
    void createNewBooking_whenBookerIsOwner_thenThrowsUserVerificationException() {
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
    void createNewBooking_whenItemIsNotAvailable_thenThrowsBadRequestException() {
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
    void getBooking_whenFound_thenReturnBookingDto() {
        Mockito.when(userService.getUserIfExist(1L))
                .thenReturn(owner);
        Mockito.when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(bookingToSave));

        BookingDto actualBookingDto = bookingService.getBookingById(1L, 1L);

        assertEquals(1L, actualBookingDto.getId());
    }

    @Test
    void getBooking_whenNotFound_thenThrowsNotFoundException() {
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
    void getUserBookings_whenInvoked_thenReturnListOfBookings() {
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
    void getOwnerBookings_whenInvoked_thenReturnListOfBookings() {
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
    void checkIfBookingExist_WhenNotFound_thenThrowsNotFoundException() {
        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> bookingService.getBookingIfExist(1L)
        );
    }
}
