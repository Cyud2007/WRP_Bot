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
        return Commands.slash("work", "Выдает 2000 монет раз в сутки.");
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
            embed.setTitle("💰 Заработок успешен!");
            embed.setDescription("Вы заработали 2000 монет.");
            embed.addField("Ваш текущий баланс", userData.getBalance() + " монет", false);
            embed.setTimestamp(now);

            event.replyEmbeds(embed.build()).queue();
        } else {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.RED);
            embed.setTitle("⏳ Подождите немного!");
            embed.setDescription("Вы уже использовали эту команду сегодня. Попробуйте снова после полуночи.");
            embed.setTimestamp(now);

            event.replyEmbeds(embed.build()).queue();
        }
    }
}
