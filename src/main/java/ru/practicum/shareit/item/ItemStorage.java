package ru.practicum.shareit.item;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.IUserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Component
public class ItemStorage {

    @Autowired
    IUserService iUserService;

    private final HashMap<Long, Item> items = new HashMap<>();
    private Long itemId = 0L;

    @SneakyThrows
    public ItemDto addItem(ItemDto itemDto, Long owner) {
        Item item = ItemMapper.toDtoItem(itemDto, owner);
        if (iUserService.get(owner) == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        item.setOwner(owner);
        itemId++;
        item.setId(itemId);
        items.put(item.getId(), item);
        return ItemMapper.toItemDto(item);
    }

    @SneakyThrows
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        Item item = ItemMapper.toDtoItem(itemDto, userId);
        if (!items.containsKey(itemId)) {
            throw new NullPointerException("Предмет не найден");
        }
        Item updateItem = items.get(itemId);
        if (!updateItem.getOwner().equals(userId)) {
            throw new UserNotFoundException("Пользователь не найден");
        }

        if (item.getName() != null) {
            updateItem.setName(item.getName());
        }
        if (item.getAvailable() != null) {
            updateItem.setAvailable(item.getAvailable());
        }
        if (item.getRequest() != null) {
            updateItem.setRequest(item.getRequest());
        }

        if (item.getDescription() != null) {
            updateItem.setDescription(item.getDescription());
        }
        return ItemMapper.toItemDto(items.get(itemId));
    }

    public ItemDto getItem(Long itemId) {
        return ItemMapper.toItemDto(items.get(itemId));
    }

    public void removeItem(Long itemId) {
        items.remove(itemId);
    }

    public List<ItemDto> getItems(Long userId) {
        List<Item> allItems = items.values().parallelStream().filter(item -> item.getOwner().equals(userId)).collect(Collectors.toList());
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : allItems) {
            itemDtos.add(ItemMapper.toItemDto(item));
        }
        return itemDtos;
    }

    public List<ItemDto> searchItem(String text) {
        List<ItemDto> searchItems = new ArrayList<>();
        if (text.isBlank()) {
            return searchItems;
        }
        for (Item item : items.values()) {
            if ((item.getName().toLowerCase().contains(text.toLowerCase()) ||
                    item.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                            item.getAvailable() == true) {
                searchItems.add(ItemMapper.toItemDto(item));
            }
        }
        if (searchItems.size() > 0) {
            return searchItems;
        }
        throw new NoSuchElementException("Предметы не найдены");
    }
}
