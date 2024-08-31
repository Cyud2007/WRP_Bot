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
                embedBuilder.setDescription(
                        "0. **Личный состав**:\n" +
                                "   - Пехота (Цена: 500 монет)\n" +
                                "   - Спецназ (Цена: 1000 монет)\n\n" +

                                "1. **Лёгкая техника**:\n" +
                                "   - Бронетранспортёр (Цена: 5000 монет)\n" +
                                "   - Легкий многоцелевой автомобиль (Цена: 4000 монет)\n" +
                                "   - Разведывательная машина (Цена: 6000 монет)\n\n" +

                                "2. **Тяжёлая техника**:\n" +
                                "   - Основной боевой танк (Цена: 20000 монет)\n" +
                                "   - Самоходная артиллерийская установка (Цена: 18000 монет)\n" +
                                "   - Боевая машина пехоты (Цена: 15000 монет)\n" +
                                "   - Ракетная система залпового огня (Цена: 25000 монет)\n" +
                                "   - Инженерная машина разграждения (Цена: 22000 монет)\n\n" +

                                "3. **Системы ПВО**:\n" +
                                "   - Зенитный ракетно-пушечный комплекс (Цена: 30000 монет)\n" +
                                "   - Зенитно-ракетный комплекс (Цена: 35000 монет)\n\n" +

                                "4. **Воздушная техника**:\n" +
                                "   - Истребитель (Цена: 50000 монет)\n" +
                                "   - Бомбардировщик (Цена: 60000 монет)\n" +
                                "   - Штурмовик (Цена: 55000 монет)\n" +
                                "   - Вертолет огневой поддержки (Цена: 45000 монет)\n" +
                                "   - Военно-транспортный самолет (Цена: 40000 монет)\n\n" +

                                "5. **Морская техника**:\n" +
                                "   - Авианосец (Цена: 100000 монет)\n" +
                                "   - Ракетный крейсер (Цена: 90000 монет)\n" +
                                "   - Подводная лодка с баллистическими ракетами (Цена: 95000 монет)\n\n" +

                                "6. **Ядерная техника**:\n" +
                                "   - Межконтинентальная баллистическая ракета (Цена: 200000 монет)\n" +
                                "   - Стратегический бомбардировщик (Цена: 250000 монет)\n"
                );
                break;
            case "tech":
                embedBuilder.setTitle("Товары в категории **Технологии**");
                embedBuilder.setDescription("Доступные Технологии:\n" +
                        "- Авто завод (Цена: 500 монет)\n" +
                        "- Танковый завод  (Цена: 1000 монет)\n" +
                        "- Авиа завод (Цена: 2000 монет)\n" +
                        "- Верфь  (Цена: 5000 монет)\n" +
                        "- Ядерный комплекс (Цена: 10000 монет)");
                break;
            case "economy":
                embedBuilder.setTitle("Товары в категории **Экономика**");
                embedBuilder.setDescription("Доступные уровни экономики:\n" +
                        "- Экономика лв2 (Цена: 500 монет)\n" +
                        "- Экономика лв3 (Цена: 1000 монет)\n" +
                        "- Экономика лв4 (Цена: 2000 монет)\n" +
                        "- Экономика лв5 (Цена: 5000 монет)\n" +
                        "- Экономика лв6 (Цена: 10000 монет)\n" +
                        "- Экономика лв7 (Цена: 20000 монет)");
                break;
            default:
                embedBuilder.setTitle("Неизвестная категория");
                embedBuilder.setDescription("Выбранная категория не распознана.");
                break;
        }

        event.editMessageEmbeds(embedBuilder.build()).queue(); // Обновляем сообщение
    }
}
