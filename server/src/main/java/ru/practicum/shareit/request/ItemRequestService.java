package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto addNewRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getRequestsByUserId(Long userId);

    List<ItemRequestDto> getAllRequests(Long userId, Long from, Integer size);

    ItemRequestDto getRequestById(Long requestorId, Long requestId);

    ItemRequest getRequestIfExist(Long requestId);
}
