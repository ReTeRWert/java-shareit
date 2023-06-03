package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserService userService;
    @Mock
    private ItemService itemService;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    ItemRequestDto requestDtoToSave;
    ItemRequest requestToSave;
    ItemRequest requestToReturn;
    User requestor;
    User owner;
    Item item;

    @BeforeEach
    void setup() {

        requestor = new User(
                1L,
                "name",
                "email@yandex.ru"
        );

        owner = new User(
                2L,
                "name",
                "email@yandex.ru"
        );

        requestDtoToSave = new ItemRequestDto();
        requestDtoToSave.setDescription("desc");

        requestToSave = new ItemRequest("desc");

        requestToReturn = new ItemRequest();
        requestToReturn.setId(1L);
        requestToReturn.setRequestor(requestor);
        requestToReturn.setDescription("desc");
        requestToReturn.setCreated(LocalDateTime.now());

        item = new Item();
        item.setId(1L);
        item.setOwner(owner);
        item.setRequest(requestToReturn);
    }

    @Test
    void createNewRequest_whenInvoked_thenSaveAndReturnRequestDto() {
        Mockito.when(userService.getUserIfExist(1L))
                .thenReturn(requestor);

        Mockito.when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(requestToReturn);

        ItemRequestDto actualDto = itemRequestService.addNewRequest(1L, requestDtoToSave);

        assertEquals(1L, actualDto.getRequestorId());
        verify(itemRequestRepository, atMostOnce()).save(any(ItemRequest.class));
    }

    @Test
    void getRequestsByUserId_whenNoRequests_thenReturnEmptyList() {
        Long userId = 1L;

        Mockito.when(userService.getUserIfExist(userId))
                .thenReturn(requestor);

        Mockito.when(itemRequestRepository.findAllByRequestorIdIsOrderByCreatedDesc(userId))
                .thenReturn(List.of());

        Mockito.when(itemService.getItemsByRequestIdIn(List.of()))
                .thenReturn(List.of());

        List<ItemRequestDto> actual = itemRequestService.getRequestsByUserId(userId);

        assertTrue(actual.isEmpty());
    }

    @Test
    void getUsersRequests_whenFound_thenReturnListOfRequests() {
        Long userId = 1L;

        Mockito.when(userService.getUserIfExist(userId))
                .thenReturn(requestor);

        Mockito.when(itemRequestRepository.findAllByRequestorIdIsOrderByCreatedDesc(userId))
                .thenReturn(List.of(requestToReturn));

        Mockito.when(itemService.getItemsByRequestIdIn(List.of(1L)))
                .thenReturn(List.of(item));

        List<ItemRequestDto> actual = itemRequestService.getRequestsByUserId(userId);

        assertAll(
                () -> assertEquals(1, actual.size()),
                () -> assertNotNull(actual.get(0).getItems())
        );
    }

    @Test
    void getAllRequests_whenInvoked_thenReturnListOfRequests() {
        Long userId = 2L;

        Mockito.when(userService.getUserIfExist(userId))
                .thenReturn(owner);

        Mockito.when(itemRequestRepository.findAllByRequestorIdIsNotOrderByCreatedDesc(eq(userId), any()))
                .thenReturn(List.of(requestToReturn));

        Mockito.when(itemService.getItemsByRequestIdIn(List.of(1L)))
                .thenReturn(List.of(item));

        List<ItemRequestDto> actual = itemRequestService.getAllRequests(userId, 1L, 1);

        assertAll(
                () -> assertEquals(1, actual.size()),
                () -> assertNotNull(actual.get(0).getItems())
        );
    }

    @Test
    void getRequestByIdIn_whenFound_thenReturnRequest() {
        Long userId = 2L;
        Long requestId = 1L;

        Mockito.when(userService.getUserIfExist(userId))
                .thenReturn(owner);

        Mockito.when(itemRequestRepository.findById(requestId))
                .thenReturn(Optional.of(requestToReturn));

        ItemRequestDto actualDto = itemRequestService.getRequestById(userId, requestId);

        assertEquals(1L, actualDto.getId());
    }

    @Test
    void getRequestByIdIn_whenNotFound_thenThrowsNotFoundException() {
        Long userId = 2L;
        Long requestId = 1L;

        Mockito.when(userService.getUserIfExist(userId))
                .thenReturn(owner);

        Mockito.when(itemRequestRepository.findById(requestId))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getRequestById(userId, requestId)
        );
    }
}
