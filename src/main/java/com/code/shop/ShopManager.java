package com.code.shop;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ShopManager {
    private static final List<ShopItem> items = new ArrayList<>();

    static {
        // Items for the category "Army"
        items.add(new ShopItem("Infantry", 500, "army"));
        items.add(new ShopItem("Special forces", 1000, "army"));
        items.add(new ShopItem("Armored personnel carrier", 5000, "army"));
        items.add(new ShopItem("Light multi-purpose vehicle", 4000, "army"));
        items.add(new ShopItem("Reconnaissance vehicle", 6000, "army"));
        items.add(new ShopItem("Main battle tank", 20000, "army"));
        items.add(new ShopItem("Self-propelled artillery unit", 18000, "army"));
        items.add(new ShopItem("Infantry fighting vehicle", 15000, "army"));
        items.add(new ShopItem("Multiple launch rocket system", 25000, "army"));
        items.add(new ShopItem("Engineering obstacle clearance vehicle", 22000, "army"));
        items.add(new ShopItem("Anti-aircraft missile and gun system", 30000, "army"));
        items.add(new ShopItem("Anti-aircraft missile system", 35000, "army"));
        items.add(new ShopItem("Fighter", 50000, "army"));
        items.add(new ShopItem("Bomber", 60000, "army"));
        items.add(new ShopItem("Stormtrooper", 55000, "army"));
        items.add(new ShopItem("Fire support helicopter", 45000, "army"));
        items.add(new ShopItem("Military transport aircraft", 40000, "army"));
        items.add(new ShopItem("Aircraft carrier", 100000, "army"));
        items.add(new ShopItem("Guided missile cruiser", 90000, "army"));
        items.add(new ShopItem("Ballistic missile submarine", 95000, "army"));
        items.add(new ShopItem("Intercontinental ballistic missile", 200000, "army"));
        items.add(new ShopItem("Strategic bomber", 250000, "army"));


        // Items for the "Technology" category

        items.add(new ShopItem("Aircraft plant", 80000, "tech"));
        items.add(new ShopItem("Car plant", 100, "tech"));
        items.add(new ShopItem("Tank factory", 1000, "tech"));
        items.add(new ShopItem("Shipyard", 50000, "tech"));
        items.add(new ShopItem("Nuclear complex", 150000, "tech"));


        // Items for the category "Economics"
        items.add(new ShopItem("Economy lv2", 500, "economy"));
        items.add(new ShopItem("Economy lv3", 1000, "economy"));
        items.add(new ShopItem("Economy lv4", 2000, "economy"));
        items.add(new ShopItem("Economy lv5", 5000, "economy"));
        items.add(new ShopItem("Economy lv6", 10000, "economy"));
        items.add(new ShopItem("Economy lv7", 20000, "economy"));
        
   
    }

    public static List<ShopItem> getItems() {
        return items;
    }

    // Method for getting items by category
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
