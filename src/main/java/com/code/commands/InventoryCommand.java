package com.code.commands;

import com.code.data.UserData;
import com.code.data.UserDataManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class InventoryCommand extends Command {

    @Override
    public CommandData createCommand() {
        return Commands.slash("inventory", "Показывает ваш инвентарь.");
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        String username = event.getUser().getName();
        UserData userData = UserDataManager.getUserData(username);
        Map<String, Integer> inventory = userData.getInventory();

        if (inventory.isEmpty()) {
            event.reply("Ваш инвентарь пуст.").queue();
        } else {
            StringBuilder inventoryMessage = new StringBuilder("Ваш инвентарь:\n");
            for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
                inventoryMessage.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            event.reply(inventoryMessage.toString()).queue();
        }
    }
}
