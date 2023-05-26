package ru.practicum.shareit.booking.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.item.dto.ItemDto;

import java.util.List;

@Service
public class ItemService implements IItemService {
    private final ItemStorage itemStorage;


    @Autowired
    public ItemService(ItemStorage itemStorage) {
        this.itemStorage = itemStorage;
    }


    @Override
    public ItemDto add(ItemDto itemDto, Long userId) {
        return itemStorage.addItem(itemDto, userId);
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long itemId, Long userId) {
        return itemStorage.updateItem(itemDto, itemId, userId);
    }

    @Override
    public ItemDto get(Long itemId) {
        return itemStorage.getItem(itemId);
    }

    @Override
    public void deleted(Long itemId) {
        itemStorage.removeItem(itemId);
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        return itemStorage.getItems(userId);
    }

    @Override
    public List<ItemDto> searchItem(String query) {
        return itemStorage.searchItem(query);
    }
}
