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

    private final String verifiedRoleId = "1273262248638287935"; // ID Верифицированного
    private final String unverifiedRoleId = "1273011992298258544"; // ID Не верефицированного
    private final String guildId = "1269261946218348555"; // ID сервера

    public void execute(MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equalsIgnoreCase("!test")) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Верификация");
            embed.setDescription("Нажмите кнопку, чтобы пройти верификацию");
            embed.setColor(Color.BLUE);

            // Отправляем сообщение с кнопкой в канал
            event.getChannel().sendMessageEmbeds(embed.build())
                    .setActionRow(Button.primary("test_button", "Верификация"))
                    .queue();
        }
    }

    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().equals("test_button")) {
            User user = event.getUser();

            user.openPrivateChannel().queue(privateChannel -> {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("Верификация");
                embed.setDescription("Нажмите на кнопку, чтобы пройти верификацию");
                embed.setColor(Color.GREEN);

                privateChannel.sendMessageEmbeds(embed.build())
                        .setActionRow(Button.primary("private_test_button", "Верификация!"))
                        .queue();
            });

            event.reply("Сообщение отправлено в личные сообщения.").setEphemeral(true).queue();
        } else if (event.getComponentId().equals("private_test_button")) {
            // Обработка нажатия на кнопку в личных сообщениях
            Guild guild = event.getJDA().getGuildById(guildId);
            if (guild != null) {
                // Используем retrieveMember для получения объекта Member
                guild.retrieveMember(event.getUser()).queue(member -> {
                    if (member != null) {
                        Role verifiedRole = guild.getRoleById(verifiedRoleId);
                        Role unverifiedRole = guild.getRoleById(unverifiedRoleId);

                        if (verifiedRole != null && unverifiedRole != null) {
                            // Выдаём роль "Верифицированный"
                            guild.addRoleToMember(member, verifiedRole).queue(
                                    success -> {
                                        // Убираем роль "Неверифицированный"
                                        guild.removeRoleFromMember(member, unverifiedRole).queue(
                                                successRemove -> event.reply("Вы успешно прошли верификацию.").setEphemeral(true).queue(),
                                                error -> event.reply("Не удалось убрать роль 'Неверифицированный'.").setEphemeral(true).queue()
                                        );
                                    },
                                    error -> event.reply("Не удалось выдать роль 'Верифицированный'.").setEphemeral(true).queue()
                            );
                        } else {
                            event.reply("Не удалось найти роль 'Верифицированный' или 'Неверифицированный'.").setEphemeral(true).queue();
                        }
                    } else {
                        event.reply("Участник не найден.").setEphemeral(true).queue();
                    }
                }, error -> event.reply("Не удалось получить участника.").setEphemeral(true).queue());
            } else {
                event.reply("Сервер не найден.").setEphemeral(true).queue();
            }
        }
    }
}
