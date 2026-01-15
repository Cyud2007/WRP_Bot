package com.code.commands;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import org.jetbrains.annotations.NotNull;

import com.code.data.UserData;
import com.code.data.UserDataManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class WorkCommand extends Command {

    @Override
    public CommandData createCommand() {
        return Commands.slash("work", "Gives out 2000 coins once a day.");
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        String username = event.getUser().getName();
        UserData userData = UserDataManager.getUserData(username);

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Kiev"));

        if (userData.getLastJobTime().until(now, ChronoUnit.DAYS) >= 1) {
            userData.setBalance(userData.getBalance() + 2000);
            userData.setLastJobTime(now);
            UserDataManager.updateUserData(userData);

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.GREEN);
            embed.setTitle("💰 Earnings are successful!");
            embed.setDescription("You have earned 2000 coins.");
            embed.addField("Your current balance", userData.getBalance() + " coins", false);
            embed.setTimestamp(now);

            event.replyEmbeds(embed.build()).queue();
        } else {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.RED);
            embed.setTitle("⏳ Wait a bit!");
            embed.setDescription("You've already used this command today. Try again after midnight..");
            embed.setTimestamp(now);

            event.replyEmbeds(embed.build()).queue();
        }
    }
}
