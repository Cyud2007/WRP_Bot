package com.code.commands;

import com.code.data.UserData;
import com.code.data.UserDataManager;
import com.code.shop.ShopItem;
import com.code.shop.ShopManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

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
            event.reply("Такого товара нет в магазине.").queue();
            return;
        }

        int price = shopItem.getPrice() * quantity;
        UserData userData = UserDataManager.getUserData(username);

        if (userData.getBalance() < price) {
            event.reply("У вас недостаточно средств.").queue();
            return;
        }

        userData.setBalance(userData.getBalance() - price);
        userData.addToInventory(shopItem.getName(), quantity);
        UserDataManager.updateUserData(userData);

        event.reply("Вы купили " + quantity + " " + shopItem.getName() + "(ов). Ваш текущий баланс: " + userData.getBalance() + " монет.").queue();
    }
}
