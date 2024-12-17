package parallelmc.parallelutils.modules.bitsandbobs.minimodules;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelUtils;

import java.util.Objects;
import java.util.logging.Level;

public class SilenceMobs implements Listener {

    @EventHandler
    public void useSilenceNametag(PlayerInteractEntityEvent event) {
        EquipmentSlot slot = event.getHand();
        Player player = event.getPlayer();
        if (!player.getEquipment().getItem(slot).getType().equals(Material.NAME_TAG)) {
            return;
        }
        if (!(event.getRightClicked() instanceof LivingEntity entity)) {
            return;
        }
        TextComponent nametagName = (TextComponent) player.getEquipment().getItem(slot).getItemMeta().displayName();
        // If name tag is "silence me", silence the mob. If the name tag contents are different and the entity was
        // previously silenced by a "silence me" name tag, un-silence the mob
        if (nametagName != null && nametagName.content().equalsIgnoreCase("silence me")) {
            entity.setSilent(true);
            entity.customName(Component.text("Silenced"));
            if (!player.getGameMode().equals(GameMode.CREATIVE)) {
                player.getEquipment().getItem(slot).subtract();
            }
            event.setCancelled(true);
        } else {
            if (Objects.equals(entity.customName(), Component.text("Silenced"))) {
                entity.setSilent(false);
            }
        }
    }
}
