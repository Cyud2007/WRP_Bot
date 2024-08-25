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
        return Commands.slash("removeitem", "Удаляет предмет из инвентаря пользователя.")
                .addOption(OptionType.STRING, "ник", "Никнейм пользователя", true)
                .addOption(OptionType.STRING, "предмет", "Название предмета", true)
                .addOption(OptionType.INTEGER, "количество", "Количество предметов", true);
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        String username = event.getOption("ник").getAsString();
        String item = event.getOption("предмет").getAsString();
        int amount = event.getOption("количество").getAsInt();

        UserData userData = UserDataManager.getUserData(username);
        boolean success = userData.removeItemFromInventory(item, amount);

        if (success) {
            event.reply("Удалено " + amount + " шт. " + item + " из инвентаря " + username + ".").queue();
        } else {
            event.reply("Не удалось удалить " + amount + " шт. " + item + " из инвентаря " + username + ". Возможно, недостаточно предметов.").queue();
        }
    }
}
