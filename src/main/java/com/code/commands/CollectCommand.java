package com.code.commands;

import com.code.data.UserData;
import com.code.data.UserDataManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.time.LocalDateTime;
import java.util.Map;

public class CollectCommand extends Command {

    private static final Map<String, Integer> ROLE_REWARDS = Map.of(
            "1277288067589341184", 2000,   // Economy lv1
            "1277288432623681547", 5000,   // Economy lv2
            "1277288539360333824", 10000,  // Economy lv3
            "1277288626526621696", 20000,  // Economy lv4
            "1277288691685003435", 40000,  // Economy lv5
            "1277288800359284869", 80000,  // Economy lv6
            "1277288857439572048", 160000  // Economy lv7
    );

    private static final Map<String, Double> ECONOMY_MULTIPLIERS = Map.of(
            "1277288691685003435", 1.8,  // Economy lv5
            "1277288800359284869", 2.0,  // Economy lv6
            "1277288857439572048", 2.2   // Economy lv7
    );

    private static final String MINER_ROLE_ID = "1279083348504875133";
    private static final String SHIPYARD_ROLE_ID = "1279083382160228444";  // Shipyard ID

    private static final int BASE_IRON_REWARD = 10000;
    private static final int BASE_GOLD_REWARD = 5000;  // Base gold reward
    private static final int BASE_OIL_REWARD = 600;   // Base oil reward

    @Override
    public CommandData createCommand() {
        return Commands.slash("collect", "Get resources.");
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) {
            sendErrorEmbed(event, "Error: The member could not be found. Contact the administration.");
            return;
        }

        UserData userData = UserDataManager.getUserData(member.getId());

        // Cooldown check
        if (!userData.canCollect()) {
            LocalDateTime nextAllowedTime = userData.getLastJobTime().plusHours(UserData.COOLDOWN_HOURS);
            long hoursLeft = java.time.Duration.between(LocalDateTime.now(), nextAllowedTime).toHours();
            sendErrorEmbed(event, "Please wait " + hoursLeft + " hours before the next collection.");
            return;
        }

        int totalReward = 0;
        double multiplier = 1.0;

        // Checking roles for coin and resource accrual
        for (String roleId : ROLE_REWARDS.keySet()) {
            if (member.getRoles().stream().anyMatch(role -> role.getId().equals(roleId))) {
                totalReward += ROLE_REWARDS.get(roleId);
                multiplier = ECONOMY_MULTIPLIERS.getOrDefault(roleId, 1.0);
            }
        }

        // We are accruing coins
        userData.addToBalance(totalReward);

        // We are accruing gold
        int goldReward = (int) (BASE_GOLD_REWARD * multiplier);
        userData.addGold(goldReward);

        // We are adding iron
        int ironReward = 0;
        if (member.getRoles().stream().anyMatch(role -> role.getId().equals(MINER_ROLE_ID))) {
            ironReward = (int) (BASE_IRON_REWARD * multiplier);
            userData.addIron(ironReward);
        }

        // We are calculating oil
        int oilReward = 0;
        if (member.getRoles().stream().anyMatch(role -> role.getId().equals(SHIPYARD_ROLE_ID))) {
            oilReward = (int) (BASE_OIL_REWARD * multiplier);
            userData.addOil(oilReward);
        }

        UserDataManager.updateUserData(userData);
        userData.updateLastJobTime();  // Updating the last time a command was used

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Resources have been collected");
        embedBuilder.setDescription("You received " + totalReward + " coins.");
        embedBuilder.setColor(Color.GREEN);

        // Forming a description of resources
        StringBuilder resourcesDescription = new StringBuilder();
        resourcesDescription.append("Gold: ").append(userData.getGold());
        if (multiplier > 1.0) {
            resourcesDescription.append(" (x").append(multiplier).append(")");
        }
        resourcesDescription.append("\nIron: ").append(userData.getIron());
        if (ironReward > 0) {
            resourcesDescription.append(" (x").append(multiplier).append(")");
        }
        resourcesDescription.append("\nOil: ").append(userData.getOil());
        if (oilReward > 0) {
            resourcesDescription.append(" (x").append(multiplier).append(")");
        }

        embedBuilder.addField("Resources:", resourcesDescription.toString(), false);

        event.replyEmbeds(embedBuilder.build()).queue();
    }

    private void sendErrorEmbed(@NotNull SlashCommandInteractionEvent event, String message) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Ошибка");
        embedBuilder.setDescription(message);
        embedBuilder.setColor(Color.RED);

        event.replyEmbeds(embedBuilder.build()).setEphemeral(false).queue();
    }
}
