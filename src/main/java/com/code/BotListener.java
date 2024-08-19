package com.code;

import com.code.commands.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BotListener extends ListenerAdapter {

    // Слеш команды
    private final PingCommand pingCommand = new PingCommand();
    private final InfoCommand infoCommand = new InfoCommand();

    // Текстовые команды
    private final TestCommand testCommand = new TestCommand();
    private final EmbedCommand embedCommand = new EmbedCommand();

    private final String prefix;
    private final String roleId; 

    public BotListener() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("bot.properties")) {
            if (input != null) {
                properties.load(input);
                this.prefix = properties.getProperty("prefix", "!");
                this.roleId = properties.getProperty("role_id", "1273011992298258544"); 
            } else {
                throw new RuntimeException("bot.properties not found in classpath");
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load bot.properties", ex);
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "ping":
                pingCommand.execute(event);
                break;
            case "info":
                infoCommand.execute(event);
                break;
            default:
                event.reply("Unknown command").queue();
                break;
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().startsWith(prefix)) {
            String command = event.getMessage().getContentRaw().substring(prefix.length()).trim();

            switch (command.toLowerCase()) {
                case "test":
                    testCommand.execute(event);
                    break;
                case "embed":
                    embedCommand.execute(event); 
                    break;
                case "info":
                    infoCommand.execute(event);
                    break;
                default:
                    event.getChannel().sendMessage("Unknown command").queue();
                    break;
            }
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        // Обрабатываем кнопки для TestCommand и EmbedCommand
        testCommand.onButtonInteraction(event);

        // Проверка для кнопок EmbedCommand
        if (event.getComponentId().startsWith("accept_request_button") || event.getComponentId().startsWith("reject_request_button")) {
            embedCommand.onButtonInteractionForDecision(event); 
        } else {
            embedCommand.onButtonInteraction(event); 
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        // Обрабатываем модальные окна в EmbedCommand
        embedCommand.onModalInteraction(event);
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        Guild guild = event.getGuild();
        if (guild != null) {
            Role role = guild.getRoleById(roleId);
            if (role != null) {
                if (guild.getSelfMember().hasPermission(net.dv8tion.jda.api.Permission.MANAGE_ROLES)) {
                    Member member = event.getMember();
                    if (member != null) {
                        guild.retrieveMember(member.getUser()).queue(
                                retrievedMember -> {
                                    guild.addRoleToMember(retrievedMember, role).queue(
                                            success -> System.out.println("Role assigned to " + retrievedMember.getUser().getName()),
                                            error -> System.err.println("Failed to assign role: " + error.getMessage())
                                    );
                                },
                                error -> System.err.println("Failed to retrieve member: " + error.getMessage())
                        );
                    } else {
                        System.err.println("Member not found in event.");
                    }
                } else {
                    System.err.println("Bot does not have permission to manage roles.");
                }
            } else {
                System.err.println("Role not found.");
            }
        } else {
            System.err.println("Guild not found.");
        }
    }
}
