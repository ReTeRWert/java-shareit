package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingJpaRepository;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.exeption.BadRequestException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentJpaRepository;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {


    private final ItemJpaRepository itemJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final CommentJpaRepository commentJpaRepository;
    private final BookingJpaRepository bookingJpaRepository;

    @Transactional
    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);

        User owner = checkUser(userId);

        item.setOwner(owner);

        return ItemMapper.toItemDto(itemJpaRepository.save(item));
    }

    @Transactional
    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {

        Item itemToUpdate = checkItem(itemId);

        if (!Objects.equals(itemToUpdate.getOwner().getId(), userId)) {
            throw new NotFoundException("User not owner");
        }

        if (itemDto.getName() != null) {
            itemToUpdate.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            itemToUpdate.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            itemToUpdate.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toItemDto(itemJpaRepository.save(itemToUpdate));
    }

    @Transactional
    @Override
    public ItemDto getItem(Long userId, Long itemId) {

        Item item = checkItem(itemId);
        ItemDto itemDto = ItemMapper.toItemDto(item);

        List<Comment> comments = commentJpaRepository.findAllByItemIdIs(itemId);
        List<CommentDto> commentDto = comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        itemDto.setComments(commentDto);

        if (Objects.equals(item.getOwner().getId(), userId)) {
            itemDto.setLastBooking(getLastBooking(itemId));
            itemDto.setNextBooking(getNextBooking(itemId));
        }

        return itemDto;
    }

    @Transactional
    @Override
    public List<ItemDto> getItemsByOwner(Long userId) {

        List<ItemDto> ownerItems = itemJpaRepository.findAllByOwnerIdIsOrderById(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

        for (ItemDto itemDto : ownerItems) {

            List<CommentDto> comments = commentJpaRepository.findAllByItemIdIs(itemDto.getId())
                    .stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList());

            itemDto.setComments(comments);
            itemDto.setLastBooking(getLastBooking(itemDto.getId()));
            itemDto.setNextBooking(getNextBooking(itemDto.getId()));
        }

        return ownerItems;
    }

    @Transactional
    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        List<Item> items = itemJpaRepository.search(text.toLowerCase());

        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {

        User author = checkUser(userId);
        Item item = checkItem(itemId);
        LocalDateTime creationDate = LocalDateTime.now();
        List<Booking> pastBookings = bookingJpaRepository.findAllPastBookings(author.getId(), creationDate);

        Optional<Booking> booking = pastBookings.stream()
                .filter(b -> b.getItem().getId().equals(item.getId()))
                .findFirst();
        if (booking.isEmpty()) {
            throw new BadRequestException("This user don't booking this item");
        }

        Comment comment = CommentMapper.toComment(commentDto);
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreationDate(creationDate);
        commentJpaRepository.save(comment);

        return CommentMapper.toCommentDto(comment);
    }

    private BookingInfoDto getLastBooking(Long itemId) {
        Optional<Booking> lastBooking = bookingJpaRepository.getItemLastBooking(itemId, LocalDateTime.now());

        return lastBooking
                .map(BookingMapper::toBookingInfoDto)
                .orElse(null);
    }

    private BookingInfoDto getNextBooking(Long itemId) {
        Optional<Booking> nextBooking = bookingJpaRepository.getItemNextBooking(itemId,
                LocalDateTime.now());

        return nextBooking
                .filter(b -> !b.getStatus().equals(Status.REJECTED))
                .map(BookingMapper::toBookingInfoDto)
                .orElse(null);
    }

    private User checkUser(Long userId) {
        Optional<User> user = userJpaRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User with id " + userId + " not found.");
        }
        return user.get();
    }

    private Item checkItem(Long itemId) {
        Optional<Item> item = itemJpaRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new NotFoundException("Item with id " + itemId + " not found.");
        }

        return item.get();
    }
}
