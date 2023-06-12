package ru.practicum.shareit.item;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemMapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.IUserService;

import java.util.*;

@Service
public class ItemService implements IItemService {
    private final ItemStorage itemStorage;
    private final IUserService iUserService;


    @Autowired
    public ItemService(ItemStorage itemStorage, IUserService iUserService) {
        this.itemStorage = itemStorage;
        this.iUserService = iUserService;
    }


    @Override
    @SneakyThrows
    public ItemDto add(ItemDto itemDto, Long owner) {
        Item item = ItemMapper.toDtoItem(itemDto, owner);
        if (iUserService.get(item.getOwner()) == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        return ItemMapper.toItemDto(itemStorage.addItem(item));
    }

    @Override
    @SneakyThrows
    public ItemDto update(ItemDto itemDto, Long itemId, Long userId) {
        Item item = ItemMapper.toDtoItem(itemDto, userId);
        HashMap<Long, Item> items = itemStorage.getAllItemsDefault();
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

        if (item.getDescription() != null) {
            updateItem.setDescription(item.getDescription());
        }

        return ItemMapper.toItemDto(itemStorage.updateItem(itemId));
    }

    @Override
    public ItemDto get(Long itemId) {
        return ItemMapper.toItemDto(itemStorage.getItem(itemId));
    }

    @Override
    public void deleted(Long itemId) {
        itemStorage.removeItem(itemId);
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        List<Item> allItems = itemStorage.getItems(userId);
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : allItems) {
            itemDtos.add(ItemMapper.toItemDto(item));
        }
        return itemDtos;
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        List<ItemDto> searchDto = new ArrayList<>();
        for (Item item : itemStorage.searchItem(text)) {
            searchDto.add(ItemMapper.toItemDto(item));
        }
        if (searchDto.size() == 0) {
            throw new NoSuchElementException("Предметы не найдены");
        }
        return searchDto;
    }

    @Override
    public ItemDtoForBooking getItemDtoForBooking(Long id) {
        return null;
    }
}
