package parallelmc.parallelutils.modules.bitsandbobs.minimodules;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.parallelitems.ParallelItems;
import parallelmc.parallelutils.util.RandomTools;

import java.util.logging.Level;

public class HalloweenCandyDrops implements Listener {

    private ParallelItems parallelItems;

    public HalloweenCandyDrops() {
        PluginManager manager = Bukkit.getPluginManager();
        Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);
        if (plugin == null) {
            ParallelUtils.log(Level.SEVERE, "Unable to enable Halloween Candy Drops. Plugin " + Constants.PLUGIN_NAME
                    + " does not exist!");
            return;
        }

        ParallelUtils puPlugin = (ParallelUtils) plugin;

        ParallelModule module = puPlugin.getModule("ParallelItems");
        if (module instanceof ParallelItems) {
            parallelItems = (ParallelItems) module;
        }
        else {
            ParallelUtils.log(Level.WARNING, "Unable to find ParallelItems module from give command.");
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        // 1 in 5 chance of the halloween candy dropping
        if (RandomTools.betweenTwoNumbers(1, 5) == 1) {
            ItemStack candy;
            EntityType entityType = event.getEntityType();
            switch (entityType) {
                case BLAZE:
                    candy = parallelItems.getItem("ralnthar_roar").clone();
                    event.getDrops().add(candy);
                    break;
                case CREEPER:
                    candy = parallelItems.getItem("creeper_crunch").clone();
                    event.getDrops().add(candy);
                    break;
                case ENDERMAN:
                    candy = parallelItems.getItem("raspberry_rift").clone();
                    event.getDrops().add(candy);
                    break;
                case EVOKER, ILLUSIONER, PILLAGER, VINDICATOR:
                    candy = parallelItems.getItem("green_apple_goo").clone();
                    event.getDrops().add(candy);
                    break;
                case GHAST:
                    candy = parallelItems.getItem("nether_portal_nougat").clone();
                    event.getDrops().add(candy);
                    break;
                case MAGMA_CUBE:
                    candy = parallelItems.getItem("magma_melt").clone();
                    event.getDrops().add(candy);
                    break;
                case PIGLIN, PIGLIN_BRUTE, ZOMBIFIED_PIGLIN:
                    candy = parallelItems.getItem("phantasmic_fudge").clone();
                    event.getDrops().add(candy);
                    break;
                case SKELETON:
                    candy = parallelItems.getItem("mysterious_mint").clone();
                    event.getDrops().add(candy);
                    break;
                case WITHER_SKELETON:
                    candy = parallelItems.getItem("pumpkin_pop").clone();
                    event.getDrops().add(candy);
                    break;
                case ZOMBIE:
                    candy = parallelItems.getItem("eerie_eyeball").clone();
                    event.getDrops().add(candy);
                    break;
            }
        }
    }
}
