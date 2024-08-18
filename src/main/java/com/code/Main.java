package com.code;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.registry.CommandRegistry;
import com.code.utils.Config;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        try {
            // Получение токена из конфигурации
            String token = Config.getBotToken();
            if (token == null || token.isEmpty()) {
                throw new IllegalArgumentException("Токен бота не может быть пустым.");
            }

            // Создание и настройка JDA
            JDABuilder builder = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_PRESENCES)
                    .setActivity(Activity.playing("WRP"))
                    .addEventListeners(new BotListener());

            JDA jda = builder.build();
            jda.awaitReady(); 

            // Регистрация команд
            CommandRegistry.registerCommands(jda);

            System.out.println("Бот успешно запущен и готов к работе.");

        } catch (InterruptedException e) {
            logger.error("Ошибка ожидания готовности бота", e);
            Thread.currentThread().interrupt(); 
        } catch (Exception e) {
            logger.error("Ошибка при запуске бота", e);
        }
    }
}
