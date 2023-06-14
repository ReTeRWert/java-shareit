package ru.practicum.shareit.user;

public class UserMapper {

    public static UserDto userToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User dtoToUser(UserDto user) {
        return new User(
                user.getId(),
                user.getName(),
                user.getEmail());
    }

    public static UserInfoDto userToInfoDto(User user) {
        return new UserInfoDto(
                user.getId()
        );
    }
}