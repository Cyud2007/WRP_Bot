package com.code.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import com.code.data.UserData;
import com.code.data.UserDataManager;
import com.code.shop.ShopItem;
import com.code.shop.ShopManager;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class BuyCommand extends Command {

    private static final Map<String, Integer> ECONOMY_LEVELS = new HashMap<>();

    static {
        // Инициализация уровней экономики
        ECONOMY_LEVELS.put("Экономика лв1", 1);
        ECONOMY_LEVELS.put("Экономика лв2", 2);
        ECONOMY_LEVELS.put("Экономика лв3", 3);
        ECONOMY_LEVELS.put("Экономика лв4", 4);
        ECONOMY_LEVELS.put("Экономика лв5", 5);
        ECONOMY_LEVELS.put("Экономика лв6", 6);
        ECONOMY_LEVELS.put("Экономика лв7", 7);
    }

    @Override
    public CommandData createCommand() {
        return Commands.slash("buy", "Купить товар в магазине.")
                .addOption(net.dv8tion.jda.api.interactions.commands.OptionType.STRING, "item", "Название товара", true)
                .addOption(net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER, "quantity", "Количество (по умолчанию 1)", false);
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        String username = event.getUser().getName();
        String itemName = event.getOption("item").getAsString();
        int quantity = 1; // По умолчанию количество 1

        // Проверяем, если игрок пытается купить экономику, запрещаем ввод количества
        if (itemName.toLowerCase().startsWith("экономика") && event.getOption("quantity") != null) {
            event.replyEmbeds(new EmbedBuilder()
                    .setTitle("Ошибка")
                    .setDescription("Для этой покупки невозможно указать количество.")
                    .setColor(Color.RED)
                    .build()).queue();
            return;
        }

        // Проверка на возможность покупки предыдущих уровней экономики
        if (itemName.toLowerCase().startsWith("экономика")) {
            if (!canPurchaseEconomyLevel(event, itemName)) {
                event.replyEmbeds(new EmbedBuilder()
                        .setTitle("Ошибка")
                        .setDescription("Вы не можете купить этот уровень экономики, так как у вас уже есть этот или более высокий уровень..")
                        .setColor(Color.RED)
                        .build()).queue();
                return;
            }
        }

        ShopItem shopItem = ShopManager.getItemByName(itemName);
        if (shopItem == null) {
            event.replyEmbeds(new EmbedBuilder()
                    .setTitle("Ошибка")
                    .setDescription("Такого товара нет в магазине.")
                    .setColor(Color.RED)
                    .build()).queue();
            return;
        }

        int totalPrice = shopItem.getPrice() * quantity;
        UserData userData = UserDataManager.getUserData(username);

        if (userData.getBalance() < totalPrice) {
            event.replyEmbeds(new EmbedBuilder()
                    .setTitle("Недостаточно средств")
                    .setDescription("У вас недостаточно средств для покупки.")
                    .setColor(Color.RED)
                    .build()).queue();
            return;
        }

        // Обновляем баланс игрока
        userData.setBalance(userData.getBalance() - totalPrice);

        // Логика покупки уровня экономики
        if (itemName.toLowerCase().startsWith("экономика")) {
            removeOldEconomyRoles(event, userData);  // Удаляем предыдущие роли экономики
            assignNewEconomyRole(event, itemName);  // Назначаем новую роль
        } else {
            userData.addToInventory(shopItem.getName(), quantity);
        }

        UserDataManager.updateUserData(userData);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Покупка успешна");
        embedBuilder.setColor(new Color(75, 0, 130));
        embedBuilder.setDescription("Вы купили " + shopItem.getName() + " (x" + quantity + ").");
        embedBuilder.addField("Стоимость", totalPrice + " монет", false);
        embedBuilder.addField("Оставшийся баланс", userData.getBalance() + " монет", false);

        event.replyEmbeds(embedBuilder.build()).queue();
    }

    private boolean canPurchaseEconomyLevel(SlashCommandInteractionEvent event, String itemName) {
        int newEconomyLevel = ECONOMY_LEVELS.getOrDefault(itemName, 0);

        for (String roleName : ECONOMY_LEVELS.keySet()) {
            if (event.getMember().getRoles().stream().anyMatch(role -> role.getName().equals(roleName))) {
                int existingEconomyLevel = ECONOMY_LEVELS.get(roleName);
                if (existingEconomyLevel >= newEconomyLevel) {
                    return false; // Нельзя купить более низкий или такой же уровень
                }
            }
        }

        return true; // Покупка разрешена
    }

    private void removeOldEconomyRoles(SlashCommandInteractionEvent event, UserData userData) {
        String[] economyRoles = {"Экономика лв1", "Экономика лв2", "Экономика лв3", "Экономика лв4", "Экономика лв5", "Экономика лв6", "Экономика лв7"};

        for (String roleName : economyRoles) {
            event.getGuild().getRolesByName(roleName, true).forEach(role -> {
                if (event.getMember().getRoles().contains(role)) {
                    event.getGuild().removeRoleFromMember(event.getMember(), role).queue();
                }
            });
        }
    }

    private void assignNewEconomyRole(SlashCommandInteractionEvent event, String itemName) {
        event.getGuild().getRolesByName(itemName, true).forEach(role -> {
            event.getGuild().addRoleToMember(event.getMember(), role).queue();
        });
    }
}
