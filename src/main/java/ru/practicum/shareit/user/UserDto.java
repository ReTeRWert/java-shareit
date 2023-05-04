package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserDto {
    private final Long id;
    private final String name;
    @NotNull(message = "Email must not be null")
    @Email(message = "Email not valid")
    private final String email;

}
