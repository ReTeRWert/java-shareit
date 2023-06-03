package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private ItemRepository itemRepository;

    User user1;
    User user2;
    User user3;
    Item itemToSave;
    Item item1;
    Item item2;
    ItemRequest requestToSave;
    ItemRequest request1;


    @BeforeEach
    void setup() {
        user1 = userRepository.save(new User(
                null,
                "user 1",
                "user1@email")
        );

        user2 = userRepository.save(new User(
                null,
                "user 2",
                "user2@email")
        );

        user3 = userRepository.save(new User(
                null,
                "user 3",
                "user3@email")
        );

        requestToSave = new ItemRequest();
        requestToSave.setDescription("desc");
        requestToSave.setRequestor(user3);
        requestToSave.setCreated(LocalDateTime.now());
        request1 = itemRequestRepository.save(requestToSave);

        itemToSave = new Item("name1", "description1", true);
        itemToSave.setOwner(user1);
        itemToSave.setRequest(request1);
        item1 = itemRepository.save(itemToSave);

        itemToSave = new Item("name2", "description2", false);
        itemToSave.setOwner(user2);
        item2 = itemRepository.save(itemToSave);
    }

    @AfterEach
    void afterEach() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @Test
    void findAllByOwnerIdIsOrderById_whenInvoked_thenReturnListOfItems() {
        List<Item> actualItems = itemRepository
                .findAllByOwnerIdIsOrderById(user1.getId(), PageRequest.of(0, 10));

        assertEquals(1, actualItems.size());
    }

    @Test
    void findAllByOwnerIdIsOrderById_whenUserDontHaveItems_thenReturnEmptyList() {
        List<Item> actualItems = itemRepository
                .findAllByOwnerIdIsOrderById(user3.getId(), PageRequest.of(0, 10));

        assertTrue(actualItems.isEmpty());
    }

    @Test
    void search_whenFind_thenReturnListOfItems() {
        List<Item> actualItems = itemRepository.search("name", PageRequest.of(0, 10));

        assertEquals(1, actualItems.size());
    }

    @Test
    void findAllByRequestIdIn_whenFound_thenReturnListOfItems() {
        List<Item> actualItems = itemRepository.findAllByRequestIdIn(List.of(request1.getId()));

        assertEquals(1, actualItems.size());
    }

    @Test
    void findAllByRequestIdIn_whenNotFound_thenEmptyList() {
        List<Item> actualItems = itemRepository.findAllByRequestIdIn(List.of(100L));

        assertTrue(actualItems.isEmpty());
    }

    @Test
    void findAllByRequestIdIs_whenFound_thenReturnListOfItems() {
        List<Item> actualItems = itemRepository.findAllByRequestIdIs(request1.getId());

        assertEquals(1, actualItems.size());
    }

    @Test
    void findAllByRequestIdIs_whenNotFound_thenEmptyList() {
        List<Item> actualItems = itemRepository.findAllByRequestIdIs(100L);

        assertTrue(actualItems.isEmpty());
    }
}
