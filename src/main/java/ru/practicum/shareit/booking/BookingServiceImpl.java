package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exeption.BadRequestException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.UserVerificationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemJpaRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingJpaRepository bookingRepository;
    private final UserJpaRepository userRepository;
    private final ItemJpaRepository itemRepository;

    @Transactional
    @Override
    public BookingDto saveBooking(Long userId, BookingDto booking) {
        Item item = checkItem(booking.getItemId());
        User user = checkUser(userId);

        if (Objects.equals(item.getOwner().getId(), user.getId())) {
            throw new UserVerificationException("Owner cannot book his item.");
        }

        if (!item.getAvailable()) {
            throw new BadRequestException("Item is not available.");
        }

        checkTime(booking);

        Booking newBooking = BookingMapper.toBooking(booking);
        newBooking.setItem(item);
        newBooking.setBooker(user);
        newBooking.setStatus(Status.WAITING);

        return BookingMapper.toBookingDto(bookingRepository.save(newBooking));
    }

    @Transactional
    @Override
    public BookingDto approveBooking(Long userId, Long bookingId, Boolean approve) {
        Booking booking = checkBooking(bookingId);
        checkUser(userId);
        Item item = checkItem(booking.getItem().getId());

        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException("This user not owner.");
        }

        if (approve && !booking.getStatus().equals(Status.APPROVED)) {
            booking.setStatus(Status.APPROVED);
        } else if (!approve && !booking.getStatus().equals(Status.REJECTED)) {
            booking.setStatus(Status.REJECTED);
        } else {
            throw new BadRequestException("Booking status was already changed");
        }


        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        User user = checkUser(userId);
        Booking booking = checkBooking(bookingId);

        checkUserAccess(user, booking);

        return BookingMapper.toBookingDto(booking);
    }

    @Transactional
    @Override
    public List<BookingDto> getUserBookings(Long userId, String state) {
        checkUser(userId);
        List<Booking> bookings;
        State bookingState = checkBookingState(state);

        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllBookerBookings(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllCurrentBookings(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllFutureBookings(userId, LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findAllPastBookings(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findAllWaitingBookings(userId);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllRejectedBookings(userId);
                break;
            default:
                throw new IllegalStateException("Unknown booking state: " + bookingState);
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<BookingDto> getBookingsByOwner(Long userId, String state) {
        checkUser(userId);
        State bookingState = checkBookingState(state);
        List<Booking> bookings;

        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllOwnerBookings(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllOwnerCurrentBookings(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllOwnerFutureBookings(userId, LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findAllOwnerPastBookings(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findAllOwnerWaitingBookings(userId);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllOwnerRejectedBookings(userId);
                break;
            default:
                throw new IllegalStateException("Unknown booking state: " + bookingState);
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private User checkUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User with id " + userId + " does not exist.");
        }
        return user.get();
    }

    private Item checkItem(Long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new NotFoundException("Item with id " + itemId + " does not exist.");
        }
        return item.get();
    }

    private Booking checkBooking(Long bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            throw new NotFoundException("Booking with id " + bookingId + " does not exist.");
        }
        return booking.get();
    }

    private void checkUserAccess(User user, Booking booking) {
        Long ownerId = booking.getItem().getOwner().getId();
        Long bookerId = booking.getBooker().getId();
        Long userId = user.getId();

        if (!Objects.equals(userId, ownerId) &&
                !Objects.equals(userId, bookerId)) {
            throw new UserVerificationException("User with id " + userId + " is not allowed to access this booking.");
        }
    }

    private void checkTime(BookingDto booking) {
        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new BadRequestException("Start time must be before end time.");
        }

        if (booking.getStart().isEqual(booking.getEnd())) {
            throw new BadRequestException("Start time can't be equals end time.");
        }
    }

    private State checkBookingState(String state) {
        try {
            return State.valueOf(state);
        } catch (Throwable e) {
            throw new BadRequestException("Unknown state: " + state);
        }
    }
}
