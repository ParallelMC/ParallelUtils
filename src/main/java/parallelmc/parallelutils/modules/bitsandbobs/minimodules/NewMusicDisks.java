package parallelmc.parallelutils.modules.bitsandbobs.minimodules;

import net.kyori.adventure.sound.SoundStop;
import net.minecraft.world.item.Items;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Jukebox;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class NewMusicDisks implements Listener {

    private final HashMap<Jukebox, Sound> locationsPlayingCustomDiscs = new HashMap<>();

//    @EventHandler
//    public void onDiscPlay(PlayerInteractEvent event) {
//        // Check if player clicked a jukebox
//        if (event.getClickedBlock().getType() == Material.JUKEBOX) {
//            // Check if player RIGHT-clicked the jukebox
//            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
//                Jukebox jukebox = (Jukebox) event.getClickedBlock().getState(); // Don't know if this is the best way to do this
//                if (!jukebox.isPlaying()) {
//                    // Check if the player clicked with a music disc (disc 11)
//                    if (event.getItem().getType() == Material.MUSIC_DISC_11) {
//                        System.out.println("hi");
//                        // Get location of jukebox
//                        Location location = jukebox.getLocation();
//                        location.getWorld().stopSound(SoundStop.named(Sound.MUSIC_DISC_11));
//
//                        // Play the alternative music
//                        location.getWorld().playSound(location, Sound.MUSIC_GAME, SoundCategory.RECORDS, 1, 1);
//                        // Add the jukebox location to the hashmap
//                        locationsPlayingCustomDiscs.put(jukebox, Sound.MUSIC_GAME);
//                    }
//                } else if (jukebox.isPlaying()) {
//                    // If the jukebox is in the hashmap, stop playing the custom sound and remove the jukebox from the hashmap
//                    if (locationsPlayingCustomDiscs.containsKey(jukebox)) {
//                        jukebox.getLocation().getWorld().stopSound(SoundStop.named(Sound.MUSIC_GAME));
//                        locationsPlayingCustomDiscs.remove(jukebox);
//                    }
//                }
//            }
//        }
//
//    }

    @EventHandler
    public void onDiscPlay(PlayerInteractEvent event) {
        // Check if player clicked a jukebox
        if (event.getClickedBlock().getType() == Material.JUKEBOX) {
            // Check if player RIGHT-clicked the jukebox
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Jukebox jukebox = (Jukebox) event.getClickedBlock().getState(); // Don't know if this is the best way to do this
                // Check if the jukebox does NOT have a disc in it - isPlaying() SHOULD be the way to check for a disc inside
                if (!jukebox.isPlaying()) {
                    ItemStack item = event.getItem();
                    // Necessary check for the item being null
                    if (item != null) {
                        // Check if the player clicked with a music disc (disc 11)
                        if (event.getItem().getType() == Material.MUSIC_DISC_11) {
                            // Cancel the event, remove the music disc from the player's inv, and insert it into the jukebox
                            event.setCancelled(true);
                            System.out.println("hi"); //TODO
                            ItemStack disc = event.getItem();
                            event.getPlayer().getInventory().remove(disc);
                            jukebox.update(); // Need to update blockstates
                            // Get location of jukebox & play the alternative music
                            Location location = jukebox.getLocation();
                            location.getWorld().playSound(location, Sound.MUSIC_GAME, SoundCategory.RECORDS, 1, 1);
                            // Add the jukebox location to the hashmap
                            locationsPlayingCustomDiscs.put(jukebox, Sound.MUSIC_GAME);
                        }
                    }
                  // Check if the jukebox has a disc in it - isPlaying() SHOULD be the way to check for a disc inside
                } else if (jukebox.isPlaying()) {
                    System.out.println("sup bruv");
                    // If the jukebox is in the hashmap, stop playing the custom sound and remove the jukebox from the hashmap
                    if (locationsPlayingCustomDiscs.containsKey(jukebox)) {
                        jukebox.getLocation().getWorld().stopSound(SoundStop.named(Sound.MUSIC_GAME));
                        locationsPlayingCustomDiscs.remove(jukebox);
                    }
                }
            }
        }

    }

//    @EventHandler
//    public void onJukeboxBreak(BlockBreakEvent event) {
//        Jukebox jukebox = (Jukebox) event.getBlock();
//
//    }

}
