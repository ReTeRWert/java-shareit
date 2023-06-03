package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class CommentRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CommentRepository commentRepository;

    User author;
    User owner;
    Item itemToSave;
    Item item1;
    Item item2;
    Comment commentToSave;
    Comment comment1;
    Comment comment2;

    @BeforeEach
    void setup() {
        author = userRepository.save(new User(
                null,
                "user 1",
                "user1@email"));

        owner = userRepository.save(new User(
                null,
                "user 2",
                "user2@email"));

        itemToSave = new Item(
                "name1",
                "description1",
                true);
        itemToSave.setOwner(owner);
        item1 = itemRepository.save(itemToSave);

        itemToSave = new Item(
                "name2",
                "description2",
                true);
        itemToSave.setOwner(owner);
        item2 = itemRepository.save(itemToSave);

        commentToSave = new Comment();
        commentToSave.setAuthor(author);
        commentToSave.setItem(item1);
        commentToSave.setText("text");
        commentToSave.setCreationDate(LocalDateTime.now());
        comment1 = commentRepository.save(commentToSave);
    }

    @AfterEach
    void afterEach() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        commentRepository.deleteAll();
    }

    @Test
    void findAllByItemIdIs_whenFound_thenReturnListOfComments() {
        List<Comment> comments = commentRepository.findAllByItemIdIs(item1.getId());

        assertEquals(1, comments.size());
    }

    @Test
    void findAllByItemIdIs_whenNotFound_thenReturnEmptyList() {
        List<Comment> comments = commentRepository.findAllByItemIdIs(item2.getId());

        assertTrue(comments.isEmpty());
    }

    @Test
    void findAllByItemIdIn_whenFound_thenReturnListOfComments() {
        List<Comment> comments = commentRepository
                .findAllByItemIdIs(item1.getId());
        assertEquals(1, comments.size());

        commentToSave = new Comment();
        commentToSave.setAuthor(author);
        commentToSave.setItem(item2);
        commentToSave.setText("text2");
        commentToSave.setCreationDate(LocalDateTime.now());
        comment2 = commentRepository.save(commentToSave);
        comments = commentRepository
                .findAllByItemIdIs(item2.getId());
        assertEquals(1, comments.size());
    }

}
