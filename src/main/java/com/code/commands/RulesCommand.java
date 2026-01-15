package com.code.commands;

import java.awt.Color;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RulesCommand {

    public void execute(MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equalsIgnoreCase("!rules")) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("RULES");  // This sets the large title text
            embed.setDescription("**Informa WPG Bot");
            embed.setColor(Color.BLUE);  // Sets the color of the embed



            event.getChannel().sendMessageEmbeds(embed.build()).queue();
        }
    }
}
