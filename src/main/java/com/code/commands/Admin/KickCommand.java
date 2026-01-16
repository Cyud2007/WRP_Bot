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
        return Commands.slash("kick", "Kicks a player from the server and sends a message with the reason.")
                .addOptions(new OptionData(OptionType.USER, "player", "The player who needs to be kicked", true))
                .addOptions(new OptionData(OptionType.STRING, "reason", "Reason for kick", true));
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        Member memberToKick = event.getOption("игрок").getAsMember();
        String reason = event.getOption("причина").getAsString();

        if (memberToKick == null) {
            event.reply("The specified player could not be found.").setEphemeral(true).queue();
            return;
        }

        Member moderator = event.getMember();

        if (!moderator.hasPermission(Permission.KICK_MEMBERS)) {
            event.reply("You don't have permission to kick members.").setEphemeral(true).queue();
            return;
        }

        // Создание embed сообщения для текстового канала
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("The participant was kicked");
        embedBuilder.setDescription(memberToKick.getEffectiveName() + " was kicked.");
        embedBuilder.setColor(new Color(255, 0, 0)); // Red
        embedBuilder.addField("Moderator", moderator.getEffectiveName(), false);
        embedBuilder.addField("Reason", reason, false);

        // Sending a message to a text channel
        event.replyEmbeds(embedBuilder.build()).queue();

        // Sending a private message to a kicked member
        EmbedBuilder privateMessage = new EmbedBuilder();
        privateMessage.setTitle("You have been kicked from the server");
        privateMessage.setDescription("You have been kicked from the server " + event.getGuild().getName() + ".");
        privateMessage.setColor(new Color(255, 0, 0)); // Red
        privateMessage.addField("Reason", reason, false);

        memberToKick.getUser().openPrivateChannel()
                .flatMap(channel -> channel.sendMessageEmbeds(privateMessage.build()))
                .queue();

        // Kick a participant using Guild#kick
        event.getGuild().kick(memberToKick, reason).queue();
    }
}
