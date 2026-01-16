package com.code.shop;

public class ShopItem {
    private final String name;
    private final int price;
    private final String category; // New field for category

    public ShopItem(String name, int price, String category) {
        this.name = name;
        this.price = price;
        this.category = category; // Initializing a category
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getCategory() {
        return category; // Returning the category
    }
}
