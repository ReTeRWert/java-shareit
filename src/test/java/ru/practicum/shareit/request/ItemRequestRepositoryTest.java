package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ItemRequestRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    User requestor1;
    User requestor2;
    ItemRequest requestToSave;
    ItemRequest request1;
    ItemRequest request2;

    @BeforeEach
    void setup() {
        requestor1 = userRepository.save(new User(
                null,
                "user 1",
                "user1@email"));
        requestor2 = userRepository.save(new User(
                null,
                "user 2",
                "user2@email"));

        requestToSave = new ItemRequest();
        requestToSave.setRequestor(requestor1);
        requestToSave.setDescription("desc");
        requestToSave.setCreated(LocalDateTime.now().minusDays(2));
        request1 = itemRequestRepository.save(requestToSave);

        requestToSave = new ItemRequest();
        requestToSave.setRequestor(requestor1);
        requestToSave.setDescription("desc1");
        requestToSave.setCreated(LocalDateTime.now().minusDays(1));
        request2 = itemRequestRepository.save(requestToSave);
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @Test
    void findAllByRequestorIdOrderByCreatedDesc_whenFound_thenReturnListOfRequests() {
        List<ItemRequest> requests = itemRequestRepository
                .findAllByRequestorIdIsOrderByCreatedDesc(requestor1.getId());

        assertAll(
                () -> assertEquals(2, requests.size()),
                () -> assertEquals(request1.getId(), requests.get(1).getId()),
                () -> assertEquals(request2.getId(), requests.get(0).getId())
        );
    }

    @Test
    void findAllByRequestorIdOrderByCreatedDesc_whenNotFound_thenReturnEmptyList() {
        List<ItemRequest> requests = itemRequestRepository
                .findAllByRequestorIdIsOrderByCreatedDesc(requestor2.getId());

        assertTrue(requests.isEmpty());
    }

    @Test
    void findAllByRequestorIdIsNot_whenNotFound_thenReturnEmptyList() {
        List<ItemRequest> requests = itemRequestRepository
                .findAllByRequestorIdIsNotOrderByCreatedDesc(requestor1.getId(), PageRequest.of(0, 10));

        assertTrue(requests.isEmpty());
    }

    @Test
    void findAllByRequestorIdIsNot_whenFound_thenReturnListOfRequests() {
        List<ItemRequest> requests = itemRequestRepository
                .findAllByRequestorIdIsNotOrderByCreatedDesc(requestor2.getId(), PageRequest.of(0, 10));

        assertEquals(2, requests.size());
    }
}
