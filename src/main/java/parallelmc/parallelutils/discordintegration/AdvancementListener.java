package parallelmc.parallelutils.discordintegration;

import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.util.TimeTools;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

/**
 * Listen for new advancements and send messages to the discord when they occur
 */
public class AdvancementListener implements Listener {

	private static final List<String> SPECIAL_ADVANCEMENTS = Arrays.asList("minecraft:story/mine_diamond",
			"minecraft:story/shiny_gear", "minecraft:story/enchant_item", "minecraft:nether/obtain_ancient_debris",
			"minecraft:nether/netherite_armor", "minecraft:nether/get_wither_skull", "minecraft:nether/summon_wither",
			"minecraft:nether/brew_potion", "minecraft:nether/create_beacon", "minecraft:nether/create_full_beacon",
			"minecraft:end/dragon_egg", "minecraft:end/respawn_dragon", "minecraft:end/dragon_breath",
			"minecraft:end/elytra", "minecraft:adventure/throw_trident", "minecraft:adventure/totem_of_undying",
			"minecraft:adventure/summon_iron_golem", "minecraft:adventure/very_very_frightening",
			"minecraft:husbandry/obtain_netherite-hoe", "platy:building/sea_lantern", "platy:building/end_rod",
			"platy:caving/all_ores", "platy:combat/monster_skull_64", "platy:combat/golden_apple",
			"platy:combat/music_disc", "platy:combat/overpowered", "platy:combat/special_arrow",
			"platy:combat/all_music_discs", "platy:combat/perfect_crossbow", "platy:forestry/perfect_axe",
			"minecraft:story/diamond_blocks", "minecraft:story/fire_aspect", "minecraft:story/overkill",
			"minecraft:story/mending", "minecraft:story/perfect_armor", "minecraft:story/perfect_sword",
			"minecraft:story/perfect_bow", "minecraft:story/perfect_pickaxe", "minecraft:nether/soul_speed",
			"minecraft:nether/netherite_blocks", "minecraft:nether/all_netherite",
			"platy:nether_structures/gilded_blackstone", "platy:nether_structures/pigstep",
			"platy:nether_structures/summon_wither", "platy:nether_structures/create_full_beacon",
			"platy:nether_structures/wither_rose", "platy:nether_structures/wither_skull",
			"platy:nether_structures/create_beacon", "platy:exploration/postmortal", "platy:ocean/perfect_fishing_rod",
			"platy:ocean/heart_and_soul", "platy:ocean/throw_trident", "platy:ocean/very_very_frightening",
			"platy:ocean/dry_sponge", "platy:ocean/perfect_trident", "platy:ocean/riptide", "platy:ocean/frost_walker",
			"platy:redstone/tnt", "platy:redstone/tnt_minecart", "minecraft:end/dragon_head",
			"minecraft:end/shulker_box", "minecraft:end/lingering_potion", "platy:village/villager_kill");

	@EventHandler
	public void onAch(PlayerAdvancementDoneEvent event) {
		Player player = event.getPlayer();

		String formattedLocation = formatLocation(player.getLocation());

		int playtime = player.getStatistic(Statistic.PLAY_ONE_MINUTE); // Ticks played

		String fulltime = TimeTools.fullTime(playtime, TimeTools.TimeUnit.TICKS);

		Advancement advancement = event.getAdvancement();

		Parallelutils.log(Level.INFO,"Player " + player.getName() + " got advancement " + advancement.getKey().toString());

		if (SPECIAL_ADVANCEMENTS.contains(advancement.getKey().toString())) {
			if (BotManager.getInstance() != null) {
				if (!BotManager.getInstance().sendMessage("staff",
						"Player `" + player.getName() + "` got advancement " + advancement.getKey().getKey() + "." +
								" They are at " + formattedLocation + " and have " + fulltime + " of playtime")) {
					Parallelutils.log(Level.WARNING, "Unable to send message. Unknown error.");
					Parallelutils.log(Level.WARNING,
							"Player " + player.getName() + " got advancement " + advancement.getKey().getKey() + "." +
									" They are at " + formattedLocation + " and have " + fulltime + " of playtime");
				}
			} else {
				Parallelutils.log(Level.WARNING, "BotManager not initialized. Can't send message!");
				Parallelutils.log(Level.WARNING,
						"Player " + player.getName() + " got advancement " + advancement.getKey().getKey() + "." +
								" They are at " + formattedLocation + " and have " + fulltime + " of playtime");
			}
		}
	}

	/**
	 * Formats a Location object as a String
	 * @param loc The Location object to format
	 * @return The formatted String
	 */
	private String formatLocation(Location loc){
		return "X: " + loc.getBlockX() + " Y: " + loc.getBlockY() + " Z: " + loc.getBlockZ() + " in " +
				loc.getWorld().getName();
	}
}
