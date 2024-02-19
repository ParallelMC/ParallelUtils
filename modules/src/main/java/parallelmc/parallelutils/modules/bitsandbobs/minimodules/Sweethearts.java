package parallelmc.parallelutils.modules.bitsandbobs.minimodules;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class Sweethearts implements Listener {

    private final HashMap<UUID, UUID> healthDonors = new HashMap<>();

    public Sweethearts() {
        PluginManager manager = Bukkit.getPluginManager();
        Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);
        if (plugin == null) {
            ParallelUtils.log(Level.SEVERE, "Unable to enable Sweethearts. Plugin " + Constants.PLUGIN_NAME
                    + " does not exist!");
            return;
        }

        // had some weird issues when checking for sneaking in an event
        // so using a runnable seems to be the play

        plugin.getServer().getScheduler().runTaskTimer(plugin, this::checkForDonors, 0L, 16L);
    }

    public void checkForDonors() {
        for (Player donor : Bukkit.getOnlinePlayers()) {
            UUID donorUUID = donor.getUniqueId();
            if (donor.isSneaking()) {
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

                // Add donor and recipient to healthDonor list - key gets overridden if it's already in there
                healthDonors.put(donorUUID, recipient.getUniqueId());

                // Give 1 health from donor to recipient
                // If the recipient needs less than one heart, give them that
                donor.damage(1);
                if (recipientHealth + 1 > recipientMaxHealth) {
                    recipient.setHealth(recipientMaxHealth);
                } else {
                    recipient.setHealth(recipientHealth + 1);
                }

                // Spawn particles (idk why picking the function with the right parameters is so finicky)
                World world = donorLocation.getWorld();
                world.spawnParticle(Particle.DAMAGE_INDICATOR, donorLocation.getX(), donorLocation.getY() + 2,
                        donorLocation.getZ(), 5, 0, 0, 0, 0.255, null);
                world.spawnParticle(Particle.HEART, recipientLocation.getX(), recipientLocation.getY() + 2,
                        recipientLocation.getZ(), 2, 0.2, 0.2, 0.2, 1, null);
            }
            else {
                if (healthDonors.containsKey(donorUUID)) {
                    // Check if health > 0 to prevent clashing with the death event
                    if (donor.getHealth() > 0) {
                        // Remove donor from the donor list
                        healthDonors.remove(donorUUID);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player donor = event.getPlayer();
        UUID donorUUID = donor.getUniqueId();
        // Check if the player was a donor, and if so, remove the player from the healthDonor map and
        // add a custom death message
        if (healthDonors.containsKey(donorUUID)) {
            // Build and send the death message text component
            UUID recipientUUID = healthDonors.get(donorUUID);
            Player recipient = Bukkit.getPlayer(recipientUUID);
            final TextComponent deathMessage = Component.text(donor.getName() + " was shot through the heart and " +
                            recipient.getName() + " was to blame")
                    .color(NamedTextColor.WHITE);
            event.deathMessage(deathMessage);

            // Remove the player from the healthDonor map
            healthDonors.remove(donorUUID);

            // Award advancement
            awardAdvancement(donor);
        }
    }

    @EventHandler
    public void onTotemUse(EntityResurrectEvent event) {
        // Check if entity is being resurrected by a totem - if the event isn't cancelled, then they have a totem
        if (!event.isCancelled()) {
            Entity entity = event.getEntity();
            // Check if entity is a player
            if (entity instanceof Player donor) {
                // Check if entity is an active donor - if so, award the advancement
                UUID donorUUID = donor.getUniqueId();
                if (healthDonors.containsKey(donorUUID)) {
                    awardAdvancement(donor);
                }
            }
        }
    }

    public void awardAdvancement(Player donor) {
        Advancement a = Bukkit.getAdvancement(new NamespacedKey("platy",
                "forestry/sweethearts_death"));
        if (a != null) {
            AdvancementProgress avp = donor.getAdvancementProgress(a);
            if (!avp.isDone()) {
                for (String criteria : avp.getRemainingCriteria()) {
                    avp.awardCriteria(criteria);
                }
            }
        }
    }
}
