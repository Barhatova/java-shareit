package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemDtoTest {
    private final JacksonTester<ItemDto> json;

    @Test
    void test_Serialize() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1)
                .requestId(1)
                .available(true)
                .description("desc")
                .name("name")
                .build();

        JsonContent<ItemDto> result = json.write(itemDto);
        assertThat(result).hasJsonPath("$.id")
                .hasJsonPath("$.requestId")
                .hasJsonPath("$.available")
                .hasJsonPath("$.description")
                .hasJsonPath("$.name");

        assertThat(result).extractingJsonPathNumberValue("@.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("@.description").isEqualTo("desc");
        assertThat(result).extractingJsonPathNumberValue("@.requestId").isEqualTo(1);
        assertThat(result).extractingJsonPathBooleanValue("@.available").isEqualTo(true);
        assertThat(result).extractingJsonPathStringValue("@.name").isEqualTo("name");
    }
}