package parallelmc.parallelutils.modules.paralleltowns;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelClassLoader;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.paralleltowns.commands.ParallelTownGUI;
import parallelmc.parallelutils.modules.paralleltowns.commands.TownCommands;
import parallelmc.parallelutils.modules.paralleltowns.events.OnMenuInteract;

import java.util.List;
import java.util.logging.Level;

public class ParallelTowns extends ParallelModule {

    private ParallelUtils puPlugin;

    public ParallelTowns(ParallelClassLoader classLoader, List<String> dependents) {
        super(classLoader, dependents);
    }

    public GUIManager guiManager;

    private TownCommands townCommands;

    private static ParallelTowns Instance;

    @Override
    public void onLoad() { }

    @Override
    public void onEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

        if (plugin == null) {
            ParallelUtils.log(Level.SEVERE, "Unable to enable ParallelTowns. Plugin " + Constants.PLUGIN_NAME
                    + " does not exist!");
            return;
        }

        this.puPlugin = (ParallelUtils) plugin;

        if (!puPlugin.registerModule(this)) {
            ParallelUtils.log(Level.SEVERE, "Unable to register module ParallelTowns! " +
                    "Module may already be registered. Quitting...");
            return;
        }

        guiManager = new GUIManager();

        manager.registerEvents(new OnMenuInteract(), puPlugin);

        townCommands = new TownCommands();
        puPlugin.getCommand("town").setExecutor(townCommands);
        townCommands.addCommand("gui", new ParallelTownGUI());

        Instance = this;
    }

    @Override
    public void onDisable() {}

    @Override
    public void onUnload() {}

    @Override
    public @NotNull String getName() {
        return "ParallelTowns";
    }

    public static ParallelTowns get() { return Instance; }
}