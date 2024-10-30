package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemClient extends BaseClient {
    static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(Integer userId, ItemDto itemDto) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> updateItem(Integer userId, Integer itemId, ItemDto itemDto) {
        return patch("/" + userId, itemId, itemDto);
    }

    public ResponseEntity<Object> getByText(String text) {
        return get(text);
    }

    public ResponseEntity<Object> getAllItemsUser(Integer userId) {
        return get("", userId);
    }


    public ResponseEntity<Object> getById(Integer userId, Integer itemId) {
        return get("/" + userId, itemId);
    }

    public ResponseEntity<Object> addComment(Integer userId, Integer itemId, CommentDto commentDto) {
        return post("/" + userId + "/comment", itemId, commentDto);
    }
}
