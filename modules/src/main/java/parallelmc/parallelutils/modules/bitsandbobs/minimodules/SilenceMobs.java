package parallelmc.parallelutils.modules.bitsandbobs.minimodules;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Objects;

public class SilenceMobs implements Listener {

    @EventHandler
    public void useSilenceNametag(PlayerInteractEntityEvent event) {
        EquipmentSlot slot = event.getHand();
        Player player = event.getPlayer();
        if (!player.getEquipment().getItem(slot).getType().equals(Material.NAME_TAG)) {
            return;
        }
        // If the player right clicked a non-living entity or a player, return
        // This is to prevent a player accidentally editing another player's playerdata
        if (!(event.getRightClicked() instanceof LivingEntity entity) || event.getRightClicked() instanceof Player) {
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
