package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/items")
public class ItemController {
    private final IItemService itemService;

    @Autowired
    public ItemController(IItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long owner) {
        log.info("Пришел запрос Post /items");
        ItemDto itemDtoReady = itemService.add(itemDto, owner);
        log.info("Отправлен ответ " + itemDtoReady);
        return itemDtoReady;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @PathVariable Long itemId,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Обновление предмета с id = " + itemId);
        ItemDto itemDtoReady = itemService.update(itemDto, itemId, userId);
        log.info("Обновленный предмет " + itemDtoReady);
        return itemDtoReady;
    }

    @GetMapping("/{itemId}")
    public ItemDtoForBooking getItem(@PathVariable Long itemId, @RequestHeader(name = "X-Sharer-User-Id") long ownerId) {
        log.info("Запрос предмета Get " + itemId);
        ItemDtoForBooking itemDtoReady = itemService.getItemDtoForBooking(itemId, ownerId);
        log.info("Отправлен ответ " + itemDtoReady);
        return itemDtoReady;
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable Long itemId) {
        log.info("Удаление предмета " + itemId);
        itemService.deleted(itemId);
    }

    @GetMapping
    public List<ItemDtoForBooking> getItems(@RequestHeader("X-Sharer-User-Id") Long owner) {
        log.info("Список пользователей");
        List<ItemDtoForBooking> itemDtoReady = itemService.getItems(owner);
        log.info("Отправлен список " + itemDtoReady);
        return itemDtoReady;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text) {
        log.info("Поиск предмета");
        List<ItemDto> itemDtoReady = itemService.searchItem(text);
        log.info("Отправлен ответ " + itemDtoReady);
        return itemDtoReady;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(name = "X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                                 @Valid @RequestBody CommentDto request) {
        log.info("Добавление комментария");
        return itemService.addComment(request, userId, itemId);
    }
}
