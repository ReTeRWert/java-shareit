package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.comment.CommentDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = {"db.name=test"})
public class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemService itemService;
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
    void addNewItem_shouldReturnStatusOkAndItemJson_whenInvoked() throws Exception {
        when(itemService.addItem(any(), any()))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.owner", is(itemDto.getOwner()), Long.class));
        verify(itemService, times(1)).addItem(any(), any());
    }

    @Test
    void updateItem_shouldReturnStatusOkAndItemJson_whenInvoked() throws Exception {
        when(itemService.updateItem(any(), any(), any()))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/{Id}", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.owner", is(itemDto.getOwner()), Long.class));
        verify(itemService, times(1)).updateItem(any(), any(), any());
    }

    @Test
    void updateItem_shouldReturnStatusNotFound_whenNotFound() throws Exception {
        when(itemService.updateItem(any(), any(), any()))
                .thenThrow(new NotFoundException("not found"));

        mockMvc.perform(patch("/items/{Id}", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
        verify(itemService, times(1)).updateItem(any(), any(), any());
    }

    @Test
    void getItem_shouldReturnStatusOkAndItemJson_whenInvoked() throws Exception {
        when(itemService.getItem(1L, 1L))
                .thenReturn(itemDto);

        mockMvc.perform(get("/items/{Id}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.owner", is(itemDto.getOwner()), Long.class));
        verify(itemService, times(1)).getItem(1L, 1L);
    }

    @Test
    void getItemsByOwner_shouldReturnStatusOkAndListOfItemsJson_whenInvoked() throws Exception {
        when(itemService.getItemsByOwner(1L, 0L, 10))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10))
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.[0].owner", is(itemDto.getOwner()), Long.class));
        verify(itemService, times(1)).getItemsByOwner(1L, 0L, 10);
    }

    @Test
    void search_shouldReturnStatusOkAndListOfItemsJson_whenInvoked() throws Exception {
        when(itemService.searchItems("name", 0L, 10))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "name")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.[0].owner", is(itemDto.getOwner()), Long.class));
        verify(itemService, times(1)).searchItems("name", 0L, 10);
    }

    @Test
    void addComment_shouldReturnStatusOkAndListOfItemsJson_whenInvoked() throws Exception {
        when(itemService.addComment(any(), any(), any()))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/{id}/comment", 1L)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));
        verify(itemService, times(1)).addComment(any(), any(), any());
    }
}
