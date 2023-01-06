package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void itemRequestDto() throws IOException {

        ItemRequestDto.Item item = new ItemRequestDto.Item();
        item.setId(2L);
        item.setOwnerId(2L);
        item.setName("tool");
        item.setDescription("test tool");
        item.setAvailable(true);
        item.setRequestId(2L);

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("test description");
        itemRequestDto.setCreated(LocalDateTime.of(2023, 1, 4, 12, 0, 1));
        itemRequestDto.setItems(List.of(item));

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("test description");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2023-01-04T12:00:01");
        assertThat(result).extractingJsonPathArrayValue("$.items").extracting("id").contains(2);
        assertThat(result).extractingJsonPathArrayValue("$.items").extracting("ownerId").contains(2);
        assertThat(result).extractingJsonPathArrayValue("$.items").extracting("name").contains("tool");
        assertThat(result).extractingJsonPathArrayValue("$.items").extracting("description").contains("test tool");
        assertThat(result).extractingJsonPathArrayValue("$.items").extracting("available").contains(true);
        assertThat(result).extractingJsonPathArrayValue("$.items").extracting("requestId").contains(2);
    }
}