package com.code.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class UserDataManager {
    private static final String DATA_FILE = "data.txt";
    private static final Map<String, UserData> usersData = new HashMap<>();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Загружаем данные при первом обращении к UserDataManager
    static {
        loadData();
    }

    public static void loadData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 7) {  // Ожидаем 7 частей данных
                    String username = parts[0];
                    long balance = Long.parseLong(parts[1]);
                    String inventoryData = parts[2];
                    int gold = Integer.parseInt(parts[3]);
                    int iron = Integer.parseInt(parts[4]);
                    int oil = Integer.parseInt(parts[5]);
                    LocalDateTime lastJobTime = LocalDateTime.parse(parts[6], formatter);

                    UserData userData = new UserData(username, balance, inventoryData);
                    userData.setGold(gold);
                    userData.setIron(iron);
                    userData.setOil(oil);
                    userData.setLastJobTime(lastJobTime);

                    usersData.put(username, userData);
                }
            }
            System.out.println("Data loaded successfully.");
        } catch (IOException e) {
            System.out.println("Failed to load data: " + e.getMessage());
        }
    }

    public static void saveData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (UserData data : usersData.values()) {
                writer.write(data.getUsername() + ";" + data.getBalance() + ";" + data.inventoryToString() + ";" +
                        data.getGold() + ";" + data.getIron() + ";" + data.getOil() + ";" +
                        data.getLastJobTime().format(formatter));
                writer.newLine();
            }
            System.out.println("Data saved successfully.");
        } catch (IOException e) {
            System.out.println("Failed to save data: " + e.getMessage());
        }
    }

    public static UserData getUserData(String username) {
        return usersData.computeIfAbsent(username, k -> new UserData(username, 0, ""));
    }

    public static void updateUserData(UserData userData) {
        usersData.put(userData.getUsername(), userData);
        saveData();  // Сохраняем данные после обновления
    }
}
