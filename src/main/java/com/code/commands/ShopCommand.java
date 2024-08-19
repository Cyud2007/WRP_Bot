package com.code.commands;

import com.code.shop.ShopItem;
import com.code.shop.ShopManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

public class ShopCommand extends Command {

    @Override
    public CommandData createCommand() {
        return Commands.slash("shop", "Просмотр доступных товаров в магазине.");
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        StringBuilder shopItems = new StringBuilder("Доступные товары:\n");
        for (ShopItem item : ShopManager.getItems()) {
            shopItems.append(item.getName()).append(" - ").append(item.getPrice()).append(" монет\n");
        }
        event.reply(shopItems.toString()).queue();
    }
}
