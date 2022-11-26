package parallelmc.parallelutils.modules.paralleltowns;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelClassLoader;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.paralleltowns.commands.*;
import parallelmc.parallelutils.modules.paralleltowns.events.OnMenuInteract;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ParallelTowns extends ParallelModule {

    private ParallelUtils puPlugin;

    public ParallelTowns(ParallelClassLoader classLoader, List<String> dependents) {
        super(classLoader, dependents);
    }

    public GUIManager guiManager;

    private TownCommands townCommands;

    private final HashMap<String, Town> towns = new HashMap<>();

    private final HashMap<UUID, String> playersInTown = new HashMap<>();

    private final HashMap<UUID, String> pendingInvites = new HashMap<>();

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
        townCommands.addCommand("invite", new ParallelTownInvite());
        townCommands.addCommand("accept", new ParallelTownAcceptInvite());

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
        towns.put(townName, new Town(townName, founder));
        playersInTown.put(founder.getUniqueId(), townName);
    }

    public Town getPlayerTown(Player player) {
        return towns.get(playersInTown.get(player.getUniqueId()));
    }

    public TownMember getPlayerTownStatus(Player player) {
        return towns.get(playersInTown.get(player.getUniqueId())).getMember(player);
    }

    public TownMember getPlayerTownStatus(UUID uuid) {
        return towns.get(playersInTown.get(uuid)).getMember(uuid);
    }

    public boolean isPlayerInTown(Player player) {
        return playersInTown.get(player.getUniqueId()) != null;
    }

    public void addPlayerToTown(Player player, Town town) {
        town.addMember(player.getUniqueId());
        playersInTown.put(player.getUniqueId(), town.getName());
        town.sendMessage(player.getName() + " has joined the town!", NamedTextColor.GREEN);
    }

    public void removePlayerFromTown(UUID player, Town town) {
        town.removeMember(player);
        playersInTown.remove(player);
    }

    public void invitePlayerToTown(Player inviter, Player invitee) {
        Town town = getPlayerTown(inviter);
        this.pendingInvites.put(invitee.getUniqueId(), town.getName());
        ParallelChat.sendParallelMessageTo(invitee, "You have been invited to join the town " + town.getName() + " by " + inviter.getName() + ". Type /town accept to accept!");
        inviter.getServer().getScheduler().runTaskLater(puPlugin, () -> {
            if (hasPendingInvite(invitee)) {
                this.pendingInvites.remove(invitee.getUniqueId());
                ParallelChat.sendParallelMessageTo(invitee, "Town invitation has expired.");
                ParallelChat.sendParallelMessageTo(inviter, "Town invitation has expired.");
            }
        }, 600L);
    }

    public void acceptTownInvite(Player player) {
        addPlayerToTown(player, towns.get(this.pendingInvites.get(player.getUniqueId())));
        this.pendingInvites.remove(player.getUniqueId());
    }

    public boolean hasPendingInvite(Player player) {
        return pendingInvites.get(player.getUniqueId()) != null;
    }

    public void deleteTown(String townName) {
        towns.remove(townName);
        // remove all players in the town being deleted
        playersInTown.entrySet().removeIf(x -> x.getValue().equals(townName));
    }

    public ParallelUtils getPlugin() { return puPlugin; }

    public static ParallelTowns get() { return Instance; }
}
