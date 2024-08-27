package com.code.commands;

import java.awt.Color;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

public class SelectMenuCommand extends Command {

    @Override
    public CommandData createCommand() {
        return Commands.slash("testmenu", "test menu.");
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        StringSelectMenu menu = StringSelectMenu.create("menu:category")
                .setPlaceholder("Выберите категорию")
                .addOption("Информация о боте", "bot_info", "Узнать информацию о боте", Emoji.fromUnicode("ℹ️"))
                .build();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Выберите категорию");
        embedBuilder.setDescription("Используйте меню ниже, чтобы выбрать категорию.");
        embedBuilder.setColor(new Color(255, 165, 0)); // Оранжевый цвет

        event.replyEmbeds(embedBuilder.build())
             .addActionRow(menu)
             .setEphemeral(false) // Сообщение будет видимо всем
             .queue();
    }

    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        String selected = event.getValues().get(0);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(new Color(255, 165, 0)); // Оранжевый цвет

        switch (selected) {
            case "bot_info":
                embedBuilder.setTitle("Информация о боте");
                embedBuilder.setDescription("Этот бот был разработан для работы с сервером WPG.");
                embedBuilder.addField("Версия", "Beta 0.2 ", false);
                embedBuilder.addField("Авторы", "Cyud, poslaya_sairys", false);
                break;
            default:
                embedBuilder.setTitle("Неизвестная категория");
                embedBuilder.setDescription("Выбранная категория не распознана.");
                break;
        }

        event.editMessageEmbeds(embedBuilder.build()).queue(); // Обновляем сообщение
    }
}
