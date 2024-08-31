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

public class KickCommand extends Command {

    @Override
    public CommandData createCommand() {
        return Commands.slash("kick", "Кикает игрока с сервера и отправляет сообщение с причиной.")
                .addOptions(new OptionData(OptionType.USER, "игрок", "Игрок, которого нужно кикнуть", true))
                .addOptions(new OptionData(OptionType.STRING, "причина", "Причина кика", true));
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        Member memberToKick = event.getOption("игрок").getAsMember();
        String reason = event.getOption("причина").getAsString();

        if (memberToKick == null) {
            event.reply("Не удалось найти указанного игрока.").setEphemeral(true).queue();
            return;
        }

        Member moderator = event.getMember();

        if (!moderator.hasPermission(Permission.KICK_MEMBERS)) {
            event.reply("У вас нет прав на кик участников.").setEphemeral(true).queue();
            return;
        }

        // Создание embed сообщения для текстового канала
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Участник кикнут");
        embedBuilder.setDescription(memberToKick.getEffectiveName() + " был кикнут.");
        embedBuilder.setColor(new Color(255, 0, 0)); // Красный цвет
        embedBuilder.addField("Модератор", moderator.getEffectiveName(), false);
        embedBuilder.addField("Причина", reason, false);

        // Отправка сообщения в текстовый канал
        event.replyEmbeds(embedBuilder.build()).queue();

        // Отправка личного сообщения кикнутому участнику
        EmbedBuilder privateMessage = new EmbedBuilder();
        privateMessage.setTitle("Вы были кикнуты с сервера");
        privateMessage.setDescription("Вас кикнули с сервера " + event.getGuild().getName() + ".");
        privateMessage.setColor(new Color(255, 0, 0)); // Красный цвет
        privateMessage.addField("Причина", reason, false);

        memberToKick.getUser().openPrivateChannel()
                .flatMap(channel -> channel.sendMessageEmbeds(privateMessage.build()))
                .queue();

        // Кик участника с использованием Guild#kick
        event.getGuild().kick(memberToKick, reason).queue();
    }
}
