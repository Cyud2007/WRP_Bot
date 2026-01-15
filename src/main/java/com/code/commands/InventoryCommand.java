package com.code.commands;

import java.awt.Color;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import com.code.data.UserData;
import com.code.data.UserDataManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class InventoryCommand extends Command {

    @Override
    public CommandData createCommand() {
        return Commands.slash("inventory", "Shows your inventory.");
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        String username = event.getUser().getName();
        UserData userData = UserDataManager.getUserData(username);
        Map<String, Integer> inventory = userData.getInventory();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Your inventory");
        embedBuilder.setColor(new Color(75, 0, 130)); 

        if (inventory.isEmpty()) {
            embedBuilder.setDescription("Your inventory is empty.");
        } else {
            StringBuilder inventoryDescription = new StringBuilder();
            for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
                inventoryDescription.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            embedBuilder.setDescription(inventoryDescription.toString());
        }

        event.replyEmbeds(embedBuilder.build()).queue();
    }
}
