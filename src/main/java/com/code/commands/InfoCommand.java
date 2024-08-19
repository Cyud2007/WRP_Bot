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

    @Override
    public void execute(@NotNull MessageReceivedEvent event) {

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Информация о боте");
        embedBuilder.setDescription("Этот бот был разработан для работы с сервером WPG.");
        embedBuilder.setColor(new Color(255, 165, 0)); // Оранжевый цвет
        embedBuilder.addField("Версия", "Beta 0.2 ", false);
        embedBuilder.addField("Авторы", "Cyud, poslaya_sairys", false);
        embedBuilder.setFooter("GWRP Bot", null);

        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }
    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Информация о боте");
        embedBuilder.setDescription("Этот бот был разработан для работы с сервером WPG.");
        embedBuilder.setColor(new Color(255, 165, 0)); // Оранжевый цвет
        embedBuilder.addField("Версия", "Beta 0.2 ", false);
        embedBuilder.addField("Авторы", "Cyud, poslaya_sairys", false);
        embedBuilder.setFooter("WPG Bot", null);

        event.replyEmbeds(embedBuilder.build()).queue();
    }
}
