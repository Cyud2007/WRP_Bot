package com.code.commands.Admin;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import com.code.commands.Command;

import java.awt.Color;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MuteCommand extends Command {

    @Override
    public CommandData createCommand() {
        return Commands.slash("mute", "Замутить пользователя на определённое время.")
                .addOptions(
                        new OptionData(OptionType.USER, "игрок", "Пользователь, которого нужно замутить", true),
                        new OptionData(OptionType.STRING, "время", "Длительность мута (например, 1h, 30m)", true)
                );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        // Получаем параметры
        Member member = event.getOption("игрок").getAsMember();
        String timeInput = event.getOption("время").getAsString();

        if (member == null) {
            event.reply("Пользователь не найден!").setEphemeral(true).queue();
            return;
        }

        // Проверка правильности формата времени
        Pattern pattern = Pattern.compile("(\\d+)([mhd])");
        Matcher matcher = pattern.matcher(timeInput);

        if (!matcher.matches()) {
            event.reply("Неверный формат времени! Используйте формат, например, 1h для 1 часа или 30m для 30 минут.").setEphemeral(true).queue();
            return;
        }

        // Определяем количество минут в зависимости от единицы измерения
        int duration = Integer.parseInt(matcher.group(1));
        String unit = matcher.group(2);
        long durationInMinutes;

        switch (unit) {
            case "h":
                durationInMinutes = duration * 60L;
                break;
            case "d":
                durationInMinutes = duration * 1440L; // 1 день = 1440 минут
                break;
            case "m":
            default:
                durationInMinutes = duration;
                break;
        }

        // Получаем роль "Мут"
        Role muteRole = event.getGuild().getRoleById("1278809802671263826"); // Укажите ID вашей роли "Мут"

        if (muteRole == null) {
            event.reply("Роль 'Мут' не найдена!").setEphemeral(true).queue();
            return;
        }

        // Назначаем роль "Мут"
        event.getGuild().addRoleToMember(member, muteRole).queue();

        // Отправляем сообщение в ЛС пользователю
        member.getUser().openPrivateChannel().queue(channel -> 
            channel.sendMessage("Вы замучены на " + duration + " " + (unit.equals("h") ? "часов" : unit.equals("d") ? "дней" : "минут") + ".").queue()
        );

        // Создаём embed для ответа в канал
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Пользователь замучен");
        embedBuilder.setDescription(member.getAsMention() + " был замучен на " + duration + " " + (unit.equals("h") ? "часов" : unit.equals("d") ? "дней" : "минут") + ".");
        embedBuilder.setColor(new Color(255, 0, 0)); // Красный цвет
        event.replyEmbeds(embedBuilder.build()).queue();

        // Планируем снятие роли "Мут" через указанное время
        event.getGuild().removeRoleFromMember(member, muteRole)
                .queueAfter(durationInMinutes, TimeUnit.MINUTES, 
                        success -> {
                            // Уведомляем пользователя о снятии мута
                            member.getUser().openPrivateChannel().queue(channel -> 
                                channel.sendMessage("Ваш мут истёк.").queue()
                            );
                        });
    }
}
