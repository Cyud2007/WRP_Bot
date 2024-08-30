package com.code.registry;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.commands.Command;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;



public class CommandRegistry {

    private static final String GUILD_ID = "1269261946218348555"; 
    private static final Logger logger = LoggerFactory.getLogger(CommandRegistry.class);

    public static void registerCommands(@NotNull JDA jda) {
        Guild guild = jda.getGuildById(GUILD_ID);
        if (guild != null) {
            List<CommandData> newCommands = discoverCommands();

       
            guild.retrieveCommands().queue(existingCommands -> {
      
                List<String> existingCommandNames = existingCommands.stream()
                        .map(command -> command.getName())
                        .collect(Collectors.toList());

                List<String> newCommandNames = newCommands.stream()
                        .map(CommandData::getName)
                        .collect(Collectors.toList());

                List<String> commandsToRemove = existingCommandNames.stream()
                        .filter(name -> !newCommandNames.contains(name))
                        .collect(Collectors.toList());

                // Удаляем устаревшие команды
                commandsToRemove.forEach(name -> {
                    existingCommands.stream()
                            .filter(command -> command.getName().equals(name))
                            .findFirst()
                            .ifPresent(command -> guild.deleteCommandById(command.getId()).queue(
                                    success -> logger.info("Command removed: {}", command.getName()),
                                    error -> logger.error("Failed to remove command: {}", command.getName(), error)
                            ));
                });

                newCommands.forEach(commandData -> guild.upsertCommand(commandData).queue(
                        success -> logger.info("Command registered: {}", commandData.getName()),
                        error -> logger.error("Failed to register command: {}", commandData.getName(), error)
                ));
            });

        } else {
            logger.error("Guild not found!");
        }
    }

    @NotNull
    private static List<CommandData> discoverCommands() {
        List<CommandData> commands = new ArrayList<>();
        try {
            Class<?>[] classes = {
                    Class.forName("com.code.commands.PingCommand"),
                    Class.forName("com.code.commands.InfoCommand"),
                    Class.forName("com.code.commands.ShopCommand"),
                    Class.forName("com.code.commands.JobCommand"),
                    Class.forName("com.code.commands.InventoryCommand"),
                    Class.forName("com.code.commands.BalanceCommand"),
                    Class.forName("com.code.commands.BuyCommand"),
                    Class.forName("com.code.commands.GiveMoneyCommand"),
                    Class.forName("com.code.commands.RemoveMoneyCommand"),
                    Class.forName("com.code.commands.ViewBalanceCommand"),
                    Class.forName("com.code.commands.RemoveItemCommand"),
                    Class.forName("com.code.commands.AddItemCommand"),
                    Class.forName("com.code.commands.GiveMoneyCommand"),
                    Class.forName("com.code.commands.SelectMenuCommand"),
                    Class.forName("com.code.commands.CollectCommand"),
                    Class.forName("com.code.commands.BallCommand"),
                    Class.forName("com.code.commands.PayCommand"),
                    Class.forName("com.code.commands.MuteCommand")
                
            };
            for (Class<?> clazz : classes) {
                if (Command.class.isAssignableFrom(clazz)) {
                    Constructor<?> constructor = clazz.getConstructor();
                    Command command = (Command) constructor.newInstance();
                    commands.add(command.createCommand());
                }
            }
        } catch (Exception e) {
            logger.error("Error discovering commands", e);
        }
        return commands;
    }
}