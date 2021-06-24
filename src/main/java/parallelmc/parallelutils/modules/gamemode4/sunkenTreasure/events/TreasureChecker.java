package parallelmc.parallelutils.modules.gamemode4.sunkenTreasure.events;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import parallelmc.parallelutils.Parallelutils;

import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

public class TreasureChecker implements Listener {

	private final String lootTable;

	public TreasureChecker(String lootTable) {
		this.lootTable = lootTable;
	}

	@EventHandler
	public void onBreakBlock(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block blockBroken = event.getBlock();

		// Block broken must be water
		if (blockBroken.getBlockData().getMaterial().equals(Material.SAND)) {

			// Player must be submerged in water
			if (player.isInWater()) {
				World playerWorld = player.getLocation().getWorld();

				// There must be 4 blocks of water above the player
				Block block1 = playerWorld.getBlockAt(player.getLocation().add(0, 2, 0));
				Block block2 = playerWorld.getBlockAt(player.getLocation().add(0, 3, 0));
				Block block3 = playerWorld.getBlockAt(player.getLocation().add(0, 4, 0));
				Block block4 = playerWorld.getBlockAt(player.getLocation().add(0, 5, 0));
				if (block1.getBlockData().getMaterial().equals(Material.WATER) &&
						block2.getBlockData().getMaterial().equals(Material.WATER) &&
						block3.getBlockData().getMaterial().equals(Material.WATER) &&
						block4.getBlockData().getMaterial().equals(Material.WATER)) {
					// Can drop loot
					// What's the chance?
					ItemStack heldItem = player.getInventory().getItemInMainHand();
					Map<Enchantment, Integer> enchantments = heldItem.getEnchantments();

					Integer enchantVal = enchantments.get(Enchantment.LOOT_BONUS_BLOCKS);

					Parallelutils.log(Level.INFO, "" + enchantVal);

					if (enchantVal == null) enchantVal = 0;

					double minVal = switch (enchantVal) {
						case 1 -> 0.1;
						case 2 -> 0.26;
						case 3 -> 0.48;
						default -> -1; // Never spawn treasure here
					};

					Random random = new Random();

					if (random.nextDouble() < minVal) {
						// Drop loot!
						player.getServer().dispatchCommand(player.getServer().getConsoleSender(), "loot spawn "
						+ blockBroken.getX() + " " + blockBroken.getY() + " " + blockBroken.getZ() + " loot " + lootTable);
					}
				}
			}
		}
	}

}
