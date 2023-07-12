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
    private final HashMap<UUID, String> selectedJoinLeaveMessages = new HashMap<>();

    public CustomMessageManager() {
        loadJoinLeaveMessages();
        loadSelectedJoinLeaveMessages();
    }

    public boolean loadJoinLeaveMessages() {
        File file = new File(ParallelChat.get().getPlugin().getDataFolder(), "joinleave.yml");
        FileConfiguration config = new YamlConfiguration();
        try {
            if (file.createNewFile()) {
                ParallelUtils.log(Level.WARNING, "joinleave.yml does not exist. Creating...");
            }
            config.load(file);
        } catch (IOException e) {
            ParallelUtils.log(Level.SEVERE, "Failed to create or read joinleave.yml\n" + e);
            return false;
        } catch (Exception e) {
            ParallelUtils.log(Level.SEVERE, "Failed to load joinleave.yml\n" + e);
            return false;
        }

        for (String key : config.getKeys(false)) {
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
            customJoinLeaveMessages.put(key, new JoinLeaveMessage(event, text, rank));
        }
        ParallelUtils.log(Level.WARNING, "Loaded " + customJoinLeaveMessages.size() + " custom join/leave messages.");
        return true;
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
                String name = (String)json.get("message");
                if (customJoinLeaveMessages.get(name) == null) {
                    ParallelUtils.log(Level.WARNING, "Player has a non-existent custom join/leave message selected! Skipping...");
                    continue;
                }
                selectedJoinLeaveMessages.put(UUID.fromString(uuid), name);
            }
            ParallelUtils.log(Level.INFO, "Loaded " + selectedJoinLeaveMessages.size() + " join/leave message selections.");
        } catch (IOException e) {
            ParallelUtils.log(Level.SEVERE, "Failed to load join/leave message selections!\n" + e.getMessage());
        } catch (ParseException e) {
            ParallelUtils.log(Level.SEVERE, "Failed to parse join/leave message selection data!\n" + e.getMessage());
        }
    }


    @SuppressWarnings("unchecked")
    public void saveSelectedJoinLeaveMessages() {
        JSONArray json = new JSONArray();
        for (Map.Entry<UUID, String> e : selectedJoinLeaveMessages.entrySet()) {
            JSONObject entry = new JSONObject();
            entry.put("uuid", e.getKey().toString());
            entry.put("message", e.getValue());
            json.add(entry);
        }
        try {
            Path path = Path.of(ParallelChat.get().getPlugin().getDataFolder().getAbsolutePath() + "/messages.json");
            Files.writeString(path, json.toJSONString());
            ParallelUtils.log(Level.INFO, "Saved " + selectedJoinLeaveMessages.size() + " selected join/leave messages.");
        } catch (IOException e) {
            ParallelUtils.log(Level.SEVERE, "Failed to save custom join/leave selections!\n" + e.getMessage());
        }
    }

    public void selectJoinLeaveMessage(Player player, String name) {
        if (customJoinLeaveMessages.get(name) == null) {
            ParallelUtils.log(Level.SEVERE, "A player tried to select a non-existent join/leave message. This shouldn't happen!");
            return;
        }
        selectedJoinLeaveMessages.put(player.getUniqueId(), name);
    }

    public void disableJoinLeaveMessage(Player player) {
        selectedJoinLeaveMessages.remove(player.getUniqueId());
    }

    public HashMap<String, JoinLeaveMessage> getCustomJoinLeaveMessages() { return customJoinLeaveMessages; }
}
