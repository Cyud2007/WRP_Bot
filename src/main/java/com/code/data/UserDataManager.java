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
                    if (parts.length == 4) {  // Ожидаем 4 части
                        String username = parts[0];
                        int balance = Integer.parseInt(parts[1]);
                        String inventoryData = parts[2];
                        LocalDateTime lastJobTime = LocalDateTime.parse(parts[3], formatter);
                        UserData userData = new UserData(username, balance, inventoryData);
                        userData.setLastJobTime(lastJobTime);  // Загружаем время последнего использования команды /job
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
                            data.getLastJobTime().format(formatter));  // Сохраняем время последнего использования команды /job
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
