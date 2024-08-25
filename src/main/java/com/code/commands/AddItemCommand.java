package com.code.commands;

import com.code.data.UserData;
import com.code.data.UserDataManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

public class AddItemCommand extends Command {

    @Override
    public CommandData createCommand() {
        return Commands.slash("additem", "Добавляет предмет в инвентарь пользователя.")
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
        userData.addItemToInventory(item, amount);

        event.reply("Добавлено " + amount + " шт. " + item + " в инвентарь " + username + ".").queue();
    }
}
