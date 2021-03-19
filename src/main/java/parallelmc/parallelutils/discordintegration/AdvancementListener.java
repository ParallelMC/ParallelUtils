package parallelmc.parallelutils.discordintegration;

import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import parallelmc.parallelutils.Parallelutils;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

/**
 * Listen for new advancements and send messages to the discord when they occur
 */
public class AdvancementListener implements Listener {

	private static final List<String> SPECIAL_ADVANCEMENTS = Arrays.asList("minecraft:story/mine_diamond",
			"minecraft;story/shiny_gear", "minecraft:story/enchant_item", "minecraft:nether/obtain_ancient_debris",
			"minecraft:nether/netherite_armor", "minecraft:nether/get_wither_skull", "minecraft:nether/summon_wither",
			"minecraft:nether/brew_potion", "minecraft:nether/create_beacon", "minecraft:nether/create_full_beacon",
			"minecraft:end/dragon_egg", "minecraft:end/respawn_dragon", "minecraft:end/dragon_breath",
			"minecraft:end/elytra", "minecraft:adventure/throw_trident", "minecraft:adventure/totem_of_undying",
			"minecraft:adventure/summon_iron_golem", "minecraft:adventure/very_very_frightening",
			"minecraft:husbandry/obtain_netherite-hoe");

	@EventHandler
	public void onAch(PlayerAdvancementDoneEvent event) {
		Player player = event.getPlayer();

		Advancement advancement = event.getAdvancement();

		// player.sendMessage("Player " + player.getName() + " got advancement " + advancement.getKey().toString());

		if (SPECIAL_ADVANCEMENTS.contains(advancement.getKey().toString())) {
			if (BotManager.getInstance() != null) {
				if (!BotManager.getInstance().sendMessage("staff",
						"Player " + player.getName() + " got advancement " + advancement.getKey().getKey())) {
					Parallelutils.log(Level.WARNING, "Unable to send message. Unknown error.");
					Parallelutils.log(Level.WARNING,
							"Player " + player.getName() + " got advancement " + advancement.getKey().getKey());
				}
			} else {
				Parallelutils.log(Level.WARNING, "BotManager not initialized. Can't send message!");
				Parallelutils.log(Level.WARNING,
						"Player " + player.getName() + " got advancement " + advancement.getKey().getKey());
			}
		}
	}
}
