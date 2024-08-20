package com.code.commands;

import com.code.data.UserData;
import com.code.data.UserDataManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class JobCommand extends Command {

    @Override
    public CommandData createCommand() {
        return Commands.slash("job", "Выдает 2000 монет раз в сутки.");
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        String username = event.getUser().getName();
        UserData userData = UserDataManager.getUserData(username);

     
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Kiev"));

        // Проверяем, прошло ли больше 24 часов с последнего использования команды /job
        if (userData.getLastJobTime().until(now, ChronoUnit.DAYS) >= 1) {
            userData.setBalance(userData.getBalance() + 2000);
            userData.setLastJobTime(now);  // Обновляем время последнего использования команды
            UserDataManager.updateUserData(userData);  // Сохраняем изменения в файл
            event.reply("Вы заработали 2000 монет. Ваш баланс: " + userData.getBalance() + " монет.").queue();
        } else {
            event.reply("Вы уже использовали эту команду сегодня. Попробуйте снова после полуночи.").queue();
        }
    }
}