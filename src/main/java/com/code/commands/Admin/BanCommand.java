package com.code.commands.Admin;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import com.code.commands.Command;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

public class BanCommand extends Command {

    @Override
    public CommandData createCommand() {
        return Commands.slash("ban", "Банит игрока на сервере и отправляет сообщение с причиной.")
                .addOptions(new OptionData(OptionType.USER, "игрок", "Игрок, которого нужно забанить", true))
                .addOptions(new OptionData(OptionType.STRING, "причина", "Причина бана", true));
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        Member memberToBan = event.getOption("игрок").getAsMember();
        String reason = event.getOption("причина").getAsString();

        if (memberToBan == null) {
            event.reply("Не удалось найти указанного игрока.").setEphemeral(true).queue();
            return;
        }

        Member moderator = event.getMember();

        if (!moderator.hasPermission(Permission.BAN_MEMBERS)) {
            event.reply("У вас нет прав на бан участников.").setEphemeral(true).queue();
            return;
        }

        // Создание embed сообщения для текстового канала
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Участник забанен");
        embedBuilder.setDescription(memberToBan.getEffectiveName() + " был забанен.");
        embedBuilder.setColor(new Color(255, 0, 0)); // Красный цвет
        embedBuilder.addField("Модератор", moderator.getEffectiveName(), false);
        embedBuilder.addField("Причина", reason, false);

        // Отправка сообщения в текстовый канал
        event.replyEmbeds(embedBuilder.build()).queue();

        // Отправка личного сообщения забаненному участнику
        EmbedBuilder privateMessage = new EmbedBuilder();
        privateMessage.setTitle("Вы были забанены на сервере");
        privateMessage.setDescription("Вас забанили на сервере " + event.getGuild().getName() + ".");
        privateMessage.setColor(new Color(255, 0, 0)); // Красный цвет
        privateMessage.addField("Причина", reason, false);

        memberToBan.getUser().openPrivateChannel()
                .flatMap(channel -> channel.sendMessageEmbeds(privateMessage.build()))
                .queue();

        // Бан участника с использованием Guild#ban
        event.getGuild().ban(memberToBan, 1, TimeUnit.DAYS).reason(reason).queue();
    }
}
