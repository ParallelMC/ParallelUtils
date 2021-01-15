package parallelmc.parallelutils.custommobs;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class WispLootTable implements LootTable {
    Plugin plugin;

    public WispLootTable(Plugin plugin){
        this.plugin = plugin;
    }


    @Override
    public Collection<ItemStack> populateLoot(@NotNull Random random, @NotNull LootContext lootContext) {
        ArrayList<ItemStack> loot = new ArrayList<>();
        loot.add(createShard());
        return loot;
    }

    @Override
    public void fillInventory(@NotNull Inventory inventory, @NotNull Random random, @NotNull LootContext lootContext) {
        inventory.addItem(createShard());
    }

    private ItemStack createShard(){
        ItemStack shard = new ItemStack(Material.PRISMARINE_SHARD, 1);
        try {
            ItemMeta shardMeta = shard.getItemMeta();
            shardMeta.setDisplayName("&fSoul Shard");
            shardMeta.setCustomModelData(1000001);
            shardMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            shardMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        catch(NullPointerException e){
            e.printStackTrace();
        }
        return shard;
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return new NamespacedKey(plugin, "soul-shard-item");
    }
}
