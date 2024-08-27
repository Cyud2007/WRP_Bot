package com.code.shop;

public class ShopItem {
    private final String name;
    private final int price;
    private final String category; // Новое поле для категории

    public ShopItem(String name, int price, String category) {
        this.name = name;
        this.price = price;
        this.category = category; // Инициализация категории
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getCategory() {
        return category; // Возвращаем категорию
    }
}
