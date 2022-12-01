package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemDao {

    Item addItem(Item item);

    Item updateItem(Item item, int itemId);

    Item getItem(int itemId);

    Collection<Item> getItems();

    Collection<Item> getItemsByUserId(int userId);

    Collection<Item> searchItems(String text);
}
