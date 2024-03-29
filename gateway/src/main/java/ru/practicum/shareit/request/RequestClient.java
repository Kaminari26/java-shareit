package ru.practicum.shareit.request;

import com.sun.nio.sctp.IllegalReceiveException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exception.InvalidStatusException;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Map;

@Service
public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createRequest(ItemRequestDto itemRequestDto, Long userId) {
        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isBlank()) {
            throw new InvalidStatusException("Не может быть пустым");
        }
        return post("", userId, itemRequestDto);
    }

    public ResponseEntity<Object> getRequest(Long userId) {
        return get("/", userId);
    }

    public ResponseEntity<Object> getAllRequests(Long userId, Integer from, Integer size) {
        if (size <= 0 || from < 0) {
            throw new IllegalReceiveException("Неверно указан параметр");
        }
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/all?from=" + from + "&size=" + size + "", userId);
    }

    public ResponseEntity<Object> getRequestForId(Long userId, Long requestId) {
        return get("/" + requestId, userId);
    }
}
