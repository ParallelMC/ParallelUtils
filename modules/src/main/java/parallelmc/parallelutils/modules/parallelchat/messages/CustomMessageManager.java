package parallelmc.parallelutils.modules.parallelchat.messages;

/*
 * Example join/leave message in config:
 *
 * message:
 *      event: 'join'
 *      text: 'PLAYER hopped into the server!'
 *      rank: 'gold'
 *
 * Event is either join or leave
 *
 * PLAYER will be replaced with the player's username
 *
 * Rank is the rank required to select the custom message
 */

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class CustomMessageManager {
    private final HashMap<String, JoinLeaveMessage> customJoinLeaveMessages = new HashMap<>();
    private final HashMap<UUID, CustomMessageSelection> selectedCustomMessages = new HashMap<>();
    public CustomMessageManager() {
        loadJoinLeaveMessages();
        loadSelectedJoinLeaveMessages();
    }

    public void loadJoinLeaveMessages() {
        File file = new File(ParallelChat.get().getPlugin().getDataFolder(), "joinleave.yml");
        FileConfiguration config = new YamlConfiguration();
        try {
            if (file.createNewFile()) {
                ParallelUtils.log(Level.WARNING, "joinleave.yml does not exist. Creating...");
            }
            config.load(file);
        } catch (IOException e) {
            ParallelUtils.log(Level.SEVERE, "Failed to create or read joinleave.yml\n" + e);
            return;
        } catch (Exception e) {
            ParallelUtils.log(Level.SEVERE, "Failed to load joinleave.yml\n" + e);
            return;
        }

        for (String key : config.getKeys(false)) {
            String name = config.getString(key + ".name");
            if (name == null) {
                ParallelUtils.log(Level.WARNING, "Invalid or missing value for 'name' in join/leave message " + key + ", skipping!");
                continue;
            }
            String event = config.getString(key + ".event");
            if (event == null || (!event.equalsIgnoreCase("join") && !event.equalsIgnoreCase("leave"))) {
                ParallelUtils.log(Level.WARNING, "Invalid or missing value for 'event' in join/leave message " + key + ", skipping!");
                continue;
            }
            String text = config.getString(key + ".text");
            String rank = config.getString(key + ".rank");
            if (text == null || rank == null) {
                ParallelUtils.log(Level.WARNING, "Invalid or missing value(s) in join/leave message " + key + ", skipping!");
                continue;
            }
            customJoinLeaveMessages.put(key, new JoinLeaveMessage(name, event, text, rank));
        }
        ParallelUtils.log(Level.WARNING, "Loaded " + customJoinLeaveMessages.size() + " custom join/leave messages.");
    }

    private void loadSelectedJoinLeaveMessages() {
        Path path = Path.of(ParallelChat.get().getPlugin().getDataFolder().getAbsolutePath() + "/messages.json");
        if (!path.toFile().exists()) {
            ParallelUtils.log(Level.WARNING, "messages.json does not exist, skipping loading.");
            return;
        }
        try {
            String data = Files.readString(path);
            JSONParser parser = new JSONParser();
            JSONArray arr = (JSONArray)parser.parse(data);
            for (Object o : arr) {
                JSONObject json = (JSONObject)o;
                String uuid = (String)json.get("uuid");
                Object join = json.get("join");
                Object leave = json.get("leave");
                CustomMessageSelection sel = new CustomMessageSelection(join == null ? null : (String)join, leave == null ? null : (String)leave);
                selectedCustomMessages.put(UUID.fromString(uuid), sel);
            }
            ParallelUtils.log(Level.INFO, "Loaded " + selectedCustomMessages.size() + " custom message selections.");
        } catch (IOException e) {
            ParallelUtils.log(Level.SEVERE, "Failed to load join/leave message selections!\n" + e.getMessage());
        } catch (ParseException e) {
            ParallelUtils.log(Level.SEVERE, "Failed to parse join/leave message selection data!\n" + e.getMessage());
        }
    }


    @SuppressWarnings("unchecked")
    public void saveSelectedCustomMessages() {
        JSONArray json = new JSONArray();
        for (Map.Entry<UUID, CustomMessageSelection> e : selectedCustomMessages.entrySet()) {
            JSONObject entry = new JSONObject();
            CustomMessageSelection c = e.getValue();
            entry.put("uuid", e.getKey().toString());
            entry.put("join", c.getJoinMessage());
            entry.put("leave", c.getLeaveMessage());
            json.add(entry);
        }
        try {
            Path path = Path.of(ParallelChat.get().getPlugin().getDataFolder().getAbsolutePath() + "/messages.json");
            Files.writeString(path, json.toJSONString());
            ParallelUtils.log(Level.INFO, "Saved " + selectedCustomMessages.size() + " selected custom messages.");
        } catch (IOException e) {
            ParallelUtils.log(Level.SEVERE, "Failed to save custom message selections!\n" + e.getMessage());
        }
    }

    public String getRequiredRankForMessage(String message) {
        return customJoinLeaveMessages.get(message).requiredRank();
    }

    public void selectJoinMessage(Player player, String name) {
        if (customJoinLeaveMessages.get(name) == null) {
            ParallelUtils.log(Level.SEVERE, "A player tried to select a non-existent join message. This shouldn't happen!");
            return;
        }
        UUID uuid = player.getUniqueId();
        CustomMessageSelection sel = selectedCustomMessages.get(uuid);
        if (sel == null) {
            selectedCustomMessages.put(uuid, new CustomMessageSelection(name, null));
        }
        else {
            sel.setJoinMessage(name);
        }
    }

    public void selectLeaveMessage(Player player, String name) {
        if (customJoinLeaveMessages.get(name) == null) {
            ParallelUtils.log(Level.SEVERE, "A player tried to select a non-existent leave message. This shouldn't happen!");
            return;
        }
        UUID uuid = player.getUniqueId();
        CustomMessageSelection sel = selectedCustomMessages.get(uuid);
        if (sel == null) {
            selectedCustomMessages.put(uuid, new CustomMessageSelection(null, name));
        }
        else {
            sel.setLeaveMessage(name);
        }
    }

    public void disableJoinMessage(Player player) {
        UUID uuid = player.getUniqueId();
        CustomMessageSelection sel = selectedCustomMessages.get(uuid);
        if (sel == null)
            return;
        sel.setJoinMessage(null);
        if (sel.getJoinMessage() == null && sel.getLeaveMessage() == null)
            selectedCustomMessages.remove(uuid);
    }

    public void disableLeaveMessage(Player player) {
        UUID uuid = player.getUniqueId();
        CustomMessageSelection sel = selectedCustomMessages.get(uuid);
        if (sel == null)
            return;
        sel.setLeaveMessage(null);
        if (sel.getJoinMessage() == null && sel.getLeaveMessage() == null)
            selectedCustomMessages.remove(uuid);
    }

    @Nullable
    public String getJoinMessageForPlayer(Player player) {
        CustomMessageSelection sel = selectedCustomMessages.get(player.getUniqueId());
        if (sel == null)
            return null;
        JoinLeaveMessage msg = customJoinLeaveMessages.get(sel.getJoinMessage());
        if (msg == null) {
            return null;
        }
        return msg.text().replace("PLAYER", player.getName());
    }

    @Nullable
    public String getLeaveMessageForPlayer(Player player) {
        CustomMessageSelection sel = selectedCustomMessages.get(player.getUniqueId());
        if (sel == null)
            return null;
        JoinLeaveMessage msg = customJoinLeaveMessages.get(sel.getLeaveMessage());
        if (msg == null) {
            return null;
        }
        return msg.text().replace("PLAYER", player.getName());
    }

    public HashMap<String, JoinLeaveMessage> getCustomJoinLeaveMessages() { return customJoinLeaveMessages; }
}
