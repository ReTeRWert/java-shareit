package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> addUser(@RequestBody @NotNull @Valid UserDto userDto) {
        log.info("save new user = {}", userDto);
        return userClient.addUser(userDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        return userClient.getUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findUser(@PathVariable("id") Long id) {
        log.info("get user with id = {}", id);
        return userClient.findUser(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable("id") Long id,
                                             @RequestBody @NotNull UserDto userDto) {
        userDto.setId(id);
        log.info("update user = {}", userDto);
        return userClient.updateUser(id, userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable("id") Long id) {
        log.info("delete user with id = {}", id);
        return userClient.deleteUser(id);
    }
}
