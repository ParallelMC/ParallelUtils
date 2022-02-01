package parallelmc.parallelutils.modules.bitsandbobs.minimodules;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class ShardLotto implements Listener {
    private final HashMap<UUID, Integer> playerRolls = new HashMap<>();
    private final Vector lantern = new Vector(-342, 24, -454);
    private final static String lootTable = "parallel_loot:events/ralnthar_treasure";
    private final static Component failureMsg = MiniMessage.miniMessage().deserialize("<gold><bold>Ralnthar </bold><gray>> <white>I don't have a need for more of your shards at the moment, but if you return at a later date, I may make it worth your while.");
    private final static Component successMsg = MiniMessage.miniMessage().deserialize("<gold><bold>Ralnthar </bold><gray>> <white>Thank you for lending your souls to me. Here's a small sample of my fortune in return.");

    @EventHandler
    public void onPlayerUseShard(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            UUID uuid = player.getUniqueId();
            Location loc = event.getClickedBlock().getLocation();
            if (loc.getBlockX() == lantern.getX() &&
                loc.getBlockY() == lantern.getY() &&
                loc.getBlockZ() == lantern.getZ() &&
                loc.getWorld().getName().equals("world") &&
                event.getClickedBlock().getType() == Material.SEA_LANTERN) {
                ItemStack held = event.getItem();
                if (held != null &&
                    held.getType() == Material.PRISMARINE_SHARD &&
                    held.getItemMeta().getCustomModelData() == 1000001) {
                    int rollsLeft;
                    if (playerRolls.containsKey(uuid)) {
                        rollsLeft = playerRolls.get(uuid);
                    }
                    else {
                        playerRolls.put(uuid, 3);
                        rollsLeft = 3;
                    }
                    if (rollsLeft > 0) {
                        player.sendMessage(successMsg);
                        // if inventory is full just drop it on the ground
                        // this is tested, give does nothing with a full inventory
                        if (player.getInventory().firstEmpty() == -1) {
                            Location pos = player.getLocation();
                            player.getServer().dispatchCommand(player.getServer().getConsoleSender(), String.format("loot spawn %d %d %d loot %s",
                                    pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(), lootTable));
                        }
                        else {
                            player.getServer().dispatchCommand(player.getServer().getConsoleSender(), "loot give " + player.getName() + " loot " + lootTable);
                        }
                        playerRolls.put(uuid, rollsLeft - 1);
                        event.getItem().subtract();
                    }
                    else {
                        player.sendMessage(failureMsg);
                    }
                }
            }
        }
    }
}
