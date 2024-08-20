package com.code.commands;

import java.awt.Color;

import org.jetbrains.annotations.NotNull;

import com.code.data.UserData;
import com.code.data.UserDataManager;
import com.code.shop.ShopItem;
import com.code.shop.ShopManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class BuyCommand extends Command {

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

        ShopItem shopItem = ShopManager.getItemByName(itemName);
        if (shopItem == null) {
            event.replyEmbeds(new EmbedBuilder()
                    .setTitle("Ошибка")
                    .setDescription("Такого товара нет в магазине.")
                    .setColor(Color.RED)
                    .build()).queue();
            return;
        }

        int price = shopItem.getPrice() * quantity;
        UserData userData = UserDataManager.getUserData(username);

        if (userData.getBalance() < price) {
            event.replyEmbeds(new EmbedBuilder()
                    .setTitle("Недостаточно средств")
                    .setDescription("У вас недостаточно средств для покупки.")
                    .setColor(Color.RED)
                    .build()).queue();
            return;
        }

      
        userData.setBalance(userData.getBalance() - price);
        userData.addToInventory(shopItem.getName(), quantity);
        UserDataManager.updateUserData(userData);

      
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Покупка успешна");
        embedBuilder.setColor(new Color(75, 0, 130)); // Зеленый цвет для успешного действия
        embedBuilder.setDescription("Вы купили " + quantity + " " + shopItem.getName() + "(ов).");
        embedBuilder.addField("Стоимость", price + " монет", false);
        embedBuilder.addField("Оставшийся баланс", userData.getBalance() + " монет", false);

        event.replyEmbeds(embedBuilder.build()).queue();
    }
}
