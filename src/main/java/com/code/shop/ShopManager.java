package com.code.shop;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ShopManager {
    private static final List<ShopItem> items = new ArrayList<>();

    static {
        // Предметы для категории "Армия"
        items.add(new ShopItem("Танк", 200, "army"));


        // Предметы для категории "Технологии"

        items.add(new ShopItem("Я хз", 800, "tech"));


        // Предметы для категории "Экономика"
        items.add(new ShopItem("Экономика лв1", 100, "economy"));
        items.add(new ShopItem("Экономика лв2", 500, "economy"));
        items.add(new ShopItem("Экономика лв3", 1000, "economy"));
        items.add(new ShopItem("Экономика лв4", 2000, "economy"));
        items.add(new ShopItem("Экономика лв5", 5000, "economy"));
        items.add(new ShopItem("Экономика лв6", 10000, "economy"));
        items.add(new ShopItem("Экономика лв7", 20000, "economy"));
        
   
    }

    public static List<ShopItem> getItems() {
        return items;
    }

    // Метод для получения предметов по категории
    public static List<ShopItem> getItems(String category) {
        return items.stream()
                .filter(item -> item.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
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
