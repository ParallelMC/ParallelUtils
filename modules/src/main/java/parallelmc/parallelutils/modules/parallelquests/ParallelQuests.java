package parallelmc.parallelutils.modules.parallelquests;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelClassLoader;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.parallelquests.commands.ExampleConversation;
import parallelmc.parallelutils.modules.parallelquests.commands.QuestTracker;
import parallelmc.parallelutils.modules.parallelquests.dialogue.Conversation;
import parallelmc.parallelutils.modules.parallelquests.dialogue.Dialogue;
import parallelmc.parallelutils.modules.parallelquests.events.OnJoinLeave;
import parallelmc.parallelutils.modules.parallelquests.events.OnSlotUpdated;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class ParallelQuests extends ParallelModule {
    private ParallelUtils puPlugin;

    public ParallelQuests(ParallelClassLoader classLoader, List<String> dependents) { super(classLoader, dependents); }

    // TODO: populate
    private final HashSet<String> ValidQuests = new HashSet<>();

    private final ConcurrentHashMap<UUID, List<QuestStatus>> PlayerQuestStatuses = new ConcurrentHashMap<>();
    private final HashMap<UUID, Conversation> ActiveConversations = new HashMap<>();

    private static ParallelQuests Instance;

    @Override
    public void onLoad() {}

    @Override
    public void onEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

        if (plugin == null) {
            ParallelUtils.log(Level.SEVERE, "Unable to enable ParallelQuests. Plugin " + Constants.PLUGIN_NAME
                    + " does not exist!");
            return;
        }

        this.puPlugin = (ParallelUtils) plugin;

        if (!puPlugin.registerModule(this)) {
            ParallelUtils.log(Level.SEVERE, "Unable to register module ParallelQuests! " +
                    "Module may already be registered. Quitting...");
            return;
        }

        manager.registerEvents(new OnJoinLeave(), puPlugin);
        manager.registerEvents(new OnSlotUpdated(), puPlugin);

        puPlugin.getCommand("questtracker").setExecutor(new QuestTracker());
        puPlugin.getCommand("exampleconversation").setExecutor(new ExampleConversation());

        try (Connection conn = puPlugin.getDbConn()) {
            if (conn == null) throw new SQLException("Unable to establish connection!");
            Statement statement = conn.createStatement();
            statement.setQueryTimeout(15);
            statement.execute("""
                create table if not exists Quests
                (
                    Id          int          not null auto_increment,
                    UUID        varchar(36)  not null,
                    QuestId     varchar(128) not null,
                    Completed   tinyint      not null,
                    constraint Quests_Id_uindex
                        unique (Id),
                    PRIMARY KEY (Id)
                );
            """);
            conn.commit();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Bukkit.getScheduler().runTaskTimer(puPlugin, () -> {
           PlayerQuestStatuses.keySet().forEach(this::savePlayerQuestStatus);
        }, 6000L, 6000L); // 5 minutes

        Instance = this;
    }

    @Override
    public void onDisable() {}

    @Override
    public void onUnload() {}

    @Override
    public @NotNull String getName() {
        return "ParallelQuests";
    }

    public static ParallelQuests get() {
        return Instance;
    }

    public void startConversation(Player player, Dialogue dialogue) {
        if (ActiveConversations.containsKey(player.getUniqueId())) {
            ParallelUtils.log(Level.WARNING, "Tried to add " + player.getName() + " to a conversation when they are already in one!");
            return;
        }
        Conversation c = new Conversation(dialogue);
        c.enter(player);
        ActiveConversations.put(player.getUniqueId(), c);
    }

    public void endConversation(UUID uuid) {
        ActiveConversations.remove(uuid);
    }

    public @Nullable Conversation getActiveConversation(UUID uuid) {
        return ActiveConversations.getOrDefault(uuid, null);
    }

    /**
     * Returns a list of quests a player currently has active, as well as if they are completed.
     * If a quest ID is not in this list, the player has not accepted it yet.
     * @param uuid The player UUID to search
     */
    public List<QuestStatus> getQuestStatus(UUID uuid) {
        return PlayerQuestStatuses.getOrDefault(uuid, List.of());
    }

    public boolean markQuestCompleted(UUID uuid, String questId) {
        if (!ValidQuests.contains(questId)) {
            ParallelUtils.log(Level.SEVERE, "Invalid Quest ID " + questId + " provided for quest completion!");
            return false;
        }

        Optional<QuestStatus> status = getQuestStatus(uuid).stream().filter(x -> x.getQuestId().equals(questId)).findFirst();
        if (status.isEmpty()) {
            return false;
        }

        status.get().markCompleted();
        return false;
    }

    /**
     * Asynchronously loads a player's quest data into the cache.
     * If a player's data already exists in the cache, the load will be ignored.
     * @param uuid The Player UUID to load
     */
    public void loadPlayerQuestStatus(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(puPlugin, () -> {
            List<QuestStatus> result = new ArrayList<>();
            try (Connection conn = puPlugin.getDbConn()) {
                if (conn == null) throw new SQLException("Unable to establish connection!");
                Statement statement = conn.createStatement();
                statement.setQueryTimeout(10);
                ResultSet results = statement.executeQuery("select * from Quests where UUID = '" + uuid + "'");
                while (results.next()) {
                    String questId = results.getString("QuestId");
                    boolean completed = results.getBoolean("Completed");
                    result.add(new QuestStatus(questId, completed));
                }
                conn.commit();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (PlayerQuestStatuses.putIfAbsent(uuid, result) != null) {
                ParallelUtils.log(Level.WARNING, "UUID " + uuid + " already has an entry in PlayerQuestStatuses, ignoring!");
            }
        });
    }

    /**
     * Asynchronously saves a player's quest data to the database.
     * The player's data WILL be removed from the cache, see savePlayerQuestStatus to avoid this.
     * @param uuid The Player UUID to save
     */
    public void saveAndRemovePlayerQuestStatus(UUID uuid) {
        savePlayerQuestStatus(uuid);
        PlayerQuestStatuses.remove(uuid);
    }

    /**
     * Asynchronously saves a player's quest data to the database.
     * The player's data will NOT be removed from the cache, see saveAndRemovePlayerQuestStatus
     * @param uuid The Player UUID to save
     */
    public void savePlayerQuestStatus(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(puPlugin, () -> {
            List<QuestStatus> status = getQuestStatus(uuid);
            try (Connection conn = puPlugin.getDbConn()) {
                if (conn == null) throw new SQLException("Unable to establish connection!");
                Statement statement = conn.createStatement();
                statement.setQueryTimeout(10);
                statement.execute("delete from Quests where UUID = '" + uuid + "'");
                PreparedStatement prepared = conn.prepareStatement("insert into Quests (UUID, QuestId, Completed) values (?, ?, ?)");
                prepared.setQueryTimeout(30);
                status.forEach(s -> {
                    try {
                        prepared.setString(1, uuid.toString());
                        prepared.setString(2, s.getQuestId());
                        prepared.setBoolean(3, s.isCompleted());
                        prepared.addBatch();
                    }
                    catch (SQLException e){
                        e.printStackTrace();
                    }
                });
                prepared.executeBatch();
                conn.commit();
                statement.close();
                prepared.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
