package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto saveBooking(Long userId, BookingDto booking);

    BookingDto approveBooking(Long userId, Long bookingId, Boolean approve);

    BookingDto getBookingById(Long userId, Long bookingId);

    List<BookingDto> getUserBookings(Long userId, String state);

    List<BookingDto> getBookingsByOwner(Long userId, String state);

}
