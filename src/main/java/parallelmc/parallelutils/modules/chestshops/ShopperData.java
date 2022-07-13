package parallelmc.parallelutils.modules.chestshops;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

// stores various data for use in shop events
public record ShopperData(Inventory fakeInv, Inventory chestInv, Shop shop, ItemStack diamonds) { }
