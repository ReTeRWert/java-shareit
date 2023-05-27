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

    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto getUser(Long userId) {
        return UserMapper.userToDto(getUserIfExist(userId));
    }

    @Transactional
    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::userToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserDto addUser(UserDto user) {
        User newUser = UserMapper.dtoToUser(user);
        return UserMapper.userToDto(userRepository.save(newUser));
    }

    @Transactional
    @Override
    public UserDto updateUser(Long userId, UserDto user) {
        User updatedUser = getUserIfExist(userId);

        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            updatedUser.setEmail(user.getEmail());
        }

        userRepository.save(updatedUser);
        return UserMapper.userToDto(updatedUser);
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        getUserIfExist(userId);
        userRepository.deleteById(userId);
    }

    public User getUserIfExist(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User with id " + userId + " does not exist");
        }
        return user.get();
    }
}
