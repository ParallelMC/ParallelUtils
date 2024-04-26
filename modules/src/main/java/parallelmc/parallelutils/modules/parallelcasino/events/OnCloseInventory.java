package parallelmc.parallelutils.modules.parallelcasino.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import parallelmc.parallelutils.modules.parallelcasino.ParallelCasino;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

public class OnCloseInventory implements Listener {
    @EventHandler
    public void onCloseInventory(InventoryCloseEvent event) {
        Player player = (Player)event.getPlayer();
        if (ParallelCasino.get().isPlayerInGame(player)) {
            // if the player's inventory is closed, and they're registered as being in a casino game
            // assume they're playing a casino game
            ParallelCasino.get().removePlayerFromGame(player);
            // TODO: refund the player
            ParallelChat.sendParallelMessageTo(player, "Inventory closed before the game ended!");
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        // same as above
        if (ParallelCasino.get().isPlayerInGame(player)) {
            ParallelCasino.get().removePlayerFromGame(player);
        }
    }
}
