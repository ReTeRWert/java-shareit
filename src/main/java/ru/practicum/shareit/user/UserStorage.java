package ru.practicum.shareit.user;

import java.util.List;

public interface UserStorage {
    User getUser(Long userId);

    List<User> getUsers();

    User addUser(UserDto user);

    User updateUser(Long userId, UserDto user);

    void deleteUser(Long userId);


}
