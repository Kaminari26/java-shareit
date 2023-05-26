package ru.practicum.shareit.booking.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@Slf4j
@RequestMapping("/items")
public class ItemController {
    ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long owner) {
        log.info("Пришел запрос Post /items");
        return itemService.add(itemDto, owner);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @PathVariable Long itemId,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Обновление предмета с id = " + itemId);
        return itemService.update(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {
        log.info("Запрос предмета Get " + itemId);
        return itemService.get(itemId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable Long itemId) {
        log.info("Удаление предмета " + itemId);
        itemService.deleted(itemId);
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") Long owner) {
        log.info("Список пользователей");
        return itemService.getItems(owner);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text) {
        log.info("Поиск предмета");
        return itemService.searchItem(text);
    }
}
