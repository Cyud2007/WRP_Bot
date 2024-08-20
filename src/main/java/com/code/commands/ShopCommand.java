package com.code.commands;

import java.awt.Color;

import org.jetbrains.annotations.NotNull;

import com.code.shop.ShopItem;
import com.code.shop.ShopManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class ShopCommand extends Command {

    @Override
    public CommandData createCommand() {
        return Commands.slash("shop", "Просмотр доступных товаров в магазине.");
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Магазин");
        embedBuilder.setDescription("Доступные товары для покупки:");
        embedBuilder.setColor(new Color(75, 0, 130)); 

        for (ShopItem item : ShopManager.getItems()) {
            embedBuilder.addField(item.getName(), item.getPrice() + " монет", false);
        }

        embedBuilder.setFooter("/buy <название> для покупки.", null);

        event.replyEmbeds(embedBuilder.build()).queue();
    }
}
