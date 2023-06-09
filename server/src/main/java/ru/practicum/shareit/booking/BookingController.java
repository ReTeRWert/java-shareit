package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto saveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @RequestBody BookingDto bookingDto) {
        return bookingService.saveBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                     @RequestParam(name = "approved") Boolean approve,
                                     @PathVariable Long bookingId) {
        return bookingService.approveBooking(bookerId, bookingId, approve);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestParam(value = "state", defaultValue = "ALL") String state,
                                            @RequestParam(value = "from", defaultValue = "0") Long from,
                                            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return bookingService.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                               @RequestParam(value = "state", defaultValue = "ALL") String state,
                                               @RequestParam(value = "from", defaultValue = "0") Long from,
                                               @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return bookingService.getBookingsByOwner(ownerId, state, from, size);
    }

}
