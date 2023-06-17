package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Transactional
    @Override
    public BookingDto saveBooking(Long userId, BookingDto booking) {
        Item item = itemService.getItemIfExist(booking.getItemId());
        User user = userService.getUserIfExist(userId);

        if (Objects.equals(item.getOwner().getId(), user.getId())) {
            throw new UserVerificationException("Owner cannot book his item.");
        }

        if (!item.getAvailable()) {
            throw new BadRequestException("Item is not available.");
        }

        checkTime(booking);

        Booking newBooking = BookingMapper.dtoToBooking(booking);
        newBooking.setItem(item);
        newBooking.setBooker(user);
        newBooking.setStatus(Status.WAITING);

        return BookingMapper.bookingToDto(bookingRepository.save(newBooking));
    }

    @Transactional
    @Override
    public BookingDto approveBooking(Long userId, Long bookingId, Boolean approve) {
        Booking booking = getBookingIfExist(bookingId);
        userService.getUserIfExist(userId);
        Item item = itemService.getItemIfExist(booking.getItem().getId());

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


        return BookingMapper.bookingToDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        User user = userService.getUserIfExist(userId);
        Booking booking = getBookingIfExist(bookingId);

        checkUserAccess(user, booking);

        return BookingMapper.bookingToDto(booking);
    }

    @Transactional
    @Override
    public List<BookingDto> getUserBookings(Long userId, String state, Long from, Integer size) {
        userService.getUserIfExist(userId);
        List<Booking> bookings;
        State bookingState = checkBookingState(state);
        if (from < 0) {
            throw new IllegalArgumentException("From must be greater than 0.");
        }
        int startPage = Math.toIntExact(from / size);

        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdIsOrderByIdDesc(userId, PageRequest.of(startPage, size));
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
                .map(BookingMapper::bookingToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<BookingDto> getBookingsByOwner(Long userId, String state, Long from, Integer size) {
        userService.getUserIfExist(userId);
        State bookingState = checkBookingState(state);
        List<Booking> bookings;
        if (from < 0) {
            throw new IllegalArgumentException("From must be greater than 0.");
        }
        int startPage = Math.toIntExact(from / size);

        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllOwnerBookings(userId, PageRequest.of(startPage, size));
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
                .map(BookingMapper::bookingToDto)
                .collect(Collectors.toList());
    }

    public Booking getBookingIfExist(Long bookingId) {
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
