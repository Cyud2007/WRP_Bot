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
            if (input == null) {
                throw new IllegalStateException("Не удалось найти конфигурационный файл.");
            }
            properties.load(input);
        } catch (Exception e) {
            logger.error("Ошибка при загрузке конфигурационного файла.", e);
        }
    }

    public static String getBotToken() {
        return properties.getProperty("bot.token");
    }

    public static String getRoleId() {
        return properties.getProperty("role.id");
    }
}
