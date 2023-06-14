package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemClient itemClient;
    @Autowired
    private MockMvc mockMvc;
    ItemDto itemDto;
    CommentDto commentDto;

    @BeforeEach
    void setup() {
        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("name1");
        itemDto.setDescription("description1");
        itemDto.setAvailable(true);
        itemDto.setOwner(1L);

        commentDto = new CommentDto(
                1L,
                "text",
                "author",
                LocalDateTime.now()
        );
    }

    @Test
    void addNewItem_whenNotValid_thenReturnStatusBadRequest() throws Exception {
        itemDto.setName("");

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
        verify(itemClient, never()).addItem(any(), any());

        itemDto.setName("name");
        itemDto.setDescription("");

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
        verify(itemClient, never()).addItem(any(), any());

        itemDto.setDescription("desc");
        itemDto.setAvailable(null);

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
        verify(itemClient, never()).addItem(any(), any());
    }

    @Test
    void addComment_whenNotValid_thenReturnStatusBadRequest() throws Exception {
        commentDto.setText("");

        mockMvc.perform(post("/items/{id}/comment", 1L)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
        verify(itemClient, never()).addComment(any(), any(), any());
    }
}