package com.code.commands;

import com.code.data.UserData;
import com.code.data.UserDataManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;

public class ViewBalanceCommand extends Command {

    @Override
    public CommandData createCommand() {
        return Commands.slash("v-balance", "Просмотреть баланс игрока.")
                .addOption(net.dv8tion.jda.api.interactions.commands.OptionType.USER, "user", "Пользователь, баланс которого нужно посмотреть", true);
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        String userName = event.getOption("user").getAsUser().getName();

        UserData userData = UserDataManager.getUserData(userName);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Баланс пользователя");
        embedBuilder.setDescription("Пользователь " + userName + " имеет " + userData.getBalance() + " монет.");
        embedBuilder.setColor(Color.BLUE);

        event.replyEmbeds(embedBuilder.build()).queue();
    }
}
