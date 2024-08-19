package com.code.data;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserDataManager {
    private static final String DATA_FILE = "data.txt";
    private static final Map<String, UserData> usersData = new HashMap<>();

    // Метод для загрузки данных
    public static void loadData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 3) {
                    String username = parts[0];
                    int balance = Integer.parseInt(parts[1]);
                    String inventoryData = parts[2];
                    usersData.put(username, new UserData(username, balance, inventoryData));
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to load data: " + e.getMessage());
        }
    }

    // Метод для сохранения данных
    public static void saveData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (UserData data : usersData.values()) {
                writer.write(data.getUsername() + ";" + data.getBalance() + ";" + data.inventoryToString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Failed to save data: " + e.getMessage());
        }
    }

    // Метод для получения данных пользователя
    public static UserData getUserData(String username) {
        return usersData.computeIfAbsent(username, k -> new UserData(username, 0, ""));
    }
    
    // Метод для обновления данных пользователя
    public static void updateUserData(UserData userData) {
        usersData.put(userData.getUsername(), userData);
        saveData();
    }
}
