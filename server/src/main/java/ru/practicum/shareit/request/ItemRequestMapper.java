package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.ArrayList;

public class ItemRequestMapper {
    public static ItemRequestDto requestToDto(ItemRequest request) {
        return new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getRequestor().getId(),
                request.getCreated(),
                new ArrayList<>()
        );
    }

    public static ItemRequest dtoToRequest(ItemRequestDto requestDto) {
        return new ItemRequest(
                requestDto.getDescription()
        );
    }

}
