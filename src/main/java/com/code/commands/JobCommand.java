package com.code.commands;

import com.code.data.UserData;
import com.code.data.UserDataManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

public class JobCommand extends Command {

    @Override
    public CommandData createCommand() {
        return Commands.slash("job", "выдает 2000 монет.");
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        String username = event.getUser().getName();
        UserData userData = UserDataManager.getUserData(username);
        userData.setBalance(userData.getBalance() + 2000);
        UserDataManager.updateUserData(userData);
        event.reply("Вы заработали 2000 монет. Ваш баланс: " + userData.getBalance() + " монет.").queue();
    }
}
