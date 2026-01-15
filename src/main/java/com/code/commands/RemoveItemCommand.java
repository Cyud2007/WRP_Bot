package com.code.commands;

import org.jetbrains.annotations.NotNull;

import com.code.data.UserData;
import com.code.data.UserDataManager;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class RemoveItemCommand extends Command {

    @Override
    public CommandData createCommand() {
        return Commands.slash("removeitem", "Removes an item from the user's inventory.")
                .addOption(OptionType.STRING, "nickname", "User nickname", true)
                .addOption(OptionType.STRING, "item", "Item name", true)
                .addOption(OptionType.INTEGER, "quantity", "Number of items", true);
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        String username = event.getOption("nickname").getAsString();
        String item = event.getOption("item").getAsString();
        int amount = event.getOption("quantity").getAsInt();

        UserData userData = UserDataManager.getUserData(username);
        boolean success = userData.removeItemFromInventory(item, amount);

        if (success) {
            event.reply("Removed " + amount + " pcs. " + item + " from inventory " + username + ".").queue();
        } else {
            event.reply("Failed to remove " + amount + " items " + item + " from inventory " + username + ". There may not be enough items.").queue();
        }
    }
}
