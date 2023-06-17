package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private BookingClient bookingClient;
    @Autowired
    private MockMvc mockMvc;
    LocalDateTime start;
    LocalDateTime end;
    BookItemRequestDto bookingDto;

    @BeforeEach
    void setup() {
        start = LocalDateTime.now().plusDays(1);
        end = LocalDateTime.now().plusDays(2);
        bookingDto = new BookItemRequestDto(1L, start, end);
    }

    @Test
    void createNewBooking_whenNotValid_thenReturnStatusBadRequest() throws Exception {
        bookingDto.setStart(LocalDateTime.now().minusDays(1));

        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());

        bookingDto.setStart(start);
        bookingDto.setEnd(LocalDateTime.now().minusDays(1));

        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());

        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        bookingDto.setItemId(null);

        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
    }
}