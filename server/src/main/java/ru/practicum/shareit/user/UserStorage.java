package ru.practicum.shareit.user;

import java.util.List;

public interface UserStorage {
    User getUser(Long userId);

    List<User> getUsers();

    User addUser(User user);

    User updateUser(Long userId, User user);

    void deleteUser(Long userId);


}
