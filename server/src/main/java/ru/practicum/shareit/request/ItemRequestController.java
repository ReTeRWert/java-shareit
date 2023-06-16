package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addNewRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.addNewRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getRequestsByUserId(@RequestHeader("X-Sharer-User-Id") Long requestorId) {
        return itemRequestService.getRequestsByUserId(requestorId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long requestorId,
                                               @RequestParam(value = "from", defaultValue = "0") Long from,
                                               @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return itemRequestService.getAllRequests(requestorId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") Long requestorId,
                                         @PathVariable Long requestId) {
        return itemRequestService.getRequestById(requestorId, requestId);
    }

}
