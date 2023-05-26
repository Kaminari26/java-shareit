package ru.practicum.shareit.booking.item;

import ru.practicum.shareit.booking.item.dto.ItemDto;

import java.util.List;

public interface IItemService {

    ItemDto add(ItemDto itemDto, Long userId);

    ItemDto update(ItemDto itemDto, Long itemId, Long userId);

    ItemDto get(Long itemId);

    void deleted(Long itemId);

    List<ItemDto> getItems(Long userId);

    List<ItemDto> searchItem(String text);
}
