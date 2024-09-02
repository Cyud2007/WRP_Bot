package com.code.data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class UserData {
    private final String username;
    private long balance;
    private LocalDateTime lastJobTime;
    private final Map<String, Integer> inventory;

    // Новые поля для ресурсов
    private int gold;
    private int iron;
    private int oil;

    public UserData(String username, long balance, String inventoryData) {
        this.username = username;
        this.balance = balance;
        this.inventory = parseInventory(inventoryData);
        this.lastJobTime = LocalDateTime.now().minusDays(1);
    }

    public String getUsername() {
        return username;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public void addToBalance(long amount) {
        this.balance += amount;
    }

    public void subtractFromBalance(long amount) {
        if (this.balance >= amount) {
            this.balance -= amount;
        } else {
            throw new IllegalArgumentException("Недостаточно средств.");
        }
    }

    public LocalDateTime getLastJobTime() {
        return lastJobTime;
    }

    public void setLastJobTime(LocalDateTime lastJobTime) {
        this.lastJobTime = lastJobTime;
    }

    public Map<String, Integer> getInventory() {
        return inventory;
    }

    // Методы для работы с золотом
    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public void addGold(int amount) {
        this.gold += amount;
    }

    // Методы для работы с железом
    public int getIron() {
        return iron;
    }

    public void setIron(int iron) {
        this.iron = iron;
    }

    public void addIron(int amount) {
        this.iron += amount;
    }

    // Методы для работы с нефтью
    public int getOil() {
        return oil;
    }

    public void setOil(int oil) {
        this.oil = oil;
    }

    public void addOil(int amount) {
        this.oil += amount;
    }

    // Добавляет предмет в инвентарь
    public void addItemToInventory(String item, int quantity) {
        inventory.put(item, inventory.getOrDefault(item, 0) + quantity);
    }

    // Удаляет предмет из инвентаря
    public boolean removeItemFromInventory(String item, int quantity) {
        int currentQuantity = inventory.getOrDefault(item, 0);
        if (currentQuantity >= quantity) {
            inventory.put(item, currentQuantity - quantity);
            if (inventory.get(item) == 0) {
                inventory.remove(item);
            }
            return true;
        } else {
            return false;
        }
    }

    public void addToInventory(String item, int quantity) {
        inventory.put(item, inventory.getOrDefault(item, 0) + quantity);
    }

    public void removeFromInventory(String item, int quantity) {
        int currentQuantity = inventory.getOrDefault(item, 0);
        if (currentQuantity > quantity) {
            inventory.put(item, currentQuantity - quantity);
        } else {
            inventory.remove(item);
        }
    }

    public String inventoryToString() {
        return inventory.entrySet().stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue())
                .collect(Collectors.joining(","));
    }

    private Map<String, Integer> parseInventory(String inventoryData) {
        Map<String, Integer> parsedInventory = new HashMap<>();
        if (inventoryData.isEmpty()) return parsedInventory;

        String[] items = inventoryData.split(",");
        for (String item : items) {
            String[] parts = item.split(":");
            if (parts.length == 2) {
                String itemName = parts[0];
                try {
                    int quantity = Integer.parseInt(parts[1]);
                    parsedInventory.put(itemName, quantity);
                } catch (NumberFormatException e) {
                    System.err.println("Ошибка формата количества для товара: " + item);
                }
            }
        }
        return parsedInventory;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "username='" + username + '\'' +
                ", balance=" + balance +
                ", lastJobTime=" + lastJobTime +
                ", inventory=" + inventory +
                ", gold=" + gold +
                ", iron=" + iron +
                ", oil=" + oil +
                '}';
    }
}
