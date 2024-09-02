package com.code.commands;


import java.util.Map;

import org.jetbrains.annotations.NotNull;

import com.code.data.UserData;
import com.code.data.UserDataManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

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
            "1277288691685003435", 1.8,  // Экономика лв5 Дает x1 если у тебя есть Экономика этого уровня)
            "1277288800359284869", 2.0,  // Экономика лв6
            "1277288857439572048", 2.2   // Экономика лв7
    );

    private static final String MINER_ROLE_ID = "1279083348504875133";
    private static final int BASE_IRON_REWARD = 10000;
    private static final int BASE_GOLD_REWARD = 5000;

    @Override
    public CommandData createCommand() {
        return Commands.slash("collect", "Получить ресурсы.");
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) {
            event.reply("Ошибка: не удалось найти роль или участника. Обратитесь в администрацию").setEphemeral(true).queue();
            return;
        }

        UserData userData = UserDataManager.getUserData(member.getId());
        int totalReward = 0;
        double multiplier = 1.0;

        // Проверяем роли для начисления монет и ресурсов
        for (String roleId : ROLE_REWARDS.keySet()) {
            if (member.getRoles().stream().anyMatch(role -> role.getId().equals(roleId))) {
                totalReward += ROLE_REWARDS.get(roleId);
                multiplier = ECONOMY_MULTIPLIERS.getOrDefault(roleId, 1.0);
            }
        }

        // Начисляем монеты и золото
        userData.addToBalance(totalReward);

        int goldReward = (int) (BASE_GOLD_REWARD * multiplier);
        userData.addGold(goldReward);

  
        int ironReward = 0;
        if (member.getRoles().stream().anyMatch(role -> role.getId().equals(MINER_ROLE_ID))) {
            ironReward = (int) (BASE_IRON_REWARD * multiplier);
            userData.addIron(ironReward);
        }

        UserDataManager.updateUserData(userData);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Ресурсы собраны");
        embedBuilder.setDescription("Вы получили " + totalReward + " монет.");

        //Тут типо пишет есть ли у тебя x2 
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

        embedBuilder.addField("Ресурсы:", resourcesDescription.toString(), false);
        embedBuilder.setColor(java.awt.Color.GREEN);

        event.replyEmbeds(embedBuilder.build()).queue();
    }
}
