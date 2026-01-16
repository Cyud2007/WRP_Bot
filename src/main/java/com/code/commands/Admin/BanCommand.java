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
        return Commands.slash("ban", "Bans a player from the server and sends a message with the reason.")
                .addOptions(new OptionData(OptionType.USER, "player", "The player to be banned", true))
                .addOptions(new OptionData(OptionType.STRING, "reason", "Reason for ban", true));
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        Member memberToBan = event.getOption("player").getAsMember();
        String reason = event.getOption("reason").getAsString();

        if (memberToBan == null) {
            event.reply("The specified player could not be found..").setEphemeral(true).queue();
            return;
        }

        Member moderator = event.getMember();

        if (!moderator.hasPermission(Permission.BAN_MEMBERS)) {
            event.reply("You do not have the right to ban members..").setEphemeral(true).queue();
            return;
        }

        // Creating an embed message for a text channel
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Member banned");
        embedBuilder.setDescription(memberToBan.getEffectiveName() + " был забанен.");
        embedBuilder.setColor(new Color(255, 0, 0)); // Red
        embedBuilder.addField("Модератор", moderator.getEffectiveName(), false);
        embedBuilder.addField("Причина", reason, false);

        // Sending a message to a text channel
        event.replyEmbeds(embedBuilder.build()).queue();

        // Sending a private message to a banned user
        EmbedBuilder privateMessage = new EmbedBuilder();
        privateMessage.setTitle("You have been banned from the server.");
        privateMessage.setDescription("You have been banned from the server " + event.getGuild().getName() + ".");
        privateMessage.setColor(new Color(255, 0, 0)); // Red
        privateMessage.addField("Reason", reason, false);

        memberToBan.getUser().openPrivateChannel()
                .flatMap(channel -> channel.sendMessageEmbeds(privateMessage.build()))
                .queue();

        // Ban a member using Guild#ban
        event.getGuild().ban(memberToBan, 1, TimeUnit.DAYS).reason(reason).queue();
    }
}
