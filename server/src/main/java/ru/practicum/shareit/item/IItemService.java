package ru.practicum.shareit.item;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;

import java.util.List;

public interface IItemService {

    ItemDto add(ItemDto itemDto, Long userId);

    ItemDto update(ItemDto itemDto, Long itemId, Long userId);

    ItemDto get(Long itemId);

    void deleted(Long itemId);

    List<ItemDtoForBooking> getItems(Long userId);

    List<ItemDto> searchItem(String text);

    ItemDtoForBooking getItemDtoForBooking(Long id, Long ownerId);

    CommentDto addComment(CommentDto request, Long userId, Long itemId);
}
