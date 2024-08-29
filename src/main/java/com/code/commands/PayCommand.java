package com.code.commands;

import com.code.data.UserData;
import com.code.data.UserDataManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;

public class PayCommand extends Command {

    @Override
    public CommandData createCommand() {
        return Commands.slash("pay", "Передать деньги другому пользователю.")
                .addOption(net.dv8tion.jda.api.interactions.commands.OptionType.USER, "user", "Пользователь, которому нужно передать деньги", true)
                .addOption(net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER, "amount", "Сумма денег для передачи", true);
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        String senderName = event.getMember().getUser().getName();
        String receiverName = event.getOption("user").getAsUser().getName();
        long amount = event.getOption("amount").getAsLong();

        if (amount <= 0) {
            event.reply("Сумма должна быть положительной.").queue();
            return;
        }

        UserData senderData = UserDataManager.getUserData(senderName);
        if (senderData.getBalance() < amount) {
            event.reply("У вас недостаточно средств для этой операции.").queue();
            return;
        }

        UserData receiverData = UserDataManager.getUserData(receiverName);
        senderData.setBalance(senderData.getBalance() - amount);
        receiverData.setBalance(receiverData.getBalance() + amount);

        UserDataManager.updateUserData(senderData);
        UserDataManager.updateUserData(receiverData);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Транзакция успешна!");
        embedBuilder.setDescription("Вы передали " + amount + " монет пользователю " + receiverName + ".");
        embedBuilder.setColor(Color.GREEN);

        event.replyEmbeds(embedBuilder.build()).queue();
    }
}
