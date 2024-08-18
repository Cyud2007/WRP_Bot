package com.code.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;

public class InfoCommand extends Command {

    @Override
    public CommandData createCommand() {
        return Commands.slash("info", "Отправляет информационное сообщение в формате embed.");
    }

    // Метод для обработки текстовых сообщений, если это нужно
    @Override
    public void execute(@NotNull MessageReceivedEvent event) {
        // Добавьте логику для обработки текстовых сообщений здесь, если необходимо
        // Например, отправка embed сообщения как ответ на текстовое сообщение
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Bot Information");
        embedBuilder.setDescription("This bot is designed to work with the WPG server.");
        embedBuilder.setColor(new Color(255, 165, 0)); // Оранжевый цвет линии
        embedBuilder.addField("Version", "2.0.1", false);
        embedBuilder.addField("Authors", "Cyud, poslaya_sairys", false);
        embedBuilder.setFooter("GWRP Bot", null);

        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }       // Метод для обработки команд через слэш
    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Bot Information");
        embedBuilder.setDescription("This bot is designed to work with the WPG server.");
        embedBuilder.setColor(new Color(255, 165, 0)); // Orange color for the line
        embedBuilder.addField("Version", "2.0.1", false);
        embedBuilder.addField("Authors", "Cyud, poslaya_sairys", false);
        embedBuilder.setFooter("WPG Bot", null);

        event.replyEmbeds(embedBuilder.build()).queue();
    }
}