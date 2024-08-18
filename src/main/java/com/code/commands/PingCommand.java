package com.code.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PingCommand extends Command {

    @Override
    public CommandData createCommand() {
        return Commands.slash("ping", "Возвращает ответ со временем задержки.");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Ping Command");
        embedBuilder.setDescription("Pong!");
        embedBuilder.addField("Latency", String.valueOf(event.getJDA().getGatewayPing()) + " ms", false);
        embedBuilder.setColor(java.awt.Color.GREEN); 

        event.replyEmbeds(embedBuilder.build()).queue();
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
