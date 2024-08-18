package com.code.commands;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

public abstract class Command {

    // Конструктор без параметров, если не требуется инициализация
    public Command() {
    }

    // Метод для создания данных команды
    public abstract CommandData createCommand();

    // Метод для выполнения команды в случае взаимодействия с SlashCommand
    public abstract void execute(@NotNull SlashCommandInteractionEvent event);

    // Метод для выполнения команды в случае взаимодействия с текстовыми командами (если нужно)
    public void execute(@NotNull MessageReceivedEvent event) {
        // Реализация, если нужно
    }
}

