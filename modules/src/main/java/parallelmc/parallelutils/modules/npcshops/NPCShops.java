package parallelmc.parallelutils.modules.npcshops;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelClassLoader;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.npcshops.maggieshop.MaggieShop;

import java.util.List;
import java.util.logging.Level;

public class NPCShops extends ParallelModule {
    private MaggieShop maggieShop;

    public NPCShops(ParallelClassLoader classLoader, List<String> dependents) { super(classLoader, dependents); }
    private static NPCShops Instance;

    public static NPCShops get() {
        return Instance;
    }

    @Override
    public void onLoad() { }

    @Override
    public void onEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

        if (plugin == null) {
            ParallelUtils.log(Level.SEVERE, "Unable to enable NPCShops. Plugin " + Constants.PLUGIN_NAME + " does not exist!");
            return;
        }

        ParallelUtils puPlugin = (ParallelUtils) plugin;

        if (!puPlugin.registerModule(this)) {
            ParallelUtils.log(Level.SEVERE, "Unable to register module NPCShops! Module may already be registered. Quitting...");
            return;
        }

        maggieShop = new MaggieShop(puPlugin);

        Instance = this;
    }


    public MaggieShop getMaggieShop() { return maggieShop; }

    @Override
    public void onDisable() { }

    @Override
    public void onUnload() { }

    @NotNull
    @Override
    public String getName() { return "NPCShops"; }
}
