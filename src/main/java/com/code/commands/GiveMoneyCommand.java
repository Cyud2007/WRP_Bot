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
        return Commands.slash("givemoney", "Выдать деньги пользователю.")
                .addOption(net.dv8tion.jda.api.interactions.commands.OptionType.USER, "user", "Пользователь, которому нужно выдать деньги", true)
                .addOption(net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER, "amount", "Сумма денег для выдачи", true);
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        // Проверка на наличие необходимых прав (например, администратор или модератор)
        if (!event.getMember().hasPermission(net.dv8tion.jda.api.Permission.MANAGE_SERVER)) {
            event.reply("У вас нет прав для выполнения этой команды.").queue();
            return;
        }

        String userName = event.getOption("user").getAsUser().getName();
        int amount = event.getOption("amount").getAsInt();

        if (amount <= 0) {
            event.reply("Сумма должна быть положительной.").queue();
            return;
        }

        UserData userData = UserDataManager.getUserData(userName);
        userData.setBalance(userData.getBalance() + amount);
        UserDataManager.updateUserData(userData);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Деньги выданы!");
        embedBuilder.setDescription("Пользователю " + userName + " выдано " + amount + " монет.");
        embedBuilder.setColor(Color.GREEN);

        event.replyEmbeds(embedBuilder.build()).queue();
    }
}
