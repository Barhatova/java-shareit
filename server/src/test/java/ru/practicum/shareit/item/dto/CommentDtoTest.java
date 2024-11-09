package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentDtoTest {
    @Autowired
    final JacksonTester<CommentDto> json;

    @Test
    void test_serialize() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .id(1)
                .created(LocalDateTime.now())
                .authorName("Alex")
                .text("Text")
                .build();
        JsonContent<CommentDto> result = json.write(commentDto);
        assertThat(result).hasJsonPath("$.id")
                .hasJsonPath("$.created")
                .hasJsonPath("$.authorName")
                .hasJsonPath("$.text");

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .satisfies(item_id -> assertThat(item_id.intValue()).isEqualTo(commentDto.getId()));
        assertThat(result).extractingJsonPathStringValue("$.created")
                .satisfies(created -> assertThat(created).isNotNull());
        assertThat(result).extractingJsonPathStringValue("$.authorName")
                .satisfies(item_name -> assertThat(item_name).isEqualTo(commentDto.getAuthorName()));
        assertThat(result).extractingJsonPathStringValue("$.text")
                .satisfies(item_description -> assertThat(item_description).isEqualTo(commentDto.getText()));
    }
}