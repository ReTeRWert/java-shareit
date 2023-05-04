package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public UserDto getUser(Long userId) {
        return UserMapper.toUserDto(userStorage.getUser(userId));
    }

    @Override
    public List<UserDto> getUsers() {
        return userStorage.getUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto addUser(UserDto user) {

        User newUser = UserMapper.toUser(user);
        return UserMapper.toUserDto(userStorage.addUser(newUser));
    }

    @Override
    public UserDto updateUser(Long userId, UserDto user) {

        User updatedUser = UserMapper.toUser(user);

        return UserMapper.toUserDto(userStorage.updateUser(userId, updatedUser));
    }

    @Override
    public void deleteUser(Long userId) {
        userStorage.deleteUser(userId);
    }
}
