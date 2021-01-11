package parallelmc.parallelutils.custommobs;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
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

    @NotNull
    @Override
    public Collection<ItemStack> populateLoot(@NotNull Random random, @NotNull LootContext lootContext) {
        //TODO: ask DB how soul shards work so I can make an ItemStack of them
        return new ArrayList<>();
    }

    @Override
    public void fillInventory(@NotNull Inventory inventory, @NotNull Random random, @NotNull LootContext lootContext) {

    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return new NamespacedKey(plugin, "");
    }
}
