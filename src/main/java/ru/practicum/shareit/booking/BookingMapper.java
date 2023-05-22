package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

public class BookingMapper {

    public static BookingDto bookingToDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                ItemMapper.itemToInfoDto(booking.getItem()),
                UserMapper.userToInfoDto(booking.getBooker()),
                booking.getStatus()
        );
    }

    public static Booking dtoToBooking(BookingDto bookingDto) {
        return new Booking(
                bookingDto.getStart(),
                bookingDto.getEnd(),
                bookingDto.getStatus()
        );
    }

    public static BookingInfoDto bookingToInfoDto(Booking booking) {
        return new BookingInfoDto(
                booking.getId(),
                booking.getBooker().getId()
        );
    }
}
