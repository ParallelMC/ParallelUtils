package parallelmc.parallelutils;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import parallelmc.parallelutils.custommobs.EntityPair;

import java.util.List;
import java.util.logging.Level;

public class ParallelListener implements Listener {
	@EventHandler
	public void onEntityDespawn(final EntityRemoveFromWorldEvent event) {
		CraftEntity entity = (CraftEntity)event.getEntity();
		if (Registry.containsEntity(entity.getUniqueId().toString())) {
			Bukkit.getLogger().log(Level.ALL, "[ParallelUtils] Removing entity " + entity.getUniqueId().toString() + " from world");
			Registry.removeEntity(entity.getUniqueId().toString());
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event){
		Player player = event.getEntity();
		EntityDamageEvent lastDamage = player.getLastDamageCause();
		if(lastDamage instanceof EntityDamageByEntityEvent){
			org.bukkit.entity.Entity killer = ((EntityDamageByEntityEvent) lastDamage).getDamager();
			if(Registry.containsEntity(killer.getUniqueId().toString())){
				EntityPair pair = Registry.getEntity(killer.getUniqueId().toString());
				switch(pair.type){
					case "wisp":
						event.setDeathMessage(player.getDisplayName() + " was slain by Wisp");
						break;
				}
			}
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event){
		EntityPair pair = Registry.getEntity(event.getEntity().getUniqueId().toString());
		if(pair != null){
			switch(pair.type){
				case "wisp":
					ItemStack shard = new ItemStack(Material.PRISMARINE_SHARD, 1);
					try {
						ItemMeta shardMeta = shard.getItemMeta();
						shardMeta.setDisplayName(ChatColor.WHITE + "Soul Shard");
						shardMeta.setCustomModelData(1000001);
						shardMeta.addEnchant(Enchantment.DURABILITY, 1, true);
						shardMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
						shard.setItemMeta(shardMeta);
					}
					catch(NullPointerException e){
						e.printStackTrace();
					}
					List<ItemStack> drops = event.getDrops();
					drops.clear();
					drops.add(shard);
					event.setDroppedExp(0);
					break;
			}
		}
	}
}
