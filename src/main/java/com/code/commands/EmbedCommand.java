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
            System.err.println("Не удалось загрузить счётчик заявок: " + e.getMessage());
        }
    }

    private void saveRequestCounter() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            writer.write(String.valueOf(requestCounter.get()));
        } catch (IOException e) {
            System.err.println("Не удалось сохранить счётчик заявок: " + e.getMessage());
        }
    }

    private boolean isUserAlreadyRegistered(Guild guild, User user) {
        Member member = guild.getMember(user);
        return member != null && member.getRoles().stream().anyMatch(role -> role.getId().equals(roleId));
    }

    public void execute(MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equalsIgnoreCase("!embed")) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Зарегистрировать Страну");
            embed.setDescription("Нажмите кнопку, чтобы зарегистрироваться");
            embed.setColor(Color.BLUE);

            // Отправляем сообщение с кнопкой в канал
            event.getChannel().sendMessageEmbeds(embed.build())
                    .setActionRow(Button.primary("send_request_button", "Отправить заявку"))
                    .queue();
        }
    }

    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().equals("send_request_button")) {
            // Создаём текстовый инпут для ввода страны
            TextInput countryInput = TextInput.create("country_input", "Введите вашу страну", TextInputStyle.SHORT)
                    .setPlaceholder("Например: Америка")
                    .setRequired(true)
                    .build();

            // Создаём модальное окно
            Modal modal = Modal.create("request_modal", "Отправка заявки")
                    .addActionRow(countryInput)
                    .build();

            // Отправляем модальное окно пользователю
            event.replyModal(modal).queue();
        } else if (event.getComponentId().startsWith("reject_request_button")) {
            // Открываем модальное окно для ввода причины отклонения
            String requestId = event.getComponentId().split(":")[1];
            String userId = event.getComponentId().split(":")[2];

            TextInput rejectReasonInput = TextInput.create("reject_reason_input", "Причина отклонения", TextInputStyle.PARAGRAPH)
                    .setPlaceholder("Например: Не соответствует требованиям...")
                    .setRequired(true)
                    .build();

            Modal rejectModal = Modal.create("reject_reason_modal:" + requestId + ":" + userId, "Причина отклонения заявки")
                    .addActionRow(rejectReasonInput)
                    .build();

            event.replyModal(rejectModal).queue();
        }
    }

    public void onModalInteraction(ModalInteractionEvent event) {
        String modalId = event.getModalId();

        if (modalId.startsWith("request_modal")) {
            // Обработка основной заявки
            ModalMapping countryMapping = event.getValue("country_input");
            if (countryMapping == null) {
                event.reply("Ошибка: Не удалось получить страну.").setEphemeral(true).queue();
                return;
            }
            String country = countryMapping.getAsString();

            // Получаем пользователя, отправившего заявку
            User user = event.getUser();

            // Получаем номер заявки
            int requestNumber = requestCounter.getAndIncrement();
            saveRequestCounter();

            // Создаём Embed сообщение
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Новая заявка на регистрацию");
            embed.setDescription(String.format("*%s* хочет зарегистрировать страну: *%s*", user.getAsTag(), country));
            embed.setColor(Color.GREEN);
            embed.setFooter("Номер заявки: #" + requestNumber);

            String requestId = "request_" + requestNumber;
            requestStates.put(requestId, "pending");

            event.getJDA().getTextChannelById(targetChannelId).sendMessageEmbeds(embed.build())
                    .setActionRow(
                            Button.success("accept_request_button:" + requestId + ":" + user.getId() + ":" + country, "Принять"),
                            Button.danger("reject_request_button:" + requestId + ":" + user.getId(), "Отклонить")
                    ).queue();

            event.reply("Ваша заявка успешно отправлена! Номер заявки: #" + requestNumber).setEphemeral(true).queue();
        } else if (modalId.startsWith("reject_reason_modal")) {
            // Обработка модального окна с причиной отклонения
            String[] parts = modalId.split(":");
            if (parts.length < 3) {
                event.reply("Ошибка: Не удалось разобрать ID модального окна.").setEphemeral(true).queue();
                return;
            }

            String requestId = parts[1];
            String userId = parts[2];

            ModalMapping reasonMapping = event.getValue("reject_reason_input");
            if (reasonMapping == null) {
                event.reply("Ошибка: Не удалось получить причину отклонения.").setEphemeral(true).queue();
                return;
            }
            String reason = reasonMapping.getAsString();

            Guild guild = event.getGuild();
            if (guild == null) {
                event.reply("Ошибка: Не удалось найти гильдию.").setEphemeral(true).queue();
                return;
            }

            // Получаем пользователя через кеш или API
            guild.retrieveMemberById(userId).queue(member -> {
                if (member == null) {
                    event.reply("Ошибка: Не удалось найти пользователя.").setEphemeral(true).queue();
                    return;
                }

                User user = member.getUser();

                EmbedBuilder dmEmbed = new EmbedBuilder();
                dmEmbed.setTitle("Заявка отклонена");
                dmEmbed.setDescription("Ваша заявка была отклонена. Причина: " + reason);
                dmEmbed.setColor(Color.RED);

                user.openPrivateChannel().queue(channel -> {
                    channel.sendMessageEmbeds(dmEmbed.build()).queue();
                });

                EmbedBuilder channelEmbed = new EmbedBuilder();
                channelEmbed.setTitle("Заявка отклонена");
                channelEmbed.setDescription("Заявка отклонена. Игрок " + member.getEffectiveName() + " не принят. Причина: " + reason);
                channelEmbed.setColor(Color.RED);

                guild.getTextChannelById(targetChannelId)
                        .sendMessageEmbeds(channelEmbed.build()).queue();

                requestStates.put(requestId, "rejected");
            }, error -> {
                System.err.println("Ошибка: Не удалось найти пользователя. " + error.getMessage());
                event.reply("Ошибка: Не удалось найти пользователя.").setEphemeral(true).queue();
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
            event.reply("Ошибка: Заявка не найдена.").setEphemeral(true).queue();
            return;
        }
        if (!requestState.equals("pending")) {
            event.reply("Ошибка: Заявка уже обработана.").setEphemeral(true).queue();
            return;
        }

        Guild guild = event.getGuild();
        if (guild == null) {
            event.reply("Ошибка: Не удалось найти гильдию.").setEphemeral(true).queue();
            return;
        }

        // Получаем пользователя через кеш или API
        guild.retrieveMemberById(userId).queue(member -> {
            if (member == null) {
                event.reply("Ошибка: Не удалось найти пользователя.").setEphemeral(true).queue();
                return;
            }

            User user = member.getUser();
            String nickname = member.getEffectiveName();

            if (action.equals("accept_request_button")) {
                String newCountry = parts[3];

                if (isUserAlreadyRegistered(guild, user)) {
                    event.reply("Пользователь уже зарегистрирован.").setEphemeral(true).queue();
                    return;
                }

                // Меняем ник пользователя на название страны
                guild.modifyNickname(member, newCountry).queue();

                // Выдаем роль пользователю
                Role role = guild.getRoleById(roleId);
                if (role != null) {
                    guild.addRoleToMember(member, role).queue(
                            success -> {
                                event.reply("Роль успешно выдана!").setEphemeral(true).queue();
                            },
                            error -> {
                                System.err.println("Не удалось выдать роль пользователю " + user.getId() + ": " + error.getMessage());
                                event.reply("Не удалось выдать роль пользователю.").setEphemeral(true).queue();
                            }
                    );
                } else {
                    event.reply("Роль не найдена.").setEphemeral(true).queue();
                }

                EmbedBuilder dmEmbed = new EmbedBuilder();
                dmEmbed.setTitle("Заявка принята");
                dmEmbed.setDescription("Ваша заявка принята!");
                dmEmbed.setColor(Color.GREEN);

                user.openPrivateChannel().queue(channel -> {
                    channel.sendMessageEmbeds(dmEmbed.build()).queue();
                });

                EmbedBuilder channelEmbed = new EmbedBuilder();
                channelEmbed.setTitle("Игрок принят");
                channelEmbed.setDescription("Заявка принята! Игрок " + nickname + " успешно принят.");
                channelEmbed.setColor(Color.BLUE);

                guild.getTextChannelById(targetChannelId)
                        .sendMessageEmbeds(channelEmbed.build()).queue();

                requestStates.put(requestId, "accepted");

            } else if (action.equals("reject_request_button")) {
                // Открываем модальное окно для ввода причины отклонения
                TextInput rejectReasonInput = TextInput.create("reject_reason_input", "Причина отклонения", TextInputStyle.PARAGRAPH)
                        .setPlaceholder("Например: Не соответствует требованиям...")
                        .setRequired(true)
                        .build();

                Modal rejectModal = Modal.create("reject_reason_modal:" + requestId + ":" + userId, "Причина отклонения заявки")
                        .addActionRow(rejectReasonInput)
                        .build();

                event.replyModal(rejectModal).queue();
            }
        }, error -> {
            System.err.println("Ошибка: Не удалось найти пользователя. " + error.getMessage());
            event.reply("Ошибка: Не удалось найти пользователя.").setEphemeral(true).queue();
        });
    }
}
