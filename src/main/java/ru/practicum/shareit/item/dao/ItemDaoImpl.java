package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemDaoImpl implements ItemDao {

    private final Map<Integer, Item> items = new HashMap<>();

    @Override
    public Item addItem(Item item) {
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public Item updateItem(Item item, int itemId) {
        items.put(itemId, item);
        return items.get(itemId);
    }

    @Override
    public Item getItem(int itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getItems() {
        return new ArrayList<>(items.values());
    }

    @Override
    public List<Item> getItemsByUserId(int userId) {
        return items.values().stream()
                .filter(item -> item.getOwner() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItems(String text) {
        List<Item> itemList = new ArrayList<>();

        if (!text.isBlank()) {
            for (Item item : items.values()) {
                String name = item.getName().toLowerCase();
                String description = item.getDescription().toLowerCase();

                if (name.contains(text.toLowerCase()) || description.contains(text.toLowerCase()) && item.getAvailable()) {
                    itemList.add(item);
                }
            }
        }
        return itemList;
    }
}
