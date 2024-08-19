package com.code.commands;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

            User admin = event.getUser();  // Получаем пользователя, который отклоняет заявку

            // Получаем пользователя через кеш или API
            guild.retrieveMemberById(userId).queue(member -> {
                if (member == null) {
                    event.reply("Ошибка: Не удалось найти пользователя.").setEphemeral(true).queue();
                    return;
                }

                User user = member.getUser();

                // Генерация UNIX timestamp для временной метки
                long timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();

                // Создаем Embed сообщение для личных сообщений
                EmbedBuilder dmEmbed = new EmbedBuilder();
                dmEmbed.setTitle("🔔 Оповещения");
                dmEmbed.setDescription(String.format(
                        "**Администратор:** %s\n**Время:** <t:%d:F>\n\n⚠️ **Отклонения!** Ваша заявка на регистрацию была отклонена по причине: %s",
                        admin.getAsTag(),
                        timestamp,
                        reason
                ));
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
        String country = (parts.length > 3) ? parts[3] : null;

        Guild guild = event.getGuild();
        if (guild == null) {
            event.reply("Ошибка: Не удалось найти гильдию.").setEphemeral(true).queue();
            return;
        }

        if (action.equals("accept_request_button")) {
            if (isUserAlreadyRegistered(guild, event.getUser())) {
                event.reply("Ошибка: Этот пользователь уже зарегистрирован.").setEphemeral(true).queue();
                return;
            }

            guild.retrieveMemberById(userId).queue(member -> {
                Role role = guild.getRoleById(roleId);
                if (role == null) {
                    event.reply("Ошибка: Не удалось найти роль.").setEphemeral(true).queue();
                    return;
                }

                guild.addRoleToMember(member, role).queue();
                event.reply("Заявка принята. Игрок " + member.getEffectiveName() + " добавлен в группу.").queue();

                EmbedBuilder acceptedEmbed = new EmbedBuilder();
                acceptedEmbed.setTitle("Заявка одобрена");
                acceptedEmbed.setDescription(String.format("Заявка на регистрацию страны %s была одобрена.", country));
                acceptedEmbed.setColor(Color.GREEN);

                guild.getTextChannelById(targetChannelId)
                        .sendMessageEmbeds(acceptedEmbed.build()).queue();

                requestStates.put(requestId, "accepted");

            }, error -> {
                System.err.println("Ошибка: Не удалось найти пользователя. " + error.getMessage());
                event.reply("Ошибка: Не удалось найти пользователя.").setEphemeral(true).queue();
            });
        } else if (action.equals("reject_request_button")) {
            onButtonInteraction(event);
        }
    }
}
