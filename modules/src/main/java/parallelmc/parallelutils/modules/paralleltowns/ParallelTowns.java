package parallelmc.parallelutils.modules.paralleltowns;

import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelClassLoader;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.paralleltowns.commands.*;
import parallelmc.parallelutils.modules.paralleltowns.events.OnMenuInteract;

import javax.json.Json;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
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

    private static Path jsonPath;

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

        jsonPath = Path.of(puPlugin.getDataFolder().getAbsolutePath() + "/towns.json");

        loadTownsFromFile();

        Instance = this;
    }

    @Override
    public void onDisable() {
        saveTownsToFile();
    }

    @Override
    public void onUnload() {}

    @Override
    public @NotNull String getName() {
        return "ParallelTowns";
    }

    public boolean doesTownExist(String townName) {
        return towns.get(townName) != null;
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

    public void loadTownsFromFile() {
        if (!jsonPath.toFile().exists()) {
            ParallelUtils.log(Level.WARNING, "Towns JSON file does not exist, skipping loading.");
            return;
        }
        String data;
        try {
            data = Files.readString(jsonPath);
            JSONParser parser = new JSONParser();
            JSONArray arr = (JSONArray)parser.parse(data);
            for (Object o : arr) {
                JSONObject json = (JSONObject)o;
                HashMap<UUID, TownMember> members = new HashMap<>();
                ArrayList<Component> charterPages = new ArrayList<>();
                String name = (String)json.get("name");
                long founded = (long)json.get("founded");
                for (Object p : (JSONArray)json.get("charter")) {
                    charterPages.add(LegacyComponentSerializer.legacyAmpersand().deserialize((String)p));
                }
                Book book = Book.book(Component.text("Town Charter"), Component.text("Parallel"), charterPages);
                for (Object m : (JSONArray)json.get("members")) {
                    JSONObject member = (JSONObject)m;
                    UUID uuid = UUID.fromString((String)member.get("uuid"));
                    short rank = (short)((long)member.get("rank"));
                    boolean isFounder = (boolean)member.get("founder");
                    members.put(uuid, new TownMember(rank, isFounder));
                    playersInTown.put(uuid, name);
                }
                towns.put(name, new Town(name, founded, members, book));
            }
            ParallelUtils.log(Level.INFO, "Loaded " + towns.size() + " existing towns.");
        } catch (IOException e) {
            ParallelUtils.log(Level.SEVERE, "Failed to load towns!\n" + e.getMessage());
        } catch (ParseException e) {
            ParallelUtils.log(Level.SEVERE, "Failed to parse town data!\n" + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void saveTownsToFile() {
        JSONArray json = new JSONArray();
        for (Map.Entry<String, Town> e : towns.entrySet()) {
            Town t = e.getValue();
            JSONObject entry = new JSONObject();
            entry.put("name", t.getName());
            entry.put("founded", t.getUnformattedFoundedDate());
            JSONArray charter = new JSONArray();
            t.getCharter().pages().forEach(x -> {
                charter.add(LegacyComponentSerializer.legacyAmpersand().serialize(x));
            });
            entry.put("charter", charter);
            JSONArray members = new JSONArray();
            t.getMembers().forEach((u, m) -> {
                JSONObject member = new JSONObject();
                member.put("uuid", u.toString());
                member.put("rank", m.getTownRank());
                member.put("founder", m.getIsFounder());
                members.add(member);
            });
            entry.put("members", members);
            json.add(entry);
        }
        try {
            Files.writeString(jsonPath, json.toJSONString());
            ParallelUtils.log(Level.INFO, "Saved " + towns.size() + " towns.");
        } catch (IOException e) {
            ParallelUtils.log(Level.SEVERE, "Failed to save towns!\n" + e.getMessage());
        }
    }

    public ParallelUtils getPlugin() { return puPlugin; }

    public static ParallelTowns get() { return Instance; }
}
