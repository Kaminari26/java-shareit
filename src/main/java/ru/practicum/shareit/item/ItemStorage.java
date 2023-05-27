package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemStorage {

    private final HashMap<Long, Item> items = new HashMap<>();
    private Long itemId = 0L;

    public Item addItem(Item item) {
        itemId++;
        item.setId(itemId);
        items.put(item.getId(), item);
        return item;
    }

    public Item updateItem(Long itemId) {
        return items.get(itemId);
    }

    public Item getItem(Long itemId) {
        return items.get(itemId);
    }

    public void removeItem(Long itemId) {
        items.remove(itemId);
    }

    public List<Item> getItems(Long userId) {
        List<Item> allItems = items.values().parallelStream().filter(item ->
                item.getOwner().equals(userId)).collect(Collectors.toList());

        return allItems;
    }

    public List<Item> searchItem(String text) {
        List<Item> searchItems = new ArrayList<>();
        for (Item item : items.values()) {
            if ((item.getName().toLowerCase().contains(text.toLowerCase()) ||
                    item.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                    item.getAvailable() == true) {
                searchItems.add(item);
            }
        }
        return searchItems;
    }

    public HashMap<Long, Item> getAllItemsDefault() {
        return items;
    }
}
