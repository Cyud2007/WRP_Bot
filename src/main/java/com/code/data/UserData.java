package com.code.data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class UserData {
    private final String username;
    private long balance; // Изменен тип с int на long
    private LocalDateTime lastJobTime; // Поле для отслеживания последнего использования команды /job
    private final Map<String, Integer> inventory;

    public UserData(String username, long balance, String inventoryData) { // Изменен тип с int на long
        this.username = username;
        this.balance = balance;
        this.inventory = parseInventory(inventoryData);
        this.lastJobTime = LocalDateTime.now().minusDays(1); // Инициализация так, чтобы команда была доступна сразу
    }

    public String getUsername() {
        return username;
    }

    public long getBalance() { // Изменен тип с int на long
        return balance;
    }

    public void setBalance(long balance) { // Изменен тип с int на long
        this.balance = balance;
    }

    public void addToBalance(long amount) { // Изменен тип с int на long
        this.balance += amount;
    }

    public void subtractFromBalance(long amount) { // Изменен тип с int на long
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
                '}';
    }
}
