package com.code.commands;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.code.data.UserData;
import com.code.data.UserDataManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

public class EmbedCommand {
    
    private static final String DATA_FILE = "countrydata.txt";
    private static final AtomicInteger requestCounter = new AtomicInteger(1);
    private final String targetChannelId = "1273338936177197138";
    private final String roleId = "1274822435597717565";
    private final String economyRoleId = "1277288067589341184"; 

    private final Map<String, String> requestStates = new ConcurrentHashMap<>();

    public EmbedCommand() {
        loadRequestCounter();
    }

    private void loadRequestCounter() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line = reader.readLine();
            if (line != null) {
                requestCounter.set(Integer.parseInt(line));
            }
        } catch (IOException e) {
            System.err.println("Failed to load application counter: " + e.getMessage());
        }
    }

    private void saveRequestCounter() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            writer.write(String.valueOf(requestCounter.get()));
        } catch (IOException e) {
            System.err.println("Failed to save the application counter: " + e.getMessage());
        }
    }

    private boolean isUserAlreadyRegistered(Guild guild, User user) {
        Member member = guild.getMember(user);
        return member != null && member.getRoles().stream().anyMatch(role -> role.getId().equals(roleId));
    }

    public void execute(MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equalsIgnoreCase("!embed")) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Register Country");
            embed.setDescription("Click the button to register");
            embed.setColor(Color.BLUE);

            // Send a message with a button to the channel
            event.getChannel().sendMessageEmbeds(embed.build())
                    .setActionRow(Button.primary("send_request_button", "Submit an application"))
                    .queue();
        }
    }

    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().equals("send_request_button")) {
            
            // Create a text input for entering emoji and country
            TextInput countryInput = TextInput.create("country_input", "Enter your country", TextInputStyle.SHORT)
                    .setPlaceholder("For example: America")
                    .setRequired(true)
                    .build();

                    TextInput emojiInput = TextInput.create("emoji_input", "Enter your emoji", TextInputStyle.SHORT)
                    .setPlaceholder("For example: 🇺🇸")
                    .setRequired(true)
                    .build();

            // Create a modal window
            Modal modal = Modal.create("request_modal", "Submitting an application")
                    .addActionRow(countryInput)
                    .addActionRow(emojiInput)
                    .build();

            // Sending a modal window to the user
            event.replyModal(modal).queue();
        } else if (event.getComponentId().startsWith("reject_request_button")) {
            // Open a modal window to enter the reason for rejection.
            String requestId = event.getComponentId().split(":")[1];
            String userId = event.getComponentId().split(":")[2];

            TextInput rejectReasonInput = TextInput.create("reject_reason_input", "Reason for rejection", TextInputStyle.PARAGRAPH)
                    .setPlaceholder("For example: Does not meet the requirements...")
                    .setRequired(true)
                    .build();

            Modal rejectModal = Modal.create("reject_reason_modal:" + requestId + ":" + userId, "Reason for application rejection")
                    .addActionRow(rejectReasonInput)
                    .build();

            event.replyModal(rejectModal).queue();
        }
    }

    public void onModalInteraction(ModalInteractionEvent event) {
        String modalId = event.getModalId();

        if (modalId.startsWith("request_modal")) {
            // Processing the main application
        
            ModalMapping countryMapping = event.getValue("country_input");
            ModalMapping emojiMapping = event.getValue("emoji_input");
            if (emojiMapping == null || countryMapping == null) {
                event.reply("Error: Failed to get emoji or country.").setEphemeral(true).queue();
                return;
            }
         
            String country = countryMapping.getAsString();
            String emoji = emojiMapping.getAsString();

            // We get the user who submitted the request
            User user = event.getUser();

            // We receive an application number
            int requestNumber = requestCounter.getAndIncrement();
            saveRequestCounter();

            // Create an Embed message
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("New application for registration");
            embed.setDescription(String.format("*%s* wants to register the country: *%s* *%s*", user.getAsTag(), emoji, country ));
            embed.setColor(Color.GREEN);
            embed.setFooter("Application number: #" + requestNumber);

            String requestId = "request_" + requestNumber;
            requestStates.put(requestId, "pending");

            event.getJDA().getTextChannelById(targetChannelId).sendMessageEmbeds(embed.build())
                    .setActionRow(
                            Button.success("accept_request_button:" + requestId + ":" + user.getId() + ":" + emoji + ":" + country, "Accept"),
                            Button.danger("reject_request_button:" + requestId + ":" + user.getId(), "Reject")
                    ).queue();

            event.reply("Your application has been successfully submitted! Application number: #" + requestNumber).setEphemeral(true).queue();
        } else if (modalId.startsWith("reject_reason_modal")) {
            // Handling a modal window with a rejection reason
            String[] parts = modalId.split(":");
            if (parts.length < 3) {
                event.reply("Error: Unable to parse modal window ID.").setEphemeral(true).queue();
                return;
            }

            String requestId = parts[1];
            String userId = parts[2];

            ModalMapping reasonMapping = event.getValue("reject_reason_input");
            if (reasonMapping == null) {
                event.reply("Error: Unable to get rejection reason.").setEphemeral(true).queue();
                return;
            }
            String reason = reasonMapping.getAsString();

            Guild guild = event.getGuild();
            if (guild == null) {
                event.reply("Error: Unable to find guild.").setEphemeral(true).queue();
                return;
            }

            // We get the user through the cache or API
            guild.retrieveMemberById(userId).queue(member -> {
                if (member == null) {
                    event.reply("Error: Unable to find user.").setEphemeral(true).queue();
                    return;
                }

                User user = member.getUser();

                EmbedBuilder dmEmbed = new EmbedBuilder();
                dmEmbed.setTitle("Application rejected");
                dmEmbed.setDescription("Your application has been rejected. Reason: " + reason);
                dmEmbed.setColor(Color.RED);

                user.openPrivateChannel().queue(channel -> {
                    channel.sendMessageEmbeds(dmEmbed.build()).queue();
                });

                EmbedBuilder channelEmbed = new EmbedBuilder();
                channelEmbed.setTitle("Application rejected");
                channelEmbed.setDescription("Request rejected. Player " + member.getEffectiveName() + " not accepted. Reason: " + reason);
                channelEmbed.setColor(Color.RED);

                guild.getTextChannelById(targetChannelId)
                        .sendMessageEmbeds(channelEmbed.build()).queue();

                requestStates.put(requestId, "rejected");
            }, error -> {
                System.err.println("Error: Unable to find user. " + error.getMessage());
                event.reply("Error: Unable to find user.").setEphemeral(true).queue();
            });
        }
    }

    public void onButtonInteractionForDecision(ButtonInteractionEvent event) {
        String[] parts = event.getComponentId().split(":");
        String action = parts[0];
        String requestId = parts[1];
        String userId = parts[2];
    
        String requestState = requestStates.get(requestId);
        if (requestState == null) {
            event.reply("Error: Application not found.").setEphemeral(true).queue();
            return;
        }
        if (!requestState.equals("pending")) {
            event.reply("Error: Request already processed.").setEphemeral(true).queue();
            return;
        }
    
        Guild guild = event.getGuild();
        if (guild == null) {
            event.reply("Error: Unable to find guild.").setEphemeral(true).queue();
            return;
        }
    
        guild.retrieveMemberById(userId).queue(member -> {
            if (member == null) {
                event.reply("Error: Unable to find user.").setEphemeral(true).queue();
                return;
            }
    
            User user = member.getUser();
            String nickname = member.getEffectiveName();
    
            if (action.equals("accept_request_button")) {
                String emoji = parts[3];
                String country = parts[4];
    
                if (isUserAlreadyRegistered(guild, user)) {
                    event.reply("The user is already registered.").setEphemeral(true).queue();
                    return;
                }
    
                String newNickname = emoji + " " + country;
                guild.modifyNickname(member, newNickname).queue();
    
                // Granting a role to a user
                Role role = guild.getRoleById(roleId);
                if (role != null) {
                    guild.addRoleToMember(member, role).queue(
                            success -> {
                                event.reply("The role was successfully issued.!").setEphemeral(true).queue();
                            },
                            error -> {
                                System.err.println("Failed to grant role to user " + user.getId() + ": " + error.getMessage());
                                event.reply("Failed to grant role to user.").setEphemeral(true).queue();
                            }
                    );
                } else {
                    event.reply("Role not found.").setEphemeral(true).queue();
                }
                
            
                Role economyRole = guild.getRoleById(economyRoleId);
                if (economyRole != null) {
                    guild.addRoleToMember(member, economyRole).queue(
                            success -> {
                                event.reply("The second role was successfully given out!").setEphemeral(true).queue();
                            },
                            error -> {
                                System.err.println("Failed to assign second role to user " + user.getId() + ": " + error.getMessage());
                                event.reply("Failed to assign second role to user.").setEphemeral(true).queue();
                            }
                    );
                } else {
                    event.reply("Second role not found.").setEphemeral(true).queue();
                }

    
                // Create user data and save it
                UserData userData = new UserData(newNickname, 0, "");
                UserDataManager.updateUserData(userData);
    
                EmbedBuilder dmEmbed = new EmbedBuilder();
                dmEmbed.setTitle("Application accepted");
                dmEmbed.setDescription("Your application has been accepted.!");
                dmEmbed.setColor(Color.GREEN);
    
                user.openPrivateChannel().queue(channel -> {
                    channel.sendMessageEmbeds(dmEmbed.build()).queue();
                });
    
                EmbedBuilder channelEmbed = new EmbedBuilder();
                channelEmbed.setTitle("The player has been accepted");
                channelEmbed.setDescription("Application accepted! Player " + nickname + " has been successfully accepted..");
                channelEmbed.setColor(Color.BLUE);
    
                guild.getTextChannelById(targetChannelId)
                        .sendMessageEmbeds(channelEmbed.build()).queue();
    
                requestStates.put(requestId, "accepted");
    
            } else if (action.equals("reject_request_button")) {
                TextInput rejectReasonInput = TextInput.create("reject_reason_input", "Reason for rejection", TextInputStyle.PARAGRAPH)
                        .setPlaceholder("For example: Does not meet requirements...")
                        .setRequired(true)
                        .build();
    
                Modal rejectModal = Modal.create("reject_reason_modal:" + requestId + ":" + userId, "Reason for application rejection")
                        .addActionRow(rejectReasonInput)
                        .build();
    
                event.replyModal(rejectModal).queue();
            }
        }, error -> {
            System.err.println("Error: Unable to find user. " + error.getMessage());
            event.reply("Error: Unable to find user.").setEphemeral(true).queue();
        });
    }
}    
