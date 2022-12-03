package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao {

    Item addItem(Item item);

    Item updateItem(Item item, int itemId);

    Item getItem(int itemId);

    List<Item> getItems();

    List<Item> getItemsByUserId(int userId);

    List<Item> searchItems(String text);
}
