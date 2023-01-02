package parallelmc.parallelutils.modules.parallelchat;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
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
import parallelmc.parallelutils.ParallelClassLoader;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.parallelchat.chatrooms.ChatRoomManager;
import parallelmc.parallelutils.modules.parallelchat.commands.*;
import parallelmc.parallelutils.modules.parallelchat.commands.chatrooms.*;
import parallelmc.parallelutils.modules.parallelchat.emojis.EmojiManager;
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
import java.util.logging.Level;

public class ParallelChat extends ParallelModule {

    // possibly put ParallelChat settings somewhere else
    public static HashMap<Player, Component> dndPlayers = new HashMap<>();

    public HashMap<UUID, UUID> playerLastMessaged = new HashMap<>();

    private final HashMap<String, String> groupFormats = new HashMap<>();
    private boolean isUsingDefault = false;

    private static final HashSet<UUID> playersInStaffChat = new HashSet<>();
    public static final BossBar staffChatBar = BossBar.bossBar(Component.text("Staff Chat", NamedTextColor.AQUA), 1, BossBar.Color.PURPLE, BossBar.Overlay.PROGRESS);

    private static final HashSet<UUID> playersInTeamChat = new HashSet<>();
    public static final BossBar teamChatBar = BossBar.bossBar(Component.text("Team Chat", NamedTextColor.YELLOW), 1, BossBar.Color.PURPLE, BossBar.Overlay.PROGRESS);

    public static final HashSet<UUID> playersInLoreChat = new HashSet<>();
    public static final BossBar loreChatBar = BossBar.bossBar(Component.text("Lore Chat", NamedTextColor.GREEN), 1, BossBar.Color.PURPLE, BossBar.Overlay.PROGRESS);

    private final FileConfiguration bannedWordsConfig = new YamlConfiguration();
    public List<String> bannedWords = new ArrayList<>();

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

    public ChatRoomManager chatRoomManager;

    public EmojiManager emojiManager;

    private final Random rand = new Random();

    private ParallelUtils puPlugin;

    private ChatroomCommands chatroomCommands;

    private static ParallelChat Instance;

    public ParallelChat(ParallelClassLoader classLoader, List<String> dependents) {
        super(classLoader, dependents);
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

        if (plugin == null) {
            ParallelUtils.log(Level.SEVERE, "Unable to enable ParallelChat. Plugin " + Constants.PLUGIN_NAME
                    + " does not exist!");
            return;
        }

        this.puPlugin = (ParallelUtils) plugin;

        if (!puPlugin.registerModule(this)) {
            ParallelUtils.log(Level.SEVERE, "Unable to register module ParallelChat! " +
                    "Module may already be registered. Quitting...");
            return;
        }


        // makes things safer and easier in other events
        // plus it saves us a .getModule() every time we need this class
        // if there is a better way of exposing this class
        // feel free to change it
        Instance = this;

        try (Connection conn = puPlugin.getDbConn()) {
            if (conn == null) throw new SQLException("Unable to establish connection!");
            Statement statement = conn.createStatement();
            statement.setQueryTimeout(15);
            statement.execute("""
                    create table if not exists SocialSpy
                    (
                        UUID        varchar(36) not null,
                        SocSpy      tinyint     not null,
                        CmdSpy      tinyint     not null,
                        ChatRoomSpy tinyint     not null
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
                boolean chatRoomSpy = results.getBoolean("ChatRoomSpy");
                socialSpyUsers.put(uuid, new SocialSpyOptions(socialSpy, cmdSpy, chatRoomSpy));
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
            ParallelUtils.log(Level.SEVERE, "Failed to load banned words! Does the file not exist?");
        }

        this.bannedWords = bannedWordsConfig.getStringList("Banned-Words");
        ParallelUtils.log(Level.INFO, "ParallelChat: Loaded " + bannedWords.size() + " banned words.");

        ConfigurationSection groups = puPlugin.getConfig().getConfigurationSection("group-formats");
        if (groups == null) {
            // allow default fallback if configuration is incorrect/missing
            ParallelUtils.log(Level.WARNING, "ParallelChat: No group formats found! Using default value.");
            isUsingDefault = true;
        }
        else {
            groups.getValues(false).forEach((g, f) ->
            {
                String format = f.toString();
                groupFormats.put(g, format);
                ParallelUtils.log(Level.INFO, "ParallelChat: Loaded chat formatting for group " + g);
            });
        }


        this.capsEnabled = puPlugin.getConfig().getBoolean("anti-caps.enabled", false);
        this.capsMinMsgLength = puPlugin.getConfig().getInt("anti-caps.min-message-length", -1);
        this.capsPercentage = puPlugin.getConfig().getInt("anti-caps.match-percent", -1);
        if (capsEnabled) {
            ParallelUtils.log(Level.INFO, "ParallelChat: Enabling Anti-Caps. (Msg Length: " + capsMinMsgLength + ", Match %: " + capsPercentage + ")");
        }

        // combine broadcast options into one string
        this.broadcastMsg = puPlugin.getConfig().getString("announcements.broadcast.prefix") + puPlugin.getConfig().getString("announcements.broadcast.chat-color");
        this.announceMsg = String.join("\n", puPlugin.getConfig().getStringList("announcements.announce.message"));

        List<String> messages = puPlugin.getConfig().getStringList("auto-broadcast.messages");
        long interval = puPlugin.getConfig().getLong("auto-broadcast.interval");
        String autoPrefix = puPlugin.getConfig().getString("auto-broadcast.prefix");
        for (String s : messages) {
            // pre-parse auto broadcast since there are no placeholders
            this.autoMessages.add(MiniMessage.miniMessage().deserialize("\n" + autoPrefix + s + "\n"));
        }
        // Auto-Message
        puPlugin.getServer().getScheduler().scheduleSyncRepeatingTask(puPlugin, () -> {
            Component msg = autoMessages.get(rand.nextInt(autoMessages.size()));
            for (Player p : puPlugin.getServer().getOnlinePlayers()) {
                p.sendMessage(msg);
            }
        }, 0L, interval);


        try {
            this.chatLogWriter = Files.newBufferedWriter(Path.of(puPlugin.getDataFolder().getAbsolutePath() + "/chat_log.txt"), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            this.cmdLogWriter = Files.newBufferedWriter(Path.of(puPlugin.getDataFolder().getAbsolutePath() + "/command_log.txt"), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        }
        catch (IOException e) {
            ParallelUtils.log(Level.SEVERE, "Failed to open writer to loggers!");
        }

        this.chatRoomManager = new ChatRoomManager(Path.of(puPlugin.getDataFolder().getAbsolutePath() + "/chatrooms.json"));

        this.emojiManager = new EmojiManager();

        manager.registerEvents(new OnBookEdit(), puPlugin);
        manager.registerEvents(new OnChatMessage(), puPlugin);
        manager.registerEvents(new OnJoinLeave(puPlugin), puPlugin);
        manager.registerEvents(new OnSignTextSet(), puPlugin);
        manager.registerEvents(new OnCommand(), puPlugin);
        puPlugin.getCommand("fakejoin").setExecutor(new ParallelFakeJoin());
        puPlugin.getCommand("fakeleave").setExecutor(new ParallelFakeLeave());
        puPlugin.getCommand("msg").setExecutor(new ParallelMessage());
        puPlugin.getCommand("r").setExecutor(new ParallelReply());
        puPlugin.getCommand("sc").setExecutor(new ParallelStaffChat());
        puPlugin.getCommand("tc").setExecutor(new ParallelTeamChat());
        puPlugin.getCommand("lc").setExecutor(new ParallelLoreChat());
        puPlugin.getCommand("broadcast").setExecutor(new ParallelBroadcast());
        puPlugin.getCommand("announce").setExecutor(new ParallelAnnounce());
        puPlugin.getCommand("clearchat").setExecutor(new ParallelClearChat());
        puPlugin.getCommand("socialspy").setExecutor(new ParallelSocialSpy());
        puPlugin.getCommand("commandspy").setExecutor(new ParallelCommandSpy());
        puPlugin.getCommand("chatroomspy").setExecutor(new ParallelChatRoomSpy());
        puPlugin.getCommand("mutechat").setExecutor(new ParallelMuteChat());
        puPlugin.getCommand("colors").setExecutor(new ParallelColors());
        puPlugin.getCommand("formats").setExecutor(new ParallelFormats());
        puPlugin.getCommand("dnd").setExecutor(new ParallelDoNotDisturb());
        puPlugin.getCommand("reloademojis").setExecutor(new ParallelReloadEmojis());
        puPlugin.getCommand("banword").setExecutor(new ParallelBanWord());
        puPlugin.getCommand("allowword").setExecutor(new ParallelAllowWord());

        this.chatroomCommands = new ChatroomCommands();
        puPlugin.getCommand("chatroom").setExecutor(chatroomCommands);
        addChatRoomCommand("create", new ParallelCreateChatroom());
        addChatRoomCommand("leave", new ParallelLeaveChatroom());
        addChatRoomCommand("join", new ParallelJoinChatroom());
        addChatRoomCommand("promote", new ParallelPromoteMember());
        addChatRoomCommand("demote", new ParallelDemoteMember());
        addChatRoomCommand("members", new ParallelListMembers());
        addChatRoomCommand("kick", new ParallelKickMember());
        addChatRoomCommand("list", new ParallelListChatrooms());
        addChatRoomCommand("invite", new ParallelSendInvite());
        addChatRoomCommand("accept", new ParallelAcceptInvite());
        addChatRoomCommand("disband", new ParallelDisbandChatroom());
        addChatRoomCommand("help", new ParallelHelpChatrooms());
        addChatRoomCommand("msg", new ParallelMsgChatroom());
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
            ParallelUtils.log(Level.SEVERE, "Failed to close chat log writer!");
        }

        // save spy data across shutdowns
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

        // save chatrooms
        chatRoomManager.saveChatroomsToFile();

        // save banned words list in case any words were added or removed
        bannedWordsConfig.set("Banned-Words", bannedWords);
        try {
            bannedWordsConfig.save(new File(puPlugin.getDataFolder(), "bannedwords.yml"));
        } catch (IOException e) {
            ParallelUtils.log(Level.SEVERE, "Failed to save banned words to file!");
            e.printStackTrace();
        }
    }

    @Override
    public void onUnload() {

    }

    @Override
    public @NotNull String getName() {
        return "ParallelChat";
    }

    /**
     * Sends a chat message to a player with the ParallelUtils prefix
     * @param player The player to send the message to
     * @param message The message to send
     */
    public static void sendParallelMessageTo(Player player, String message) {
        Component text = MiniMessage.miniMessage().deserialize("<dark_aqua>[<white><bold>P<reset><dark_aqua>] <green>" + message);
        player.sendMessage(text);
    }

    /**
     * Sends a chat component to a player with the ParallelUtils prefix.
     * Note that with this function the chat color is dependent on the message parameter
     * @param player The player to send the message to
     * @param message The component to send
     */
    public static void sendParallelMessageTo(Player player, Component message) {
        Component text = MiniMessage.miniMessage().deserialize("<dark_aqua>[<white><bold>P<reset><dark_aqua>] ").append(message);
        player.sendMessage(text);
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
        Component text = MiniMessage.miniMessage().deserialize("<yellow>[<aqua>Staff-Chat<yellow>] <green>" + sender.getName() + " <gray>> ").append(message.color(NamedTextColor.AQUA));
        // i know this is ugly
        // possible todo: dynamically keep track of staff in a list
        for (Player p : sender.getServer().getOnlinePlayers()) {
            if (p.hasPermission("parallelutils.staffchat")) {
                p.sendMessage(text);
            }
        }
        ParallelUtils.log(Level.INFO, LegacyComponentSerializer.legacyAmpersand().serialize(text));
    }

    /**
     * Sends a message into the team chat
     * @param sender The CommandSender who sent the messsage
     * @param message The message Component
     */
    public static void sendMessageToTeamChat(CommandSender sender, Component message) {
        Component text = MiniMessage.miniMessage().deserialize("<gold>[<yellow>Team-Chat<gold>] <green>" + sender.getName() + " <gray>> ").append(message.color(NamedTextColor.YELLOW));
        // i know this is ugly
        // possible todo: dynamically keep track of team in a list
        for (Player p : sender.getServer().getOnlinePlayers()) {
            if (p.hasPermission("parallelutils.teamchat")) {
                p.sendMessage(text);
            }
        }
        ParallelUtils.log(Level.INFO, LegacyComponentSerializer.legacyAmpersand().serialize(text));
    }

    /**
     * Sends a message into the lore chat
     * @param sender The CommandSender who sent the messsage
     * @param message The message Component
     */
    public static void sendMessageToLoreChat(CommandSender sender, Component message) {
        Component text = MiniMessage.miniMessage().deserialize("<red>[<green>Lore-Chat<red>] <green>" + sender.getName() + " <gray>> ").append(message.color(NamedTextColor.GREEN));
        // i know this is ugly
        // possible todo: dynamically keep track of lore staff in a list
        for (Player p : sender.getServer().getOnlinePlayers()) {
            if (p.hasPermission("parallelutils.lorechat")) {
                p.sendMessage(text);
            }
        }
        ParallelUtils.log(Level.INFO, LegacyComponentSerializer.legacyAmpersand().serialize(text));
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

        TagResolver placeholders = TagResolver.resolver(
                Placeholder.component("displayname", displayName.hoverEvent(Component.text(
                        PlaceholderAPI.setPlaceholders(source, "%pronouns_pronouns%")).asHoverEvent())),
                Placeholder.component("tag", getTagForPlayer(source)),
                Placeholder.component("donorrank", getDonorRankForPlayer(source)),
                Placeholder.component("message", message)
        );

        if (isUsingDefault) {
            // if default is enabled for whatever reason mimic the default rank
            Component result = MiniMessage.builder().build().deserialize("<tag><gray><displayname><donorrank> > <reset><message>", placeholders);
            return result;
        }
        else {
            String group = this.getGroupForPlayer(source);
            String format = ParallelChat.get().groupFormats.get(group);
            if (format == null) {
                ParallelUtils.log(Level.SEVERE, "Error while formatting group! Unknown group name " + group);
                return Component.empty();
            }
            else {
                Component result = MiniMessage.builder().build().deserialize(format, placeholders);
                return result;
            }
        }
    }

    private Component getTagForPlayer(Player player) {
        String formatted = PlaceholderAPI.setPlaceholders(player, "%deluxetags_tag%").replaceAll("§", "&");
        return LegacyComponentSerializer.legacyAmpersand().deserialize(formatted);
    }

    private Component getDonorRankForPlayer(Player player) {
        String formatted = PlaceholderAPI.setPlaceholders(player, "%luckperms_suffix_element_highest_on_track_donortrack%").replaceAll("§", "&");
        return LegacyComponentSerializer.legacyAmpersand().deserialize(formatted);
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

    public void addToLoreChat(Player player) {
        playersInLoreChat.add(player.getUniqueId());
        player.showBossBar(loreChatBar);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
    }

    public void removeFromLoreChat(Player player) {
        if (playersInLoreChat.contains(player.getUniqueId())) {
            playersInLoreChat.remove(player.getUniqueId());
            player.hideBossBar(loreChatBar);
        }
    }


    /**
     * Wrapper for {@code parallelmc.parallelutils.modules.parallelchat.commands.chatrooms.ChatroomCommand.addCommand}
     * Adds a new command to the commandmap
     * @param name The name of the command
     * @param command The command to be run when the name is called
     * @return Returns true when the command was added successfully, false if the command already exists.
     */
    public boolean addChatRoomCommand(String name, ChatroomCommand command) { return chatroomCommands.addCommand(name, command); }

    /**
     * Wrapper for {@code parallelmc.parallelutils.modules.parallelchat.commands.chatrooms.ChatroomCommands#getCommands()}
     * @return A deep copy of the command map
     */
    public Map<String, ChatroomCommand> getChatroomCommands() {
        return chatroomCommands.getCommands();
    }

    public HashSet<UUID> getStaffChat() {
        return playersInStaffChat;
    }

    public HashSet<UUID> getTeamChat() {
        return playersInTeamChat;
    }

    public HashSet<UUID> getLoreChat() { return playersInLoreChat; }

    public ParallelUtils getPlugin() { return puPlugin; }

}
