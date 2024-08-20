package com.code.data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class UserData {
    private final String username;
    private int balance;
    private LocalDateTime lastJobTime;  // Добавляем поле для отслеживания последнего использования команды /job
    private final Map<String, Integer> inventory;

    public UserData(String username, int balance, String inventoryData) {
        this.username = username;
        this.balance = balance;
        this.inventory = parseInventory(inventoryData);
        this.lastJobTime = LocalDateTime.now().minusDays(1); // Инициализируем так, чтобы команда была доступна сразу
    }

    public String getUsername() {
        return username;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
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
                int quantity = Integer.parseInt(parts[1]);
                parsedInventory.put(itemName, quantity);
            }
        }
        return parsedInventory;
    }
}
