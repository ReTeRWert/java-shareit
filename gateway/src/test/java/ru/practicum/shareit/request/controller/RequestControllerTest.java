package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.RequestClient;
import ru.practicum.shareit.request.RequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequestController.class)
@AutoConfigureMockMvc
class RequestControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private RequestClient requestClient;
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
    void createNewRequest_whenNotValid_thenReturnStatusBadRequest() throws Exception {
        requestDto.setDescription("");

        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
        verify(requestClient, never()).addNewRequest(any(), any());
    }

}