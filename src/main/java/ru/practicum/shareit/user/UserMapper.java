package ru.practicum.shareit.user;

public class UserMapper {

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User toUser(UserDto user) {
        return new User(
                user.getId(),
                user.getName(),
                user.getEmail());
    }

    public static UserInfoDto toUserInfoDto(User user) {
        return new UserInfoDto(
                user.getId()
        );
    }
}