package com.code.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.Color;

public class TestCommand {

    private final String verifiedRoleId = "1273262248638287935"; 
    private final String unverifiedRoleId = "1273011992298258544"; 
    private final String guildId = "1269261946218348555";

    public void execute(MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equalsIgnoreCase("!test")) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Verification");
            embed.setDescription("Click the button to complete verification");
            embed.setColor(Color.BLUE);

            event.getChannel().sendMessageEmbeds(embed.build())
                    .setActionRow(Button.primary("test_button", "Verification"))
                    .queue();
        }
    }

    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().equals("test_button")) {
            User user = event.getUser();

            user.openPrivateChannel().queue(privateChannel -> {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("Verification");
                embed.setDescription("Click the button to complete verification.");
                embed.setColor(Color.GREEN);

                privateChannel.sendMessageEmbeds(embed.build())
                        .setActionRow(Button.primary("private_test_button", "Verification!"))
                        .queue();
            });

            event.reply("The message has been sent to private messages.").setEphemeral(true).queue();
        } else if (event.getComponentId().equals("private_test_button")) {

            Guild guild = event.getJDA().getGuildById(guildId);
            if (guild != null) {
              
                guild.retrieveMember(event.getUser()).queue(member -> {
                    if (member != null) {
                        Role verifiedRole = guild.getRoleById(verifiedRoleId);
                        Role unverifiedRole = guild.getRoleById(unverifiedRoleId);

                        if (verifiedRole != null && unverifiedRole != null) {
             
                            guild.addRoleToMember(member, verifiedRole).queue(
                                    success -> {
                         
                                        guild.removeRoleFromMember(member, unverifiedRole).queue(
                                                successRemove -> event.reply("You have successfully passed verification.").setEphemeral(true).queue(),
                                                error -> event.reply("Failed to remove role 'Unverified'.").setEphemeral(true).queue()
                                        );
                                    },
                                    error -> event.reply("Failed to grant the 'Verified' role.").setEphemeral(true).queue()
                            );
                        } else {
                            event.reply("Could not find the role 'Verified' or 'Unverified'.").setEphemeral(true).queue();
                        }
                    } else {
                        event.reply("Participant not found.").setEphemeral(true).queue();
                    }
                }, error -> event.reply("Failed to get participant.").setEphemeral(true).queue());
            } else {
                event.reply("Server not found.").setEphemeral(true).queue();
            }
        }
    }
}
