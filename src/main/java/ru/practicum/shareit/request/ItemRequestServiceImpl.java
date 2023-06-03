package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.BadRequestException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemService itemService;

    @Transactional
    @Override
    public ItemRequestDto addNewRequest(Long userId, ItemRequestDto itemRequestDto) {
        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isEmpty()) {
            throw new BadRequestException("Item request description is empty.");
        }
        User requestor = userService.getUserIfExist(userId);

        ItemRequest newRequest = ItemRequestMapper.dtoToRequest(itemRequestDto);
        newRequest.setCreated(LocalDateTime.now());
        newRequest.setRequestor(requestor);

        itemRequestRepository.save(newRequest);

        return ItemRequestMapper.requestToDto(newRequest);
    }

    @Transactional
    @Override
    public List<ItemRequestDto> getRequestsByUserId(Long requestorId) {
        userService.getUserIfExist(requestorId);

        List<ItemRequestDto> requests = itemRequestRepository.findAllByRequestorIdIsOrderByCreatedDesc(requestorId)
                .stream()
                .map(ItemRequestMapper::requestToDto)
                .collect(Collectors.toList());

        List<Long> requestIds = requests.stream()
                .map(ItemRequestDto::getId)
                .collect(Collectors.toList());

        List<ItemDto> items = itemService.getItemsByRequestIdIn(requestIds).
                stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());

        return requests.stream()
                .map(r -> getItemsToRequest(r, items))
                .collect(Collectors.toList());

    }

    @Transactional
    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, Long from, Integer size) {
        userService.getUserIfExist(userId);
        int startPage = Math.toIntExact(from / size);
        List<ItemRequestDto> requests = itemRequestRepository.findAllByRequestorIdIsNotOrderByCreatedDesc(userId,
                        PageRequest.of(startPage, size)).stream()
                .map(ItemRequestMapper::requestToDto)
                .collect(Collectors.toList());

        List<Long> requestIds = requests.stream()
                .map(ItemRequestDto::getId)
                .collect(Collectors.toList());

        List<ItemDto> items = itemService.getItemsByRequestIdIn(requestIds).
                stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());

        return requests.stream()
                .map(r -> getItemsToRequest(r, items))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ItemRequestDto getRequestById(Long requestorId, Long requestId) {
        userService.getUserIfExist(requestorId);
        ItemRequestDto request = ItemRequestMapper.requestToDto(getRequestIfExist(requestId));

        Long id = request.getId();

        List<ItemDto> items = itemService.getItemsByRequestIdIs(id).stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());

        getItemsToRequest(request, items);

        return request;
    }

    @Override
    public ItemRequest getRequestIfExist(Long requestId) {
        Optional<ItemRequest> request = itemRequestRepository.findById(requestId);

        if (request.isEmpty()) {
            throw new NotFoundException("Request " + requestId + " does not exist.");
        }

        return request.get();
    }

    private ItemRequestDto getItemsToRequest(ItemRequestDto requestDto, List<ItemDto> items) {
        List<ItemDto> requestItems = items.stream()
                .filter(item -> item.getRequestId().equals(requestDto.getId()))
                .collect(Collectors.toList());

        requestDto.setItems(requestItems);
        return requestDto;
    }
}
