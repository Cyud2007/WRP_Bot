package com.code.commands;

import com.code.data.UserData;
import com.code.data.UserDataManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;

public class GiveMoneyCommand extends Command {

    @Override
    public CommandData createCommand() {
        return Commands.slash("givemoney", "Issue money to the user.")
                .addOption(net.dv8tion.jda.api.interactions.commands.OptionType.USER, "user", "The user who needs to be given money", true)
                .addOption(net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER, "amount", "Amount of money to be issued", true);
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        // Checking for the required rights (e.g. administrator or moderator)
        if (!event.getMember().hasPermission(net.dv8tion.jda.api.Permission.MANAGE_SERVER)) {
            event.reply("You do not have permission to execute this command.").queue();
            return;
        }

        String userName = event.getOption("user").getAsUser().getName();
        long amount = event.getOption("amount").getAsLong();

        if (amount <= 0) {
            event.reply("The amount must be positive.").queue();
            return;
        }

        UserData userData = UserDataManager.getUserData(userName);
        userData.setBalance(userData.getBalance() + amount);
        UserDataManager.updateUserData(userData);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("The money has been issued!");
        embedBuilder.setDescription("User " + userName + " was issued " + amount + " coins.");
        embedBuilder.setColor(Color.GREEN);

        event.replyEmbeds(embedBuilder.build()).queue();
    }
}
