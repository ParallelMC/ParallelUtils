package parallelmc.parallelutils.modules.bitsandbobs.minimodules;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class Sweethearts implements Listener {

    private final HashMap<UUID, UUID> healthDonors = new HashMap<>();
    private BukkitTask runnable = null;
    private final Plugin plugin;

    public Sweethearts() {
        PluginManager manager = Bukkit.getPluginManager();
        plugin = manager.getPlugin(Constants.PLUGIN_NAME);
        if (plugin == null) {
            ParallelUtils.log(Level.SEVERE, "Unable to enable Sweethearts. Plugin " + Constants.PLUGIN_NAME
                    + " does not exist!");
        }
    }

    @EventHandler
    public void onPlayerShift(PlayerToggleSneakEvent event) {
        Player donor = event.getPlayer();
        if (event.isSneaking()) {
            runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    // If the donor is sneaking but not holding a poppy, return
                    if (!(donor.getInventory().getItemInMainHand().getType().equals(Material.POPPY) ||
                        donor.getInventory().getItemInOffHand().getType().equals(Material.POPPY))) {
                        return;
                    }
                    // Get a list of all players in a 3-block radius (ty Paper for the epic function)
                    Location donorLocation = donor.getLocation();
                    Collection<Player> nearbyPlayersCollection =
                            donor.getWorld().getNearbyEntitiesByType(Player.class, donorLocation, 3);
                    ArrayList<Player> nearbyPlayers = new ArrayList<>(nearbyPlayersCollection);
                    // Check if there are more players than just the donor themselves within a 3-block radius
                    if (!(nearbyPlayers.size() > 1)) {
                        return;
                    }

                    // Iterate over all players within the radius and find the closest player
                    UUID donorUUID = donor.getUniqueId();
                    double smallestDistance = Double.MAX_VALUE;
                    Player recipient = null;
                    Location recipientLocation = null;

                    for (Player player : nearbyPlayers) {
                        if (!(player.getUniqueId().equals(donorUUID))) {
                            Location playerLocation = player.getLocation();
                            double distance = donorLocation.distanceSquared(playerLocation); // using distance squared to avoid expensive square roots
                            if (distance < smallestDistance) {
                                smallestDistance = distance;
                                recipient = player;
                                recipientLocation = player.getLocation();
                            }
                        }
                    }

                    // Check if recipient needs to be healed
                    double recipientHealth = recipient.getHealth();
                    double recipientMaxHealth = recipient.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
                    if (!(recipientHealth < recipientMaxHealth)) {
                        return;
                    }

                    // Add donor and recipient to healthDonor list
                    // The remove function does a check to see if the donor's in the list
                    healthDonors.remove(donorUUID);
                    healthDonors.put(donorUUID, recipient.getUniqueId());

                    // Give 1 health from donor to recipient
                    donor.damage(1);
                    recipient.setHealth(recipientHealth + 1);

                    // Spawn particles (idk why picking the function with the right parameters is so finicky)
                    World world = donorLocation.getWorld();
                    world.spawnParticle(Particle.DAMAGE_INDICATOR, donorLocation.getX(), donorLocation.getY() + 2,
                            donorLocation.getZ(), 5, 0, 0, 0, 0.255, null);
                    world.spawnParticle(Particle.HEART, recipientLocation.getX(), recipientLocation.getY() + 2,
                            recipientLocation.getZ(), 2, 0.2, 0.2, 0.2, 1, null);
                }
            }.runTaskTimer(plugin, 16L, 16L);

        } else {
            // if the runnable is running for the player, stop it
            if (!runnable.isCancelled()) {
                runnable.cancel();
            }
            // Remove donor from the donor list
            healthDonors.remove(donor.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player donor = event.getPlayer();
        UUID donorUUID = donor.getUniqueId();
        // Check if the player was a donor, and if so, remove the player from the healthDonor list and
        // add a custom death message
        if (healthDonors.containsKey(donorUUID)) {
            if (!runnable.isCancelled()) { // may be redundant because dying unshifts the player, but it's here just in case
                runnable.cancel();
            } else {
                ParallelUtils.log(Level.WARNING, "Player " + donor.getName() +
                        " was listed as a healthDonor but they weren't actively donating health. Something is wrong!");
            }

            // Build and send the death message text component
            UUID recipientUUID = healthDonors.get(donorUUID);
            Player recipient = Bukkit.getPlayer(recipientUUID);
            final TextComponent deathMessage = Component.text(donor.getName() + " was shot through the heart and " +
                    recipient.getName() + " was to blame")
                            .color(NamedTextColor.WHITE);
            event.deathMessage(deathMessage);

            healthDonors.remove(donorUUID);

            // todo: give advancement and entityresurrectevent
        }
    }

    public void onTotemUse(EntityResurrectEvent event) {

    }

}
