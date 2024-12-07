package com.code.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("bot.properties")) {
            if (input != null) {
                properties.load(input);
            } else {
                logger.warn("bot.properties не найден, используются переменные окружения.");
            }
        } catch (Exception e) {
            logger.error("Ошибка при загрузке bot.properties.", e);
        }
    }

    public static String getBotToken() {
        // Попытка получить токен из переменной окружения
        String envToken = System.getenv("BOT_TOKEN");
        if (envToken != null && !envToken.isEmpty()) {
            return envToken;
        }

        // Альтернатива: из файла bot.properties
        return properties.getProperty("bot.token");
    }

    public static String getRoleId() {
        return properties.getProperty("role.id");
    }
}
