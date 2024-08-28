package com.code.shop;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ShopManager {
    private static final List<ShopItem> items = new ArrayList<>();

    static {
        // Предметы для категории "Армия"
        items.add(new ShopItem("Пехота", 500, "army"));
        items.add(new ShopItem("Спецназ", 1000, "army"));
        items.add(new ShopItem("Бронетранспортёр", 5000, "army"));
        items.add(new ShopItem("Легкий многоцелевой автомобиль", 4000, "army"));
        items.add(new ShopItem("Разведывательная машина", 6000, "army"));
        items.add(new ShopItem("Основной боевой танк", 20000, "army"));
        items.add(new ShopItem("Самоходная артиллерийская установка", 18000, "army"));
        items.add(new ShopItem("Боевая машина пехоты", 15000, "army"));
        items.add(new ShopItem("Ракетная система залпового огня", 25000, "army"));
        items.add(new ShopItem("Инженерная машина разграждения", 22000, "army"));
        items.add(new ShopItem("Зенитный ракетно-пушечный комплекс", 30000, "army"));
        items.add(new ShopItem("Зенитно-ракетный комплекс", 35000, "army"));
        items.add(new ShopItem("Истребитель", 50000, "army"));
        items.add(new ShopItem("Бомбардировщик", 60000, "army"));
        items.add(new ShopItem("Штурмовик", 55000, "army"));
        items.add(new ShopItem("Вертолет огневой поддержки", 45000, "army"));
        items.add(new ShopItem("Военно-транспортный самолет", 40000, "army"));
        items.add(new ShopItem("Авианосец", 100000, "army"));
        items.add(new ShopItem("Ракетный крейсер", 90000, "army"));
        items.add(new ShopItem("Подводная лодка с баллистическими ракетами", 95000, "army"));
        items.add(new ShopItem("Межконтинентальная баллистическая ракета", 200000, "army"));
        items.add(new ShopItem("Стратегический бомбардировщик", 250000, "army"));


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
