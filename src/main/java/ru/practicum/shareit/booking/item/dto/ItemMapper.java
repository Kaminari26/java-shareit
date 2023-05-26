package ru.practicum.shareit.booking.item.dto;

import ru.practicum.shareit.booking.item.model.Item;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {

        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null
        );

    }

    public static Item toDtoItem(ItemDto itemDto, Long owner) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                owner,
                null
        );

    }
}
