package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface IItemService {

    ItemDto add(ItemDto itemDto, Long userId);

    ItemDto update(ItemDto itemDto, Long itemId, Long userId);

    ItemDto get(Long itemId);

    void deleted(Long itemId);

    List<ItemDto> getItems(Long userId);

    List<ItemDto> searchItem(String text);
}
