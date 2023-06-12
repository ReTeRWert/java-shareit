package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.exeption.BadRequestException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {


    private final ItemRepository itemRepository;
    private final UserService userService;
    private final CommentRepository commentJpaRepository;
    private final BookingRepository bookingJpaRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.dtoToItem(itemDto);

        User owner = userService.getUserIfExist(userId);
        item.setOwner(owner);

        if (itemDto.getRequestId() != null) {
            Optional<ItemRequest> request = itemRequestRepository.findById(itemDto.getRequestId());
            if (request.isEmpty()) {
                throw new NotFoundException("Request does not exist.");
            }

            item.setRequest(request.get());

            return ItemMapper.itemToDto(itemRepository.save(item));
        }

        return ItemMapper.itemToDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {

        Item itemToUpdate = getItemIfExist(itemId);

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

        return ItemMapper.itemToDto(itemRepository.save(itemToUpdate));
    }

    @Transactional
    @Override
    public ItemDto getItem(Long userId, Long itemId) {

        Item item = getItemIfExist(itemId);
        ItemDto itemDto = ItemMapper.itemToDto(item);

        List<Comment> comments = commentJpaRepository.findAllByItemIdIs(itemId);
        List<CommentDto> commentDto = comments.stream()
                .map(CommentMapper::commentToDto)
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
    public List<ItemDto> getItemsByOwner(Long userId, Long from, Integer size) {
        int startPage = Math.toIntExact(from / size);

        List<ItemDto> ownerItems = itemRepository.findAllByOwnerIdIsOrderById(userId, PageRequest.of(startPage, size))
                .stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());

        for (ItemDto itemDto : ownerItems) {

            List<CommentDto> comments = commentJpaRepository.findAllByItemIdIs(itemDto.getId())
                    .stream()
                    .map(CommentMapper::commentToDto)
                    .collect(Collectors.toList());

            itemDto.setComments(comments);
            itemDto.setLastBooking(getLastBooking(itemDto.getId()));
            itemDto.setNextBooking(getNextBooking(itemDto.getId()));
        }

        return ownerItems;
    }

    @Transactional
    @Override
    public List<ItemDto> searchItems(String text, Long from, Integer size) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        int startPage = Math.toIntExact(from / size);
        List<Item> items = itemRepository.search(text.toLowerCase(), PageRequest.of(startPage, size));

        return items.stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {

        User author = userService.getUserIfExist(userId);
        Item item = getItemIfExist(itemId);
        LocalDateTime creationDate = LocalDateTime.now();
        List<Booking> pastBookings = bookingJpaRepository.findAllPastBookings(author.getId(), creationDate);

        Optional<Booking> booking = pastBookings.stream()
                .filter(b -> b.getItem().getId().equals(item.getId()))
                .findFirst();
        if (booking.isEmpty()) {
            throw new BadRequestException("This user don't booking this item");
        }

        Comment comment = CommentMapper.dtoToComment(commentDto);
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreationDate(creationDate);
        commentJpaRepository.save(comment);

        return CommentMapper.commentToDto(comment);
    }

    private BookingInfoDto getLastBooking(Long itemId) {
        Optional<Booking> lastBooking = bookingJpaRepository.getItemLastBooking(itemId, LocalDateTime.now());

        return lastBooking
                .map(BookingMapper::bookingToInfoDto)
                .orElse(null);
    }

    private BookingInfoDto getNextBooking(Long itemId) {
        Optional<Booking> nextBooking = bookingJpaRepository.getItemNextBooking(itemId,
                LocalDateTime.now());

        return nextBooking
                .filter(b -> !b.getStatus().equals(Status.REJECTED))
                .map(BookingMapper::bookingToInfoDto)
                .orElse(null);
    }

    public Item getItemIfExist(Long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new NotFoundException("Item with id " + itemId + " not found.");
        }

        return item.get();
    }

    @Override
    public List<Item> getItemsByRequestIdIn(List<Long> requestIds) {
        return itemRepository.findAllByRequestIdIn(requestIds);
    }

    @Override
    public List<Item> getItemsByRequestIdIs(Long requestId) {
        return itemRepository.findAllByRequestIdIs(requestId);
    }
}
