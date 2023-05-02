package ru.practicum.shareit.user;

public class UserMapper {

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
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