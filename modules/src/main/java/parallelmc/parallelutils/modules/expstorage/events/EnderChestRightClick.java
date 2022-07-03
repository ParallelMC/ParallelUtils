package parallelmc.parallelutils.modules.expstorage.events;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.expstorage.ExpDatabase;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

public class EnderChestRightClick implements Listener {

	private final ParallelUtils puPlugin;
	private final ExpDatabase db;

	public EnderChestRightClick(ParallelUtils puPlugin, ExpDatabase expDatabase) {
		this.puPlugin = puPlugin;
		this.db = expDatabase;
	}

	@EventHandler
	public void onPlayerRightClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (event.getClickedBlock() == null) return;

			if (event.getClickedBlock().getType() == Material.ENDER_CHEST) {
				Player player = event.getPlayer();

				Bukkit.getScheduler().runTaskAsynchronously(puPlugin, new Runnable() {
					@Override
					public void run() {
						int totalExp = db.getExpForPlayer(player.getUniqueId().toString());
						ParallelChat.sendMessageTo(player, "You currently have " + totalExp + " stored experience.");
					}
				});
			}
		}
	}

}
