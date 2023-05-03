package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorageImpl implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private static Long id = 1L;

    @Override
    public User getUser(Long userId) {
        checkUser(userId);
        return users.get(userId);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User addUser(User user) {
        checkEmail(id, user);
        User newUser = user.toBuilder().id(id++)
                .build();
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public User updateUser(Long userId, User user) {
        checkEmail(userId, user);
        User updatedUser = users.get(userId);
        if (user.getEmail() != null) {
            updatedUser = updatedUser
                    .toBuilder()
                    .email(user.getEmail())
                    .build();
        }

        if (user.getName() != null) {
            updatedUser = updatedUser
                    .toBuilder()
                    .name(user.getName())
                    .build();
        }

        users.put(userId, updatedUser);
        return updatedUser;
    }

    @Override
    public void deleteUser(Long userId) {
        checkUser(userId);
        users.remove(userId);
    }

    private void checkUser(Long userId) {
        if (users.get(userId) == null) {
            throw new NotFoundException("User " + userId + " not found");
        }
    }

    private void checkEmail(Long userId, User user) {
        if (users.values().stream()
                .anyMatch(o -> o.getEmail().equalsIgnoreCase(user.getEmail())
                        && !o.getId().equals(userId)))
            throw new ConflictException("User with email " + user.getEmail() + " already exists");
    }
}
