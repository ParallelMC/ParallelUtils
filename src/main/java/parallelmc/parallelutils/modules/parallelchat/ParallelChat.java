package parallelmc.parallelutils.modules.parallelchat;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.parallelchat.commands.*;
import parallelmc.parallelutils.modules.parallelchat.events.*;
import parallelmc.parallelutils.modules.parallelchat.events.OnChatMessage;
import parallelmc.parallelutils.modules.parallelchat.commands.ParallelFakeJoin;
import parallelmc.parallelutils.modules.parallelchat.commands.ParallelFakeLeave;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParallelChat implements ParallelModule {

    // possibly put ParallelChat settings somewhere else
    public static HashMap<Player, Component> dndPlayers = new HashMap<>();

    public HashMap<UUID, UUID> playerLastMessaged = new HashMap<>();

    private final HashMap<String, String> groupFormats = new HashMap<>();
    private boolean isUsingDefault = false;

    private static final HashSet<UUID> playersInStaffChat = new HashSet<>();
    public static final BossBar staffChatBar = BossBar.bossBar(Component.text("Staff Chat", NamedTextColor.AQUA), 1, BossBar.Color.PURPLE, BossBar.Overlay.PROGRESS);

    private static final HashSet<UUID> playersInTeamChat = new HashSet<>();
    public static final BossBar teamChatBar = BossBar.bossBar(Component.text("Team Chat", NamedTextColor.YELLOW), 1, BossBar.Color.PURPLE, BossBar.Overlay.PROGRESS);

    private final FileConfiguration bannedWordsConfig = new YamlConfiguration();
    public List<String> bannedWords = new ArrayList<>();
    public List<String> allowedWords = new ArrayList<>();

    public List<Component> autoMessages = new ArrayList<>();

    public boolean capsEnabled = false;
    public int capsMinMsgLength = -1;
    public int capsPercentage = -1;

    public String broadcastMsg = null;
    public String announceMsg = null;

    public HashMap<UUID, SocialSpyOptions> socialSpyUsers = new HashMap<>();

    public boolean isChatDisabled = false;

    public BufferedWriter chatLogWriter;
    public BufferedWriter cmdLogWriter;

    private final Random rand = new Random();

    private Parallelutils puPlugin;

    private static ParallelChat Instance;

    @Override
    public void onEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

        if (plugin == null) {
            Parallelutils.log(Level.SEVERE, "Unable to enable ParallelChat. Plugin " + Constants.PLUGIN_NAME
                    + " does not exist!");
            return;
        }

        this.puPlugin = (Parallelutils) plugin;

        if (!puPlugin.registerModule("ParallelChat", this)) {
            Parallelutils.log(Level.SEVERE, "Unable to register module ParallelChat! " +
                    "Module may already be registered. Quitting...");
            return;
        }

        try (Connection conn = puPlugin.getDbConn()) {
            if (conn == null) throw new SQLException("Unable to establish connection!");
            Statement statement = conn.createStatement();
            statement.setQueryTimeout(15);
            statement.execute("""
                    create table if not exists SocialSpy
                    (
                        UUID        varchar(36) not null,
                        SocSpy      tinyint     not null,
                        CmdSpy      tinyint     not null
                    );""");
            conn.commit();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // load existing player config
        try (Connection conn = puPlugin.getDbConn()) {
            if (conn == null) throw new SQLException("Unable to establish connection!");
            Statement statement = conn.createStatement();
            statement.setQueryTimeout(10);
            ResultSet results = statement.executeQuery("select * from SocialSpy");
            while (results.next()) {
                UUID uuid = UUID.fromString(results.getString("UUID"));
                boolean socialSpy = results.getBoolean("SocSpy");
                boolean cmdSpy = results.getBoolean("CmdSpy");
                socialSpyUsers.put(uuid, new SocialSpyOptions(socialSpy, cmdSpy));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new DNDExpansion(this.puPlugin).register();
        }

        try {
            bannedWordsConfig.load(new File(puPlugin.getDataFolder(), "bannedwords.yml"));
        }
        catch (Exception e) {
            Parallelutils.log(Level.SEVERE, "Failed to load banned words! Does the file not exist?");
        }

        this.bannedWords = bannedWordsConfig.getStringList("Banned-Words");
        this.allowedWords = bannedWordsConfig.getStringList("Whitelisted-Words");
        Parallelutils.log(Level.INFO, "ParallelChat: Loaded " + bannedWords.size() + " banned words.");

        ConfigurationSection groups = puPlugin.getConfig().getConfigurationSection("group-formats");
        if (groups == null) {
            // allow default fallback if configuration is incorrect/missing
            Parallelutils.log(Level.WARNING, "ParallelChat: No group formats found! Using default value.");
            isUsingDefault = true;
        }
        else {
            groups.getValues(false).forEach((g, f) ->
            {
                String format = f.toString();
                groupFormats.put(g, format);
                Parallelutils.log(Level.INFO, "ParallelChat: Loaded chat formatting for group " + g);
            });
        }


        this.capsEnabled = puPlugin.getConfig().getBoolean("anti-caps.enabled", false);
        this.capsMinMsgLength = puPlugin.getConfig().getInt("anti-caps.min-message-length", -1);
        this.capsPercentage = puPlugin.getConfig().getInt("anti-caps.match-percent", -1);
        if (capsEnabled) {
            Parallelutils.log(Level.INFO, "ParallelChat: Enabling Anti-Caps. (Msg Length: " + capsMinMsgLength + ", Match %: " + capsPercentage + ")");
        }

        // combine broadcast options into one string
        this.broadcastMsg = puPlugin.getConfig().getString("announcements.broadcast.prefix") + puPlugin.getConfig().getString("announcements.broadcast.chat-color");
        this.announceMsg = String.join("\n", puPlugin.getConfig().getStringList("announcements.announce.message"));

        List<String> messages = puPlugin.getConfig().getStringList("auto-broadcast.messages");
        long interval = puPlugin.getConfig().getLong("auto-broadcast.interval");
        String autoPrefix = puPlugin.getConfig().getString("auto-broadcast.prefix");
        for (String s : messages) {
            // pre-parse auto broadcast since there are no placeholders
            this.autoMessages.add(MiniMessage.get().parse("\n" + autoPrefix + s + "\n"));
        }
        // Auto-Message
        puPlugin.getServer().getScheduler().scheduleSyncRepeatingTask(puPlugin, () -> {
            Component msg = autoMessages.get(rand.nextInt(autoMessages.size()));
            for (Player p : puPlugin.getServer().getOnlinePlayers()) {
                p.sendMessage(msg);
            }
        }, 0L, interval);


        try {
            this.chatLogWriter = Files.newBufferedWriter(Path.of(puPlugin.getDataFolder().getAbsolutePath() + "/chat_log.txt"), StandardCharsets.UTF_8, StandardOpenOption.WRITE);
            this.cmdLogWriter = Files.newBufferedWriter(Path.of(puPlugin.getDataFolder().getAbsolutePath() + "/command_log.txt"), StandardCharsets.UTF_8, StandardOpenOption.WRITE);
        }
        catch (IOException e) {
            Parallelutils.log(Level.SEVERE, "Failed to open writer to loggers!");
        }
        manager.registerEvents(new OnChatMessage(), puPlugin);
        manager.registerEvents(new OnJoinLeave(), puPlugin);
        manager.registerEvents(new OnSignTextSet(), puPlugin);
        manager.registerEvents(new OnCommand(), puPlugin);
        puPlugin.getCommand("fakejoin").setExecutor(new ParallelFakeJoin());
        puPlugin.getCommand("fakeleave").setExecutor(new ParallelFakeLeave());
        puPlugin.getCommand("msg").setExecutor(new ParallelMessage());
        puPlugin.getCommand("r").setExecutor(new ParallelReply());
        puPlugin.getCommand("sc").setExecutor(new ParallelStaffChat());
        puPlugin.getCommand("tc").setExecutor(new ParallelTeamChat());
        puPlugin.getCommand("broadcast").setExecutor(new ParallelBroadcast());
        puPlugin.getCommand("announce").setExecutor(new ParallelAnnounce());
        puPlugin.getCommand("clearchat").setExecutor(new ParallelClearChat());
        puPlugin.getCommand("socialspy").setExecutor(new ParallelSocialSpy());
        puPlugin.getCommand("commandspy").setExecutor(new ParallelCommandSpy());
        puPlugin.getCommand("mutechat").setExecutor(new ParallelMuteChat());
        puPlugin.getCommand("colors").setExecutor(new ParallelColors());
        puPlugin.getCommand("formats").setExecutor(new ParallelFormats());
        puPlugin.getCommand("dnd").setExecutor(new ParallelDoNotDisturb());

        // makes things safer and easier in other events
        // plus it saves us a .getModule() every time we need this class
        // if there is a better way of exposing this class
        // feel free to change it
        Instance = this;
    }

    public static ParallelChat get() {
        return Instance;
    }

    @Override
    public void onDisable() {
        try {
            if (this.chatLogWriter != null) { // PLEASE do null checks or at least make sure that things can't be null
                this.chatLogWriter.close();
            }
        }
        catch (IOException e) {
            Parallelutils.log(Level.SEVERE, "Failed to close chat log writer!");
        }

        // save socialspy and cmdspy data across shutdowns
        try (Connection conn = puPlugin.getDbConn()) {
            if (conn == null) throw new SQLException("Unable to establish connection!");
            PreparedStatement statement = conn.prepareStatement("INSERT INTO SocialSpy (UUID, SocSpy, CmdSpy) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE SocSpy = ?, CmdSpy = ?");
            statement.setQueryTimeout(30);
            this.socialSpyUsers.forEach((u, o) -> {
                try {
                    statement.setString(1, u.toString());
                    statement.setBoolean(2, o.isSocialSpy());
                    statement.setBoolean(3, o.isCmdSpy());
                    statement.setBoolean(4, o.isSocialSpy());
                    statement.setBoolean(5, o.isCmdSpy());
                    statement.addBatch();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            statement.executeBatch();
            conn.commit();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a chat message to a player with the ParallelUtils prefix
     * @param player The player to send the message to
     * @param message The message to send
     */
    public static void sendParallelMessageTo(Player player, String message) {
        Component msg = Component.text("\n§3[§f§lP§3] §a " + message + "\n");
        player.sendMessage(msg);
    }

    /**
     * Sends a chat message to a player
     * @param player The player to send the message to
     * @param message The message to send
     */
    public static void sendMessageTo(Player player, String message) {
        Component msg = Component.text(message);
        player.sendMessage(msg);
    }

    /**
     * Sends a message into the staff chat
     * @param sender The CommandSender who sent the messsage
     * @param message The message Component
     */
    public static void sendMessageToStaffChat(CommandSender sender, Component message) {
        // TODO: Make this a config option
        Component text = MiniMessage.get().parse("<yellow>[<aqua>Staff-Chat<yellow>] <green>" + sender.getName() + " <gray>> ").append(message.color(NamedTextColor.AQUA));
        // i know this is ugly
        // possible todo: dynamically keep track of staff in a list
        for (Player p : sender.getServer().getOnlinePlayers()) {
            if (p.hasPermission("parallelutils.staffchat")) {
                p.sendMessage(text);
            }
        }
        Parallelutils.log(Level.INFO, LegacyComponentSerializer.legacyAmpersand().serialize(text));
    }

    /**
     * Sends a message into the team chat
     * @param sender The CommandSender who sent the messsage
     * @param message The message Component
     */
    public static void sendMessageToTeamChat(CommandSender sender, Component message) {
        Component text = MiniMessage.get().parse("<gold>[<yellow>Team-Chat<gold>] <green>" + sender.getName() + " <gray>> ").append(message.color(NamedTextColor.YELLOW));
        // i know this is ugly
        // possible todo: dynamically keep track of team in a list
        for (Player p : sender.getServer().getOnlinePlayers()) {
            if (p.hasPermission("parallelutils.teamchat")) {
                p.sendMessage(text);
            }
        }
        Parallelutils.log(Level.INFO, LegacyComponentSerializer.legacyAmpersand().serialize(text));
    }

    /**
     * Converts a list of command arguments into a string
     * Mostly to parse string arguments
     * @param argList The list of arguments to use
     * @return A space-separated string
     */
    public static String getStringArg(String[] argList) {
        StringBuilder sb = new StringBuilder();
        // thanks minecraft commands
        for (String arg : argList) {
            sb.append(arg);
            sb.append(' ');
        }
        return sb.toString();
    }


    /**
     * Formats a message based on the provided group's formatting settings
     * @param source The source Player
     * @param displayName The Player's displayName
     * @param message The chat message
     * @return A Component ready to be sent
     */
    public Component formatForGroup(@NotNull Player source, @NotNull Component displayName, @NotNull Component message) {
        StringBuilder sb = new StringBuilder();
        Function<String, ComponentLike> resolver = (placeholder) -> {
            switch (placeholder.toLowerCase()) {
                case "displayname" -> {
                    return displayName;
                }
                case "tag" -> {
                    String formatted = PlaceholderAPI.setPlaceholders(source, "%deluxetags_tag%");
                    Matcher matcher = Pattern.compile("&#(.{6})").matcher(formatted);
                    while (matcher.find()) {
                        // fix ampersands to be parsable by minimessage
                        matcher.appendReplacement(sb, "<color:#" + matcher.group(1) + ">");
                    }
                    matcher.appendTail(sb);
                    return MiniMessage.get().parse(sb.toString());
                }
                case "message" -> {
                    return message;
                }
                default -> {
                    return null;
                }
            }
        };
        if (isUsingDefault) {
            // if default is enabled for whatever reason mimic the default rank
            Component result = MiniMessage.builder().placeholderResolver(resolver).build().parse("<tag><gray><displayname> > <reset><message>");
            return result;
        }
        else {
            String group = this.getGroupForPlayer(source);
            String format = ParallelChat.get().groupFormats.get(group);
            if (format == null) {
                Parallelutils.log(Level.SEVERE, "Error while formatting group! Unknown group name " + group);
                return Component.empty();
            }
            else {
                Component result = MiniMessage.builder().placeholderResolver(resolver).build().parse(format);
                return result;
            }
        }
    }

    private String getGroupForPlayer(Player player) {
        return LuckPermsProvider.get().getPlayerAdapter(Player.class).getUser(player).getPrimaryGroup();
    }

    public void setChatDisabled(boolean value) {
        this.isChatDisabled = value;
    }

    public void addToStaffChat(Player player) {
        playersInStaffChat.add(player.getUniqueId());
        player.showBossBar(staffChatBar);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
    }

    public void removeFromStaffChat(Player player) {
        if (playersInStaffChat.contains(player.getUniqueId())) {
            playersInStaffChat.remove(player.getUniqueId());
            player.hideBossBar(staffChatBar);
        }
    }

    public void addToTeamChat(Player player) {
        playersInTeamChat.add(player.getUniqueId());
        player.showBossBar(teamChatBar);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
    }

    public void removeFromTeamChat(Player player) {
        if (playersInTeamChat.contains(player.getUniqueId())) {
            playersInTeamChat.remove(player.getUniqueId());
            player.hideBossBar(teamChatBar);
        }
    }

    public HashSet<UUID> getStaffChat() {
        return playersInStaffChat;
    }

    public HashSet<UUID> getTeamChat() {
        return playersInTeamChat;
    }

}
