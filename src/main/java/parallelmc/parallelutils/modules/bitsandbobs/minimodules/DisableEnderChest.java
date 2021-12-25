package parallelmc.parallelutils.modules.bitsandbobs.minimodules;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class DisableEnderChest implements Listener {

	private static final Component msgFrom = MiniMessage.get().parse("<white><bold>[</bold><yellow> Niall <dark_aqua> -> <yellow><bold>You<white>]</bold><aqua> Enderchests are too unstable to use right now!");

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onUseEnderchest(PlayerInteractEvent event) {
		if (event.getClickedBlock() != null) {
			Block block = event.getClickedBlock();

			if (block.getType() == Material.ENDER_CHEST) {
				if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					event.setCancelled(true);
					
					event.getPlayer().sendMessage(msgFrom);
				}
			}
		}
	}
}
