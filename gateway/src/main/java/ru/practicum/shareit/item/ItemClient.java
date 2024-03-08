package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getItem(Integer userId, Integer itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getUserItems(Integer userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "size", size,
                "from", from
        );
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getNeedItems(String text, Integer userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return  get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> addItem(Integer userId, ItemRequestDto requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> addComment(Integer userId, Integer itemId, CommentRequestDto requestDto) {
        return post("/" + itemId + "/comment", userId, requestDto);
    }

    public ResponseEntity<Object> editItem(Integer userId, Integer itemId, ItemRequestDto requestDto) {
        return patch("/" + itemId, userId, requestDto);
    }
}
