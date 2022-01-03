package parallelmc.parallelutils.modules.bitsandbobs.minimodules;

import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class PreventSpawnerMining implements Listener {

	private static final Component message = Component.text("§3[§f§lP§3] §cSpawners can't be mined - use torches to light it up instead!");

	@EventHandler(priority = EventPriority.HIGHEST)
	public void preventMining(BlockBreakEvent event) {
		Block block = event.getBlock();

		if (block.getType() == Material.SPAWNER) {

			Player player = event.getPlayer();

			if (player.getGameMode() != GameMode.CREATIVE) {
				event.setCancelled(true);

				player.sendMessage(message);
			}
		}
	}
}
