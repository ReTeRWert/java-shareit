package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = {"db.name=test"})
public class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemRequestService service;
    @Autowired
    private MockMvc mockMvc;

    ItemRequestDto requestDto;

    @BeforeEach
    void setup() {
        requestDto = new ItemRequestDto(
                1L,
                "desc",
                1L,
                null,
                null
        );
    }

    @Test
    void createNewRequest_shouldReturnStatusOkAndRequestJson_whenInvoked() throws Exception {
        when(service.addNewRequest(any(), any()))
                .thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())));
        verify(service, times(1)).addNewRequest(any(), any());
    }

    @Test
    void getUsersRequests_shouldReturnStatusOkAndListOfRequestsJson_whenInvoked() throws Exception {
        when(service.getRequestsByUserId(any()))
                .thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(requestDto.getDescription())));
        verify(service, times(1)).getRequestsByUserId(any());
    }

    @Test
    void getAllRequestsPageable_shouldReturnStatusOkAndListOfRequestsJson_whenInvoked() throws Exception {
        when(service.getAllRequests(any(), any(), any()))
                .thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests/all")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10))
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(requestDto.getDescription())));
        verify(service, times(1)).getAllRequests(any(), any(), any());
    }

    @Test
    void getRequestById_shouldReturnStatusOkAndRequestJson_whenInvoked() throws Exception {
        when(service.getRequestById(any(), any()))
                .thenReturn(requestDto);

        mockMvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())));
        verify(service, times(1)).getRequestById(any(), any());
    }

    @Test
    void getRequestById_shouldReturnStatusNotFound_whenNotFound() throws Exception {
        when(service.getRequestById(any(), any()))
                .thenThrow(new NotFoundException("not found"));

        mockMvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
        verify(service, times(1)).getRequestById(any(), any());
    }
}
