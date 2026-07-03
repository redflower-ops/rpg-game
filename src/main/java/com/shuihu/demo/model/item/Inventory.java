package com.shuihu.demo.model.item;

import java.util.ArrayList;
import java.util.List;

/**
 * 背包（容量上限）
 */
public class Inventory {
    private final int capacity;
    private final List<Item> items;

    public Inventory(int capacity) {
        this.capacity = capacity;
        this.items = new ArrayList<>();
    }

    public boolean add(Item item) {
        if (items.size() >= capacity) return false;
        return items.add(item);
    }

    public boolean remove(String itemId) {
        return items.removeIf(i -> i.getId().equals(itemId));
    }

    public Item use(String itemId) {
        Item item = items.stream().filter(i -> i.getId().equals(itemId)).findFirst().orElse(null);
        if (item != null) items.remove(item);
        return item;
    }

    public List<Item> getItems() { return items; }
    public int getCapacity() { return capacity; }
    public boolean isFull() { return items.size() >= capacity; }
    public int getSize() { return items.size(); }
}
