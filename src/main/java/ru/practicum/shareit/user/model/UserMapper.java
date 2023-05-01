package ru.practicum.shareit.user.model;

import ru.practicum.shareit.user.dto.UserDto;

public class UserMapper {

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getName(),
                user.getEmail()
        );
    }

    public static User toUser(Long userId, UserDto user) {
        return new User(userId,
                user.getName(),
                user.getEmail());
    }
}