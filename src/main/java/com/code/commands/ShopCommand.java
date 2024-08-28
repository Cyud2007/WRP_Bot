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

public class ShopCommand extends Command {

    @Override
    public CommandData createCommand() {
        return Commands.slash("shop", "test menu.");
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        StringSelectMenu menu = StringSelectMenu.create("menu:shop")
                .setPlaceholder("Выберите категорию")
                .addOption("Армия", "army", Emoji.fromUnicode("🏳️‍🌈"))
                .addOption("Технологии", "tech", Emoji.fromUnicode("🚄"))
                .addOption("Экономика", "economy", Emoji.fromUnicode("🏭"))
                .build();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Выберите категорию");
        embedBuilder.setDescription("Используйте меню ниже, чтобы выбрать категорию.");
        embedBuilder.setColor(new Color(255, 165, 3)); // Оранжевый цвет

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
            case "army":
                embedBuilder.setTitle("Товары в категории Армия");
                embedBuilder.setDescription("Типо да");
                break;
            case "tech":
                embedBuilder.setTitle("Товары в категории **Технологии**");
                embedBuilder.setDescription("Типо да");
                break;
            case "economy":
                embedBuilder.setTitle("Товары в категории **Экономика**");
                embedBuilder.setDescription("Доступные уровни экономики:\n" +
                        "- Экономика лв1\n" +
                        "- Экономика лв2\n" +
                        "- Экономика лв3\n" +
                        "- Экономика лв4\n" +
                        "- Экономика лв5\n" +
                        "- Экономика лв6\n" +
                        "- Экономика лв7");
                break;
            default:
                embedBuilder.setTitle("Неизвестная категория");
                embedBuilder.setDescription("Выбранная категория не распознана.");
                break;
        }

        event.editMessageEmbeds(embedBuilder.build()).queue(); // Обновляем сообщение
    }
}
