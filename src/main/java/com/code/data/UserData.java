package com.code.data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class UserData {
    private final String username;
    private long balance;
    private LocalDateTime lastJobTime;  // Last time the command was used
    private final Map<String, Integer> inventory;

    // New fields for resources
    private int gold;
    private int iron;
    private int oil;

    public static final int COOLDOWN_HOURS = 18;  // Cooldown time in hours

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
            throw new IllegalArgumentException("Insufficient funds.");
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

    // Methods for working with gold
    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public void addGold(int amount) {
        this.gold += amount;
    }

    // Methods for working with iron
    public int getIron() {
        return iron;
    }

    public void setIron(int iron) {
        this.iron = iron;
    }

    public void addIron(int amount) {
        this.iron += amount;
    }

    // Methods for working with oil
    public int getOil() {
        return oil;
    }

    public void setOil(int oil) {
        this.oil = oil;
    }

    public void addOil(int amount) {
        this.oil += amount;
    }

    // Adds an item to the inventory
    public void addItemToInventory(String item, int quantity) {
        inventory.put(item, inventory.getOrDefault(item, 0) + quantity);
    }

    // Removes an item from your inventory.
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
                    System.err.println("Error in quantity format for product: " + item);
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

    public boolean canCollect() {
        LocalDateTime now = LocalDateTime.now();
        return lastJobTime.isBefore(now.minusHours(COOLDOWN_HOURS));
    }

    public void updateLastJobTime() {
        this.lastJobTime = LocalDateTime.now();
    }
}
