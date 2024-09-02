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
            "1277288067589341184", 2000,   // Экономика лв1
            "1277288432623681547", 5000,   // Экономика лв2
            "1277288539360333824", 10000,  // Экономика лв3
            "1277288626526621696", 20000,  // Экономика лв4
            "1277288691685003435", 40000,  // Экономика лв5
            "1277288800359284869", 80000,  // Экономика лв6
            "1277288857439572048", 160000  // Экономика лв7
    );

    private static final Map<String, Double> ECONOMY_MULTIPLIERS = Map.of(
            "1277288691685003435", 1.8,  // Экономика лв5
            "1277288800359284869", 2.0,  // Экономика лв6
            "1277288857439572048", 2.2   // Экономика лв7
    );

    private static final String MINER_ROLE_ID = "1279083348504875133";
    private static final String SHIPYARD_ROLE_ID = "1279083382160228444";  // ID Верфи

    private static final int BASE_IRON_REWARD = 10000;
    private static final int BASE_GOLD_REWARD = 5000;  // Базовая награда за золото
    private static final int BASE_OIL_REWARD = 600;   // Базовая награда за нефть

    @Override
    public CommandData createCommand() {
        return Commands.slash("collect", "Получить ресурсы.");
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) {
            sendErrorEmbed(event, "Ошибка: не удалось найти участника. Обратитесь в администрацию.");
            return;
        }

        UserData userData = UserDataManager.getUserData(member.getId());

        // Проверка кулдауна
        if (!userData.canCollect()) {
            LocalDateTime nextAllowedTime = userData.getLastJobTime().plusHours(UserData.COOLDOWN_HOURS);
            long hoursLeft = java.time.Duration.between(LocalDateTime.now(), nextAllowedTime).toHours();
            sendErrorEmbed(event, "Пожалуйста, подождите " + hoursLeft + " часов перед следующим сбором.");
            return;
        }

        int totalReward = 0;
        double multiplier = 1.0;

        // Проверяем роли для начисления монет и ресурсов
        for (String roleId : ROLE_REWARDS.keySet()) {
            if (member.getRoles().stream().anyMatch(role -> role.getId().equals(roleId))) {
                totalReward += ROLE_REWARDS.get(roleId);
                multiplier = ECONOMY_MULTIPLIERS.getOrDefault(roleId, 1.0);
            }
        }

        // Начисляем монеты
        userData.addToBalance(totalReward);

        // Начисляем золото
        int goldReward = (int) (BASE_GOLD_REWARD * multiplier);
        userData.addGold(goldReward);

        // Начисляем железо
        int ironReward = 0;
        if (member.getRoles().stream().anyMatch(role -> role.getId().equals(MINER_ROLE_ID))) {
            ironReward = (int) (BASE_IRON_REWARD * multiplier);
            userData.addIron(ironReward);
        }

        // Начисляем нефть
        int oilReward = 0;
        if (member.getRoles().stream().anyMatch(role -> role.getId().equals(SHIPYARD_ROLE_ID))) {
            oilReward = (int) (BASE_OIL_REWARD * multiplier);
            userData.addOil(oilReward);
        }

        UserDataManager.updateUserData(userData);
        userData.updateLastJobTime();  // Обновляем время последнего использования команды

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Ресурсы собраны");
        embedBuilder.setDescription("Вы получили " + totalReward + " монет.");
        embedBuilder.setColor(Color.GREEN);

        // Формируем описание ресурсов
        StringBuilder resourcesDescription = new StringBuilder();
        resourcesDescription.append("Золото: ").append(userData.getGold());
        if (multiplier > 1.0) {
            resourcesDescription.append(" (x").append(multiplier).append(")");
        }
        resourcesDescription.append("\nЖелезо: ").append(userData.getIron());
        if (ironReward > 0) {
            resourcesDescription.append(" (x").append(multiplier).append(")");
        }
        resourcesDescription.append("\nНефть: ").append(userData.getOil());
        if (oilReward > 0) {
            resourcesDescription.append(" (x").append(multiplier).append(")");
        }

        embedBuilder.addField("Ресурсы:", resourcesDescription.toString(), false);

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
