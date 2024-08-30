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

    // Маппинг для технологий
    private static final Map<String, String> TECHNOLOGIES = new HashMap<>();

    static {
        // Инициализация уровней экономики
        ECONOMY_LEVELS.put("Экономика лв1", 1);
        ECONOMY_LEVELS.put("Экономика лв2", 2);
        ECONOMY_LEVELS.put("Экономика лв3", 3);
        ECONOMY_LEVELS.put("Экономика лв4", 4);
        ECONOMY_LEVELS.put("Экономика лв5", 5);
        ECONOMY_LEVELS.put("Экономика лв6", 6);
        ECONOMY_LEVELS.put("Экономика лв7", 7);

        // Инициализация технологий
        TECHNOLOGIES.put("Авиа Завод", "1279083348504875133"); // Пример ID роли
        TECHNOLOGIES.put("Танковый завод", "1279083322911494249"); // Пример ID роли
        TECHNOLOGIES.put("Верфь", "1279083382160228444"); // Пример ID роли
        TECHNOLOGIES.put("Авто завод", "1279083382160228444"); // Пример ID роли
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
        int quantity = event.getOption("quantity") != null ? event.getOption("quantity").getAsInt() : 1;

        // Проверяем, если игрок пытается купить экономику или технологию, запрещаем ввод количества
        if ((itemName.toLowerCase().startsWith("экономика") || TECHNOLOGIES.containsKey(itemName)) && event.getOption("quantity") != null && quantity != 1) {
            event.replyEmbeds(new EmbedBuilder()
                    .setTitle("Ошибка")
                    .setDescription("Для этой покупки можно указать количество только 1.")
                    .setColor(Color.RED)
                    .build()).queue();
            return;
        }

        // Проверка на возможность покупки предыдущих уровней экономики
        if (itemName.toLowerCase().startsWith("экономика")) {
            if (!canPurchaseEconomyLevel(event, itemName)) {
                event.replyEmbeds(new EmbedBuilder()
                        .setTitle("Ошибка")
                        .setDescription("Вы не можете купить этот уровень экономики, так как у вас уже есть этот или более высокий уровень.")
                        .setColor(Color.RED)
                        .build()).queue();
                return;
            }
        }

        // Проверка на возможность покупки технологии
        if (TECHNOLOGIES.containsKey(itemName) && !canPurchaseTechnology(event, itemName)) {
            event.replyEmbeds(new EmbedBuilder()
                    .setTitle("Ошибка")
                    .setDescription("Вы не можете купить эту технологию, так как она у вас уже есть.")
                    .setColor(Color.RED)
                    .build()).queue();
            return;
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
        } 
        // Логика покупки технологии
        else if (TECHNOLOGIES.containsKey(itemName)) {
            assignTechnologyRole(event, TECHNOLOGIES.get(itemName));  // Назначаем роль для технологии
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

    private boolean canPurchaseTechnology(SlashCommandInteractionEvent event, String itemName) {
        String roleId = TECHNOLOGIES.get(itemName);
        return event.getMember().getRoles().stream().noneMatch(role -> role.getId().equals(roleId));
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

    private void assignTechnologyRole(SlashCommandInteractionEvent event, String roleId) {
        event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById(roleId)).queue();
    }
}
