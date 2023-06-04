package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exeption.NotFoundException;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = {"db.name=test"})
public class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;

    User user;
    UserDto userDto;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "user 1", "user1@user.com");
        userDto = UserMapper.userToDto(user);
    }

    @Test
    void getUsers_shouldReturnStatusOkAndEmptyListJson_whenNoUsers() throws Exception {
        when(userService.getUsers()).thenReturn(List.of());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(userService, times(1)).getUsers();
    }

    @Test
    void getUsers_shouldReturnStatusOkAndUsersListJson_whenFound() throws Exception {
        when(userService.getUsers()).thenReturn(List.of(userDto));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$.[0].email", is(userDto.getEmail())));

        verify(userService, times(1)).getUsers();
    }

    @Test
    void findUser_shouldReturnStatusOkAndUserJson_whenFound() throws Exception {
        when(userService.getUser(1L))
                .thenReturn(userDto);

        mockMvc.perform(get("/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userService, times(1)).getUser(1L);
    }

    @Test
    void findUser_shouldReturnStatusNotFound_whenNotFound() throws Exception {
        when(userService.getUser(1L))
                .thenThrow(new NotFoundException("not found"));

        mockMvc.perform(get("/users/{id}", 1L))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUser(1L);
    }

    @Test
    void addNewUser_shouldReturnStatusOkAndUserJson_whenInvoked() throws Exception {
        when(userService.addUser(any()))
                .thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userService, times(1)).addUser(any());
    }

    @Test
    void updateUser_shouldReturnStatusOkAndUserJson_whenInvoked() throws Exception {
        when(userService.updateUser(any(), any())).thenReturn(userDto);

        mockMvc.perform(patch("/users/{id}", 1L)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userService, times(1)).updateUser(any(), any());
    }

    @Test
    void deleteUser_shouldReturnStatusOk_whenInvoked() throws Exception {
        mockMvc.perform(delete("/users/{id}", 1L))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(1L);
    }

}
