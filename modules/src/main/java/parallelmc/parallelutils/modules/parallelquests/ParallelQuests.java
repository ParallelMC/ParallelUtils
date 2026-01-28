package parallelmc.parallelutils.modules.parallelquests;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelClassLoader;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.parallelquests.commands.QuestTracker;
import parallelmc.parallelutils.modules.parallelquests.dialogue.ConversationManager;
import parallelmc.parallelutils.modules.parallelquests.events.OnCommand;
import parallelmc.parallelutils.modules.parallelquests.events.OnJoinLeave;
import parallelmc.parallelutils.modules.parallelquests.quests.Quest;
import parallelmc.parallelutils.modules.parallelquests.quests.TheBakersBeastQuest;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class ParallelQuests extends ParallelModule {
    private ParallelUtils puPlugin;

    public ParallelQuests(ParallelClassLoader classLoader, List<String> dependents) { super(classLoader, dependents); }

    private final HashMap<String, Quest> RegisteredQuests = new HashMap<>();

    private final ConcurrentHashMap<UUID, List<QuestStatus>> PlayerQuestStatuses = new ConcurrentHashMap<>();

    private static final ConversationManager ConversationManager = new ConversationManager();

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
        manager.registerEvents(new OnCommand(), puPlugin);

        puPlugin.getCommand("questtracker").setExecutor(new QuestTracker());

        RegisteredQuests.put("thebakersbeast", new TheBakersBeastQuest());

        for (Quest quest : RegisteredQuests.values()) {
            quest.init();
        }

        ParallelUtils.log(Level.WARNING, "Initialized " + RegisteredQuests.size() + " quests.");

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
                    QuestStage  varchar(128) not null,
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
           PlayerQuestStatuses.keySet().forEach(u -> {
               Player p = Bukkit.getPlayer(u);
               // the quest cache should only contain online players since players are removed when they log off
               // regardless, make sure the players we're saving actually exist
               if (p == null) return;
               savePlayerQuestStatus(p, false);
           });
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

    public static ConversationManager getConversationManager() { return ConversationManager; }

    @Nullable
    public Quest getQuest(String questId) {
        return RegisteredQuests.get(questId);
    }

    public Optional<QuestStatus> getQuestStatus(Player player, String questId) {
        return getAllQuestStatuses(player).stream()
                .filter(x -> x.getQuestId().equals(questId))
                .findFirst();
    }

    /**
     * Returns a list of quests a player currently has active, as well as if they are completed.
     * If a quest ID is not in this list, the player has not accepted it yet.
     * @param player The Player to search
     */
    public List<QuestStatus> getAllQuestStatuses(Player player) {
        return PlayerQuestStatuses.getOrDefault(player.getUniqueId(), new ArrayList<>());
    }

    public boolean hasCompletedQuest(Player player, String questId) {
        Optional<QuestStatus> status = getQuestStatus(player, questId);
        return status.isPresent() && status.get().isCompleted();
    }

    public void startQuest(Player player, String questId, String initialStage) {
        Quest quest = getQuest(questId);
        if (quest == null) {
            ParallelUtils.log(Level.SEVERE, "Tried to start-nonexistent quest: " + questId);
            return;
        }
        player.sendMessage(Component.text("Quest Accepted: " + quest.getQuestName(), NamedTextColor.GOLD).appendNewline());
        player.playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "block.note_block.chime"), Sound.Source.MASTER, 1f, 1f));
        UUID uuid = player.getUniqueId();
        List<QuestStatus> statuses = getAllQuestStatuses(player);
        if (statuses.isEmpty()) {
            statuses.add(new QuestStatus(questId, initialStage));
            PlayerQuestStatuses.put(uuid, statuses);
            return;
        }
        if (statuses.stream().anyMatch(x -> x.getQuestId().equals(questId))) {
            ParallelUtils.log(Level.WARNING, "Tried to start already active quest " + questId + " for UUID " + uuid);
            return;
        }
        statuses.add(new QuestStatus(questId, initialStage));
    }

    public void setQuestStage(Player player, String questId, String newStage) {
        var status = getQuestStatus(player, questId);
        if (status.isEmpty()) {
            ParallelUtils.log(Level.WARNING, "Tried to update status of quest " + questId + " for " + player.getName() + " that has not accepted said quest!");
            return;
        }
        status.get().setQuestStage(newStage);
    }

    /**
     * Asynchronously loads a player's quest data into the cache.
     * If a player's data already exists in the cache, the load will be ignored.
     * @param player The Player to load
     */
    public void loadPlayerQuestStatus(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(puPlugin, () -> {
            List<QuestStatus> result = new ArrayList<>();
            try (Connection conn = puPlugin.getDbConn()) {
                if (conn == null) throw new SQLException("Unable to establish connection!");
                Statement statement = conn.createStatement();
                statement.setQueryTimeout(10);
                ResultSet results = statement.executeQuery("select * from Quests where UUID = '" + player.getUniqueId() + "'");
                while (results.next()) {
                    String questId = results.getString("QuestId");
                    String questStage = results.getString("QuestStage");
                    result.add(new QuestStatus(questId, questStage));
                }
                conn.commit();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (PlayerQuestStatuses.putIfAbsent(player.getUniqueId(), result) != null) {
                ParallelUtils.log(Level.WARNING, "UUID " + player.getUniqueId() + " already has an entry in PlayerQuestStatuses, ignoring!");
            }
        });
    }

    /**
     * Asynchronously saves a player's quest data to the database.
     * @param player The Player to save
     * @param removeFromCache If true, the data will also be removed from the local cache
     */
    public void savePlayerQuestStatus(Player player, boolean removeFromCache) {
        Bukkit.getScheduler().runTaskAsynchronously(puPlugin, () -> {
            List<QuestStatus> status = getAllQuestStatuses(player);
            try (Connection conn = puPlugin.getDbConn()) {
                if (conn == null) throw new SQLException("Unable to establish connection!");
                Statement statement = conn.createStatement();
                statement.setQueryTimeout(10);
                statement.execute("delete from Quests where UUID = '" + player.getUniqueId() + "'");
                PreparedStatement prepared = conn.prepareStatement("insert into Quests (UUID, QuestId, QuestStage) values (?, ?, ?)");
                prepared.setQueryTimeout(30);
                status.forEach(s -> {
                    try {
                        prepared.setString(1, player.getUniqueId().toString());
                        prepared.setString(2, s.getQuestId());
                        prepared.setString(3, s.getQuestStage());
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
            } finally {
                if (removeFromCache)
                    PlayerQuestStatuses.remove(player.getUniqueId());
            }
        });
    }
}
