package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserJpaRepository userRepository;

    @Transactional
    @Override
    public UserDto getUser(Long userId) {
        return UserMapper.toUserDto(checkUser(userId));
    }

    @Transactional
    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserDto addUser(UserDto user) {
        User newUser = UserMapper.toUser(user);
        return UserMapper.toUserDto(userRepository.save(newUser));
    }

    @Transactional
    @Override
    public UserDto updateUser(Long userId, UserDto user) {
        User updatedUser = checkUser(userId);

        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            updatedUser.setEmail(user.getEmail());
        }

        userRepository.save(updatedUser);
        return UserMapper.toUserDto(updatedUser);
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        checkUser(userId);
        userRepository.deleteById(userId);
    }

    private User checkUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User with id " + userId + " does not exist");
        }
        return user.get();
    }
}
