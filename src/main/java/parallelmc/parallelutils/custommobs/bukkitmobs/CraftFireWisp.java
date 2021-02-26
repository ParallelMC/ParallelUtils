package parallelmc.parallelutils.custommobs.bukkitmobs;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftZombie;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Random;

import static org.bukkit.attribute.Attribute.*;

public class CraftFireWisp {

    public static void setupNBT(Plugin plugin, CraftZombie entity) {
        //Attributes
        //Health = 30
        entity.getAttribute(GENERIC_MAX_HEALTH).setBaseValue(50.0);
        entity.setHealth(entity.getAttribute(GENERIC_MAX_HEALTH).getBaseValue());

        //Damage = 8
        entity.getAttribute(GENERIC_ATTACK_DAMAGE).setBaseValue(12.0);

        //Follow range = 16
        entity.getAttribute(GENERIC_FOLLOW_RANGE).setBaseValue(16.0);

        //Movement speed = 0.25
        entity.getAttribute(GENERIC_MOVEMENT_SPEED).setBaseValue(0.25);

        //No zombie reinforcements
        entity.getAttribute(ZOMBIE_SPAWN_REINFORCEMENTS).setBaseValue(0);

        //Other stuff

        //invis
        entity.setInvisible(true);

        //can't pickup items
        entity.setCanPickupItems(false);

        //no random equipment
        entity.getEquipment().clear();

        entity.setShouldBurnInDay(false);

        //silent
        entity.setSilent(true);
    }

    public static void deathLoot(EntityDeathEvent event) {
        ItemStack shard = new ItemStack(Material.PRISMARINE_SHARD, 1);
        try {
            ItemMeta shardMeta = shard.getItemMeta();
            shardMeta.setDisplayName(ChatColor.GOLD + "Unstable Soul Shard");
            shardMeta.setCustomModelData(1000002);
            shardMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            shardMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            shard.setItemMeta(shardMeta);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        List<ItemStack> drops = event.getDrops();
        drops.clear();
        drops.add(shard);

        Random random = new Random();
        if(random.nextDouble() <= 0.5){
            drops.add(new ItemStack(Material.FIRE_CHARGE, 1));
        }

        event.setDroppedExp(0);
    }
}
