package com.code.commands;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

public abstract class Command {

   
    public Command() {
    }

    // Method for creating command data
    public abstract CommandData createCommand();

    // Method for executing SlashCommand
    public abstract void execute(@NotNull SlashCommandInteractionEvent event);

    // Method for execution with text commands 
    public void execute(@NotNull MessageReceivedEvent event) {
    
    }
}

