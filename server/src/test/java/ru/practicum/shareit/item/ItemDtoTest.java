package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.item.comment.CommentDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testSerialize() throws Exception {

        ItemDto itemDto = new ItemDto(
                1L,
                "name",
                "desc",
                true,
                1L,
                1L,
                List.of(new CommentDto(
                        1L,
                        "text",
                        "name",
                        LocalDateTime.now())),
                new BookingInfoDto(1L, 2L),
                null
        );

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemDto.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.owner").isEqualTo(itemDto.getOwner().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(itemDto.getRequestId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id")
                .isEqualTo(1);
    }
}
