package com.code.commands;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

public abstract class Command {

   
    public Command() {
    }

    // Метод для создания данных команды
    public abstract CommandData createCommand();

    // Метод для выполнения SlashCommand
    public abstract void execute(@NotNull SlashCommandInteractionEvent event);

    // Метод для выполнения с текстовыми командами 
    public void execute(@NotNull MessageReceivedEvent event) {
    
    }
}

