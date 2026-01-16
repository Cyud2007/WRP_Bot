package com.code;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.data.UserDataManager;
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
            // Getting a token from configuration
            String token = Config.getBotToken();
            if (token == null || token.isEmpty()) {
                throw new IllegalArgumentException("Токен бота не может быть пустым.");
            }

            // Initializing the bot with the necessary permissions and listeners
            JDABuilder builder = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_PRESENCES )
                    .setActivity(Activity.playing("WRP"))
                    .addEventListeners(new BotListener());

            JDA jda = builder.build();
            jda.awaitReady(); // Waiting for the bot to be fully ready

            if (jda.getStatus() != JDA.Status.CONNECTED) {
                logger.warn("Bot is not fully connected. Current status: " + jda.getStatus());
            } else {
                logger.info("Bot is fully connected and ready.");
            }

            // Team registration
            CommandRegistry.registerCommands(jda);

            // Add a Shutdown Hook to automatically save data when shutting down
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                UserDataManager.saveData(); // Saving data
                logger.info("Data saved successfully on shutdown.");
            }));


            System.out.println("The bot has been successfully launched and is ready to work..");
            logger.info("Bot is running ONREADY.");

        } catch (InterruptedException e) {
            logger.error("Error waiting for bot to be ready", e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logger.error("Error starting the bot", e);
        }
    }
}
