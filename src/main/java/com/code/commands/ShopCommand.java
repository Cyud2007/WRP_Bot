package com.code.commands;

import java.awt.Color;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

public class ShopCommand extends Command {

    @Override
    public CommandData createCommand() {
        return Commands.slash("shop", "test menu.");
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        StringSelectMenu menu = StringSelectMenu.create("menu:shop")
                .setPlaceholder("Select a category")
                .addOption("Army", "army", Emoji.fromUnicode("🏳️‍🌈"))
                .addOption("Technologies", "tech", Emoji.fromUnicode("🚄"))
                .addOption("Economy", "economy", Emoji.fromUnicode("🏭"))
                .build();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Select a category");
        embedBuilder.setDescription("Use the menu below to select a category.");
        embedBuilder.setColor(new Color(255, 165, 3)); // Orange

        event.replyEmbeds(embedBuilder.build())
             .addActionRow(menu)
             .setEphemeral(false) // The message will be visible to everyone.
             .queue();
    }

    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        String selected = event.getValues().get(0);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(new Color(255, 165, 0)); // Orange

        switch (selected) {
            case "army":
                embedBuilder.setTitle("Products in the Army category");
                embedBuilder.setDescription(
                        "0. **Personnel**:\n" +
                                "   - Infantry (Price: 500 coins)\n" +
                                "   - Special Forces (Price: 1000 coins)\n\n" +

                                "1. **Light equipment**:\n" +
                                "   - Armored Personnel Carrier (Price: 5000 coins)\n" +
                                "   - Lightweight Multi-Purpose Vehicle (Price: 4000 Coins)\n" +
                                "   - Reconnaissance Vehicle (Price: 6000 coins)\n\n" +

                                "2. **Heavy equipment**:\n" +
                                "   - Main Battle Tank (Price: 20,000 coins)\n" +
                                "   - Self-propelled artillery unit (Price: 18,000 coins)\n" +
                                "   - Infantry Fighting Vehicle (Price: 15,000 coins)\n" +
                                "   - Multiple Launch Rocket System (Price: 25,000 coins)\n" +
                                "   - Engineering Vehicle (Price: 22,000 coins)\n\n" +

                                "3. **Air defense systems**:\n" +
                                "   - Anti-aircraft missile and gun system (Price: 30,000 coins)\n" +
                                "   - Anti-aircraft missile system (Price: 35,000 coins)\n\n" +

                                "4. **Aircraft**:\n" +
                                "   - Fighter (Price: 50,000 coins)\n" +
                                "   - Bomber (Price: 60,000 coins)\n" +
                                "   - Stormtrooper (Price: 55,000 coins)\n" +
                                "   - Fire Support Helicopter (Price: 45,000 coins)\n" +
                                "   - Military Transport Plane (Price: 40,000 coins)\n\n" +

                                "5. **Marine technology**:\n" +
                                "   - Aircraft Carrier (Price: 100,000 coins)\n" +
                                "   - Missile Cruiser (Price: 90,000 coins)\n" +
                                "   - Ballistic Missile Submarine (Price: 95,000 coins)\n\n" +

                                "6. **Nuclear technology**:\n" +
                                "   - Intercontinental Ballistic Missile (Price: 200,000 coins)\n" +
                                "   - Strategic Bomber (Price: 250,000 coins)\n"
                );
                break;
            case "tech":
                embedBuilder.setTitle("Products in the **Technology category**");
                embedBuilder.setDescription("Accessible Technologies:\n" +
                        "- Car Factory (Price: 500 coins)\n" +
                        "- Tank Factory (Price: 1000 coins)\n" +
                        "- Aircraft Factory (Price: 2000 coins)\n" +
                        "- Shipyard (Price: 5000 coins)\n" +
                        "- Nuclear Complex (Price: 10,000 coins)");
                break;
            case "economy":
                embedBuilder.setTitle("Products in the category **Economy**");
                embedBuilder.setDescription("Available levels of economy:\n" +
                        "- Economy Lv2 (Price: 500 coins)\n" +
                        "- Economy Lv3 (Price: 1000 coins)\n" +
                        "- Economy Lv4 (Price: 2000 coins)\n" +
                        "- Economy Lv5 (Price: 5000 coins)\n" +
                        "- Economy Lv6 (Price: 10000 coins)\n" +
                        "- Economy Lv7 (Price: 20000 coins)");
                break;
            default:
                embedBuilder.setTitle("Unknown category");
                embedBuilder.setDescription("The selected category is not recognized.");
                break;
        }

        event.editMessageEmbeds(embedBuilder.build()).queue(); // Updating the message
    }
}
