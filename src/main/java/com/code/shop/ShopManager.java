package com.code.shop;

import java.util.ArrayList;
import java.util.List;

public class ShopManager {
    private static final List<ShopItem> items = new ArrayList<>();

    static {
        // Изначальные товары магазина
        items.add(new ShopItem("Танк", 200));

    }

    public static List<ShopItem> getItems() {
        return items;
    }

    public static ShopItem getItemByName(String name) {
        for (ShopItem item : items) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }
}