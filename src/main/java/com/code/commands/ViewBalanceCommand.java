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
        return Commands.slash("v-balance", "View player balance.")
                .addOption(net.dv8tion.jda.api.interactions.commands.OptionType.USER, "user", "The user whose balance you want to view", true);
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        String userName = event.getOption("user").getAsUser().getName();

        UserData userData = UserDataManager.getUserData(userName);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("User balance");
        embedBuilder.setDescription("User " + userName + " has " + userData.getBalance() + " coins.");
        embedBuilder.setColor(Color.BLUE);

        event.replyEmbeds(embedBuilder.build()).queue();
    }
}
