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
        "1277288067589341184", 2000,  
        "1277288432623681547", 50000, 
        "1277288539360333824", 1000000,  
        "1277288626526621696", 1000000,
        "1277288691685003435", 1000000,
        "1277288800359284869", 1000000,
        "1277288857439572048", 10000
        

    );

    @Override
    public CommandData createCommand() {
        return Commands.slash("collect", "типо что то делает.");
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) {
            event.reply("Ошибка: не удалось найти роль или участника.").setEphemeral(true).queue();
            return;
        }


        UserData userData = UserDataManager.getUserData(member.getId());
        int totalReward = 0;
        
        for (String roleId : ROLE_REWARDS.keySet()) {
            if (member.getRoles().stream().anyMatch(role -> role.getId().equals(roleId))) {
                totalReward += ROLE_REWARDS.get(roleId);
            }
        }


        userData.setBalance(userData.getBalance() + totalReward);
        UserDataManager.updateUserData(userData);

  
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Коллект ");
        embedBuilder.setDescription("Вы получили " + totalReward + " монет.");
        embedBuilder.setColor(java.awt.Color.GREEN);

        event.replyEmbeds(embedBuilder.build()).queue();
    }
}
