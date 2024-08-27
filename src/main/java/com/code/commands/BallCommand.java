package com.code.commands;

import java.awt.Color;

import org.jetbrains.annotations.NotNull;

import com.code.data.UserData;
import com.code.data.UserDataManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class BallCommand extends Command {

    @Override
    public CommandData createCommand() {
        return Commands.slash("ball", "Показывает информацию про игрока.");
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
 
        String userId = event.getUser().getId();
        String username = event.getMember() != null ? event.getMember().getEffectiveName() : event.getUser().getName();

 
        UserData userData = UserDataManager.getUserData(userId);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Информация о Профиле");
        embedBuilder.setDescription(" + **Информация** \n Страна:" + username);
        embedBuilder.setColor(new Color(255, 165, 0)); 
        embedBuilder.addField("Баланс-ресуры", "Баланс: " + userData.getBalance(),  false);
        embedBuilder.setFooter("WPG Bot", null);


    
        event.replyEmbeds(embedBuilder.build()).queue();
    }
}
