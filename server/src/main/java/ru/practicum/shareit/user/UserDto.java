package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank
    private String name;

    @Email(message = "Email not valid")
    @NotNull(message = "Email must not be null")
    private String email;

}
