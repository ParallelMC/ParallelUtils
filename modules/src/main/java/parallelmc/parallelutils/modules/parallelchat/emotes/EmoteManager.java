package parallelmc.parallelutils.modules.parallelchat.emotes;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

/*
 * Example emote in config:
 *
 * emote:
 *      id: ':emote:'
 *      replacement: '来'
 *
 * In most emote plugins the "emotes" are actually just characters and symbols from other languages
 * It also allows the plugin to combine multiple characters/emotes into one single emote
 *
 * The emote name and id are separate to, quite literally, allow the name and id to be separate
 * For instance, an emote could be named sunglasses, but have an id of :glasses:
 */

public class EmoteManager {
    private final HashMap<String, Emote> emotes = new HashMap<>();
    public EmoteManager() {
        File emoteFile = new File(ParallelChat.get().getPlugin().getDataFolder(), "emotes.yml");
        FileConfiguration emoteConfig = new YamlConfiguration();
        try {
            if (emoteFile.createNewFile()) {
                ParallelUtils.log(Level.WARNING, "emotes.yml does not exist. Creating...");
            }
            emoteConfig.load(emoteFile);
        } catch (IOException e) {
            ParallelUtils.log(Level.SEVERE, "Failed to create or read emotes.yml\n" + e);
            return;
        } catch (Exception e) {
            ParallelUtils.log(Level.SEVERE, "Failed to load emotes.yml\n" + e);
            return;
        }
        for (String key : emoteConfig.getKeys(false)) {
            String id = emoteConfig.getString("id");
            String replacement = emoteConfig.getString("replacement");
            if (id == null || replacement == null) {
                ParallelUtils.log(Level.WARNING, "Invalid format for emote key " + key + ", skipping!");
                continue;
            }
            emotes.put(id, new Emote(key, id, replacement));
        }
        ParallelUtils.log(Level.WARNING, "Loaded " + emotes.size() + " emotes.");
    }

    public HashMap<String, Emote> getEmotes() { return emotes; }

    /**
     * Retrieves an emote by its identifier (i.e. :emote:)
     * @param id The identifier to search for
     * @return an {@code parallelmc.parallelutils.modules.parallelchat.emotes.Emote} with the given identifier
     */
    @Nullable
    public Emote getEmote(String id) { return emotes.get(id); }
}
