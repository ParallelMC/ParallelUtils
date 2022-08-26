package parallelmc.parallelutils.modules.parallelchat.emojis;

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
 * Example emoji in config:
 *
 * emoji:
 *      id: ':emoji:'
 *      replacement: '来'
 *
 * In most emote plugins the "emojis" are actually just characters and symbols from other languages
 * It also allows the plugin to combine multiple characters/emotes into one single emote
 *
 * The emoji name and id are separate to, quite literally, allow the name and id to be separate
 * For instance, an emoji could be named sunglasses, but have an id of :glasses:
 */

public class EmojiManager {
    private final HashMap<String, Emoji> emojis = new HashMap<>();
    public EmojiManager() {
        File emoteFile = new File(ParallelChat.get().getPlugin().getDataFolder(), "emojis.yml");
        FileConfiguration emoteConfig = new YamlConfiguration();
        try {
            if (emoteFile.createNewFile()) {
                ParallelUtils.log(Level.WARNING, "emojis.yml does not exist. Creating...");
            }
            emoteConfig.load(emoteFile);
        } catch (IOException e) {
            ParallelUtils.log(Level.SEVERE, "Failed to create or read emojis.yml\n" + e);
            return;
        } catch (Exception e) {
            ParallelUtils.log(Level.SEVERE, "Failed to load emojis.yml\n" + e);
            return;
        }
        for (String key : emoteConfig.getKeys(false)) {
            String id = emoteConfig.getString("id");
            String replacement = emoteConfig.getString("replacement");
            if (id == null || replacement == null) {
                ParallelUtils.log(Level.WARNING, "Invalid format for emoji key " + key + ", skipping!");
                continue;
            }
            emojis.put(id, new Emoji(key, id, replacement));
        }
        ParallelUtils.log(Level.WARNING, "Loaded " + emojis.size() + " emojis.");
    }

    public HashMap<String, Emoji> getEmojis() { return emojis; }

    /**
     * Retrieves an emoji by its identifier (i.e. :emote:)
     * @param id The identifier to search for
     * @return an {@code parallelmc.parallelutils.modules.parallelchat.emojis.Emoji} with the given identifier
     */
    @Nullable
    public Emoji getEmojis(String id) { return emojis.get(id); }
}
