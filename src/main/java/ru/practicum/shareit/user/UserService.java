package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    UserDto getUser(Long userId);

    List<UserDto> getUsers();

    UserDto addUser(UserDto user);

    UserDto updateUser(Long userId, UserDto user);

    void deleteUser(Long userId);

    User getUserIfExist(Long userId);
}
