package ru.practicum.shareit.request;

import com.sun.nio.sctp.IllegalReceiveException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/requests")
public class ItemRequestController {

    private final IItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(IItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto createRequest(@Valid @RequestBody ItemRequestDto itemRequestDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Пришел запрос Post /requests");
        ItemRequestDto itemRequest = itemRequestService.add(itemRequestDto, userId);
        log.info("Отправлен ответ: " + itemRequest);
        return itemRequest;
    }

    @GetMapping
    public List<ItemRequestDto> getRequest(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Пришел запрос Get / requests " + userId);
        List<ItemRequestDto> itemRequestDto = itemRequestService.getItemRequestByUserId(userId);
        log.info("Отправлен ответ: " + itemRequestDto);
        return itemRequestDto;
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(value = "from", defaultValue = "0", required = false) @PositiveOrZero Integer from,
                                               @RequestParam(value = "size", defaultValue = "10", required = false) @Positive Integer size) {

        if (size <= 0 || from < 0) {
            throw new IllegalReceiveException("Неверно указан параметр");
        }
        log.info("Пришел запрос Get /All");
        List<ItemRequestDto> itemRequestDto = itemRequestService.getAllRequests(userId, from, size);
        log.info("Отправлен ответ: " + itemRequestDto);
        return itemRequestDto;
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestForId(@PathVariable Long requestId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Пришел запрос Get /" + requestId);
        ItemRequestDto itemRequest = itemRequestService.getRequestById(requestId, userId);
        log.info("Отправлен ответ: " + itemRequest);
        return itemRequest;
    }
}
