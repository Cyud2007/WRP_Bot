package com.code;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.code.commands.Admin.BanCommand;
import org.jetbrains.annotations.NotNull;

import com.code.commands.AddItemCommand;
import com.code.commands.BalanceCommand;
import com.code.commands.BallCommand;
import com.code.commands.BuyCommand;
import com.code.commands.CollectCommand;
import com.code.commands.EmbedCommand;
import com.code.commands.GiveMoneyCommand;
import com.code.commands.InfoCommand;
import com.code.commands.InventoryCommand;
import com.code.commands.WorkCommand;
import com.code.commands.PingCommand;
import com.code.commands.RemoveItemCommand;
import com.code.commands.RemoveMoneyCommand;
import com.code.commands.RulesCommand;
import com.code.commands.ShopCommand;
import com.code.commands.TestCommand;
import com.code.commands.ViewBalanceCommand;
import com.code.commands.Admin.MuteCommand;
import com.code.commands.Admin.KickCommand;
import com.code.commands.PayCommand;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BotListener extends ListenerAdapter {

    // Слеш команды
    private final PingCommand pingCommand = new PingCommand();
    private final InfoCommand infoCommand = new InfoCommand();
    private final ShopCommand shopCommand = new ShopCommand();
    private final InventoryCommand inventoryCommand = new InventoryCommand();
    private final BalanceCommand balanceCommand = new BalanceCommand();
    private final WorkCommand workCommand = new WorkCommand();
    private final BuyCommand buyCommand = new BuyCommand();
    private final GiveMoneyCommand giveMoneyCommand = new GiveMoneyCommand();
    private final RemoveMoneyCommand removeMoneyCommand = new RemoveMoneyCommand();
    private final ViewBalanceCommand viewBalanceCommand = new ViewBalanceCommand();
    private final AddItemCommand addItemCommand = new AddItemCommand();
    private final RemoveItemCommand removeItemCommand = new RemoveItemCommand();
    private final CollectCommand collectCommand = new CollectCommand();
    private final BallCommand ballCommand = new BallCommand();
    private final PayCommand payCommand = new PayCommand();
    private final MuteCommand muteCommand = new MuteCommand();
    private final KickCommand kickCommand = new KickCommand(); 

    // Текстовые команды
    private final TestCommand testCommand = new TestCommand();
    private final EmbedCommand embedCommand = new EmbedCommand();
    private final RulesCommand rulesCommand = new RulesCommand();

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
            case "shop":
                shopCommand.execute(event);
                break;
            case "inventory":
                inventoryCommand.execute(event);
                break;
            case "balance":
                balanceCommand.execute(event);
                break;
            case "work":
                workCommand.execute(event);
                break;
            case "buy":
                buyCommand.execute(event);
                break;
            case "pay":
                payCommand.execute(event);
                break;
            case "givemoney":
                giveMoneyCommand.execute(event);
                break;
            case "removemoney":
                removeMoneyCommand.execute(event);
                break;
            case "v-balance":
                viewBalanceCommand.execute(event);
                break;
            case "additem":
                addItemCommand.execute(event);
                break;
            case "removeitem":
                removeItemCommand.execute(event);
                break;
            case "collect":
                collectCommand.execute(event);
                break;
            case "ball":
                ballCommand.execute(event);
                break;
            case "kick":  // Добавляем обработку команды kick
                kickCommand.execute(event);
                break;
            case "ban":  // Обработка команды ban
                new BanCommand().execute(event);
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
                case "shop":
                    shopCommand.execute(event);
                    break;
                case "inventory":
                    inventoryCommand.execute(event);
                    break;
                case "balance":
                    balanceCommand.execute(event);
                    break;
                case "work":
                    workCommand.execute(event);
                    break;
                case "buy":
                    buyCommand.execute(event);
                    break;
                case "rules":
                    rulesCommand.execute(event);
                    break;
                case "mute":
                    muteCommand.execute(event);
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
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        String componentId = event.getComponentId();

        if (componentId.equals("menu:category")) {
        } else if (componentId.equals("menu:shop")) { 
            shopCommand.onStringSelectInteraction(event);
        }
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
