package com.code.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import com.code.data.UserData;
import com.code.data.UserDataManager;
import com.code.shop.ShopItem;
import com.code.shop.ShopManager;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class BuyCommand extends Command {

    private static final Map<String, Integer> ECONOMY_LEVELS = new HashMap<>();
    private static final Map<String, String> TECHNOLOGIES = new HashMap<>();

    static {
        // Initialization of economic levels
        ECONOMY_LEVELS.put("Economy lv1", 1);
        ECONOMY_LEVELS.put("Economy lv2", 2);
        ECONOMY_LEVELS.put("Economy lv3", 3);
        ECONOMY_LEVELS.put("Economy lv4", 4);
        ECONOMY_LEVELS.put("Economy lv5", 5);
        ECONOMY_LEVELS.put("Economy lv6", 6);
        ECONOMY_LEVELS.put("Economy lv7", 7);

        // Initialization of technologies
        TECHNOLOGIES.put("Aircraft plant", "1279083348504875133"); // ID роли
        TECHNOLOGIES.put("Tank factory", "1279083322911494249"); // ID роли
        TECHNOLOGIES.put("Shipyard", "1279083382160228444"); // ID роли
        TECHNOLOGIES.put("Car plant", "1279083382160228444"); // ID роли
        TECHNOLOGIES.put("Nuclear complex", "1279092087027011595"); // ID роли
    }

    @Override
    public CommandData createCommand() {
        return Commands.slash("buy", "Buy a product in a store.")
                .addOption(net.dv8tion.jda.api.interactions.commands.OptionType.STRING, "item", "Product name", true)
                .addOption(net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER, "quantity", "Quantity (default 1)", false);
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        String username = event.getUser().getName();
        String itemName = event.getOption("item").getAsString();
        int quantity = event.getOption("quantity") != null ? event.getOption("quantity").getAsInt() : 1;

        // We check if a player is trying to buy an economy or technology and prohibit the entry of quantities.
        if ((itemName.toLowerCase().startsWith("economy") || TECHNOLOGIES.containsKey(itemName))) {
            if (event.getOption("quantity") != null && quantity != 1) {
                event.replyEmbeds(new EmbedBuilder()
                        .setTitle("Error")
                        .setDescription("It is not possible to specify a quantity for this purchase..")
                        .setColor(Color.RED)
                        .build()).queue();
                return;
            }
        }

        // Testing the feasibility of purchasing previous levels of the economy
        if (itemName.toLowerCase().startsWith("экономика")) {
            if (!canPurchaseEconomyLevel(event, itemName)) {
                event.replyEmbeds(new EmbedBuilder()
                        .setTitle("Error")
                        .setDescription("You cannot buy this level of economy because you already have this level or a higher one..")
                        .setColor(Color.RED)
                        .build()).queue();
                return;
            }
        }

        // Checking the feasibility of purchasing technology
        if (TECHNOLOGIES.containsKey(itemName) && !canPurchaseTechnology(event, itemName)) {
            event.replyEmbeds(new EmbedBuilder()
                    .setTitle("Error")
                    .setDescription("You can't buy this technology because you already have it..")
                    .setColor(Color.RED)
                    .build()).queue();
            return;
        }

        ShopItem shopItem = ShopManager.getItemByName(itemName);
        if (shopItem == null) {
            event.replyEmbeds(new EmbedBuilder()
                    .setTitle("Error")
                    .setDescription("This product is not available in the store.")
                    .setColor(Color.RED)
                    .build()).queue();
            return;
        }

        int totalPrice = shopItem.getPrice() * quantity;
        UserData userData = UserDataManager.getUserData(username);

        if (userData.getBalance() < totalPrice) {
            event.replyEmbeds(new EmbedBuilder()
                    .setTitle("Insufficient funds")
                    .setDescription("You do not have sufficient funds to purchase.")
                    .setColor(Color.RED)
                    .build()).queue();
            return;
        }

        // Updating the player's balance
        userData.setBalance(userData.getBalance() - totalPrice);

        // The logic of buying the level of the economy
        if (itemName.toLowerCase().startsWith("экономика")) {
            removeOldEconomyRoles(event, userData);  // Removing the previous roles of the economy
            assignNewEconomyRole(event, itemName);  // Assigning a new role
        }
        // The logic of purchasing technology
        else if (TECHNOLOGIES.containsKey(itemName)) {
            assignTechnologyRole(event, TECHNOLOGIES.get(itemName));  // Assigning a role to technology
        } else {
            userData.addToInventory(shopItem.getName(), quantity);
        }

        UserDataManager.updateUserData(userData);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Purchase successful");
        embedBuilder.setColor(new Color(75, 0, 130));
        embedBuilder.setDescription("You bought " + shopItem.getName() + " (x" + quantity + ").");
        embedBuilder.addField("Price", totalPrice + " coins", false);
        embedBuilder.addField("Remaining balance", userData.getBalance() + " coins", false);

        event.replyEmbeds(embedBuilder.build()).queue();
    }

    private boolean canPurchaseEconomyLevel(SlashCommandInteractionEvent event, String itemName) {
        int newEconomyLevel = ECONOMY_LEVELS.getOrDefault(itemName, 0);

        for (String roleName : ECONOMY_LEVELS.keySet()) {
            if (event.getMember().getRoles().stream().anyMatch(role -> role.getName().equals(roleName))) {
                int existingEconomyLevel = ECONOMY_LEVELS.get(roleName);
                if (existingEconomyLevel >= newEconomyLevel) {
                    return false; // You cannot buy a lower or the same level
                }
            }
        }

        return true; // Purchase allowed
    }

    private boolean canPurchaseTechnology(SlashCommandInteractionEvent event, String itemName) {
        String roleId = TECHNOLOGIES.get(itemName);
        return event.getMember().getRoles().stream().noneMatch(role -> role.getId().equals(roleId));
    }

    private void removeOldEconomyRoles(SlashCommandInteractionEvent event, UserData userData) {
        String[] economyRoles = {"Economy lv1", "Economy lv2", "Economy lv3", "Economy lv4", "Economy lv5", "Economy lv6", "Economy lv7""};

        for (String roleName : economyRoles) {
            event.getGuild().getRolesByName(roleName, true).forEach(role -> {
                if (event.getMember().getRoles().contains(role)) {
                    event.getGuild().removeRoleFromMember(event.getMember(), role).queue();
                }
            });
        }
    }

    private void assignNewEconomyRole(SlashCommandInteractionEvent event, String itemName) {
        event.getGuild().getRolesByName(itemName, true).forEach(role -> {
            event.getGuild().addRoleToMember(event.getMember(), role).queue();
        });
    }

    private void assignTechnologyRole(SlashCommandInteractionEvent event, String roleId) {
        event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById(roleId)).queue();
    }
}
