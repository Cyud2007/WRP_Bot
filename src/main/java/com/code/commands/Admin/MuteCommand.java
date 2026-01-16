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
        return Commands.slash("mute", "Muddy a user for a certain amount of time.")
                .addOptions(
                        new OptionData(OptionType.USER, "player", "The user to be hooked", true),
                        new OptionData(OptionType.STRING, "time", "Mute duration (e.g. 1h, 30m)", true)
                );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        // Getting parameters
        Member member = event.getOption("player").getAsMember();
        String timeInput = event.getOption("time").getAsString();

        if (member == null) {
            event.reply("User not found!").setEphemeral(true).queue();
            return;
        }

        // Checking the correct time format
        Pattern pattern = Pattern.compile("(\\d+)([mhd])");
        Matcher matcher = pattern.matcher(timeInput);

        if (!matcher.matches()) {
            event.reply("Invalid time format! Use a format such as 1h for 1 hour or 30m for 30 minutes.").setEphemeral(true).queue();
            return;
        }

        // We determine the number of minutes depending on the unit of measurement
        int duration = Integer.parseInt(matcher.group(1));
        String unit = matcher.group(2);
        long durationInMinutes;

        switch (unit) {
            case "h":
                durationInMinutes = duration * 60L;
                break;
            case "d":
                durationInMinutes = duration * 1440L; // 1 day = 1440 minutes
                break;
            case "m":
            default:
                durationInMinutes = duration;
                break;
        }

        // We get the role of "Mute"
        Role muteRole = event.getGuild().getRoleById("1278809802671263826"); // Please enter your role ID "Mute"

        if (muteRole == null) {
            event.reply("Role 'Mute' not found!").setEphemeral(true).queue();
            return;
        }

        // Assign the role of "Mute"
        event.getGuild().addRoleToMember(member, muteRole).queue();

        // Send a private message to the user
        member.getUser().openPrivateChannel().queue(channel -> 
            channel.sendMessage("You are tortured by " + duration + " " + (unit.equals("h") ? "hours" : unit.equals("d") ? "days" : "minutes") + ".").queue()
        );

        // Create an embed to reply to a channel
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("The user is muted");
        embedBuilder.setDescription(member.getAsMention() + " was muted for " + duration + " " + (unit.equals("h") ? "hours" : unit.equals("d") ? "days" : "minutes") + ".");
        embedBuilder.setColor(new Color(255, 0, 0)); // Red
        event.replyEmbeds(embedBuilder.build()).queue();

        // We plan to remove the role of "Mute" after the specified time.
        event.getGuild().removeRoleFromMember(member, muteRole)
                .queueAfter(durationInMinutes, TimeUnit.MINUTES, 
                        success -> {
                            // Notifying the user about the removal of the mute
                            member.getUser().openPrivateChannel().queue(channel -> 
                                channel.sendMessage("Your mute has expired.").queue()
                            );
                        });
    }
}
