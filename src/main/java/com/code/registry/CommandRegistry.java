package com.code.registry;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import com.code.commands.Command;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class CommandRegistry {

    private static final String GUILD_ID = "1269261946218348555"; // Замените на ID вашего сервера
    private static final Logger logger = LoggerFactory.getLogger(CommandRegistry.class);

    public static void registerCommands(@NotNull JDA jda) {
        Guild guild = jda.getGuildById(GUILD_ID);
        if (guild != null) {
            List<CommandData> commands = discoverCommands();
            commands.forEach(commandData -> guild.upsertCommand(commandData).queue(
                    _ -> logger.info("Command registered: {}", commandData.getName()),
                    error -> logger.error("Failed to register command: {}", commandData.getName(), error)
            ));
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
