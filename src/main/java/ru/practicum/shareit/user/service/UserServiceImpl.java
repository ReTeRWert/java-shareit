package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public User getUser(Long userId) {
        return userStorage.getUser(userId);
    }

    @Override
    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    @Override
    public User addUser(UserDto user) {
        return userStorage.addUser(user);
    }

    @Override
    public User updateUser(Long userId, UserDto user) {
        return userStorage.updateUser(userId, user);
    }

    @Override
    public void deleteUser(Long userId) {
        userStorage.deleteUser(userId);
    }
}
