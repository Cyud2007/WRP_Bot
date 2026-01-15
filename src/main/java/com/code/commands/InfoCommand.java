package com.code.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;

public class InfoCommand extends Command {

    @Override
    public CommandData createCommand() {
        return Commands.slash("info", "Sends an informational message in embed format.");
    }

    @Override
    public void execute(@NotNull MessageReceivedEvent event) {

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Information about the bot");
        embedBuilder.setDescription("This bot was designed to work with a server. WPG.");
        embedBuilder.setColor(new Color(255, 165, 0)); // Orange
        embedBuilder.addField("Version", "Beta 1.2 ", false);
        embedBuilder.addField("Authors", "Cyud, poslaya_sairys", false);
        embedBuilder.setFooter("GWRP Bot", null);

        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }
    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Information about the bot");
        embedBuilder.setDescription("This bot was designed to work with a server. WPG.");
        embedBuilder.setColor(new Color(255, 165, 0)); // Orange
        embedBuilder.addField("Version", "Beta 1.2 ", false);
        embedBuilder.addField("Authors", "Cyud, poslaya_sairys", false);
        embedBuilder.setFooter("WPG Bot", null);

        event.replyEmbeds(embedBuilder.build()).queue();
    }
}
