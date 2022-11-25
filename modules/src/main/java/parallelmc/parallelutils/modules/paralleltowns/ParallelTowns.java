package parallelmc.parallelutils.modules.paralleltowns;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelClassLoader;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.paralleltowns.commands.ParallelCreateTown;
import parallelmc.parallelutils.modules.paralleltowns.commands.ParallelTownGUI;
import parallelmc.parallelutils.modules.paralleltowns.commands.TownCommands;
import parallelmc.parallelutils.modules.paralleltowns.events.OnMenuInteract;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class ParallelTowns extends ParallelModule {

    private ParallelUtils puPlugin;

    public ParallelTowns(ParallelClassLoader classLoader, List<String> dependents) {
        super(classLoader, dependents);
    }

    public GUIManager guiManager;

    private TownCommands townCommands;

    private final HashMap<String, Town> towns = new HashMap<>();

    private final HashMap<UUID, TownMember> playersInTown = new HashMap<>();

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
        townCommands.addCommand("create", new ParallelCreateTown());

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

    public void addTown(Player founder, String townName) {
        towns.put(townName, new Town(townName, founder.getUniqueId()));
        addPlayerToTown(founder, new TownMember(townName, TownRank.LEADER, true));
        ParallelUtils.log(Level.INFO, getPlayerTownStatus(founder).getTownRank().toString());
    }

    public Town getTown(String townName) {
        return towns.get(townName);
    }

    public Town getPlayerTown(Player player) {
        return towns.get(playersInTown.get(player.getUniqueId()).getTownName());
    }

    public TownMember getPlayerTownStatus(Player player) {
        return playersInTown.get(player.getUniqueId());
    }

    public boolean isPlayerInTown(Player player) {
        return getPlayerTownStatus(player) != null;
    }

    public void addPlayerToTown(Player player, TownMember townMember) {
        playersInTown.put(player.getUniqueId(), townMember);
    }

    public void deleteTown(String townName) {
        towns.remove(townName);
        // remove all players in the town being deleted
        playersInTown.entrySet().removeIf(x -> x.getValue().getTownName().equals(townName));
    }

    public static ParallelTowns get() { return Instance; }
}
