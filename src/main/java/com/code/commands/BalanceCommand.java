package com.code.commands;

import java.awt.Color;

import org.jetbrains.annotations.NotNull;

import com.code.data.UserData;
import com.code.data.UserDataManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class BalanceCommand extends Command {

    @Override
    public CommandData createCommand() {
        return Commands.slash("balance", "Показывает баланс.");
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        String username = event.getUser().getName();
        UserData userData = UserDataManager.getUserData(username);
        int balance = userData.getBalance();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Ваш Баланс");
        embedBuilder.setColor(new Color(75, 0, 130));
        embedBuilder.setDescription("Ваш текущий баланс составляет:");
        embedBuilder.addField("Баланс", balance + " монет", false);

        
        event.replyEmbeds(embedBuilder.build())
             .setEphemeral(true) 
             .queue();
    }
}
