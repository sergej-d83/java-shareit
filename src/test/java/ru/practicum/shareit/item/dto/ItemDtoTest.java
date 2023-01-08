package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoTest {

    @Autowired
    JacksonTester<ItemDto> json;

    @Test
    void itemDtoTest() throws Exception {

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("tool");
        itemDto.setDescription("test tool");
        itemDto.setAvailable(true);
        itemDto.setLastBooking(new ItemDto.Booking(1L, 1L));
        itemDto.setNextBooking(new ItemDto.Booking(1L, 1L));
        itemDto.setComments(List.of());
        itemDto.setRequestId(2L);

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemDto.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathArrayValue("$.comments").isEqualTo(List.of());
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(2);


    }
}