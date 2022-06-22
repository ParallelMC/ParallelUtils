package parallelmc.parallelutils.modules.parallelchat;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Parallelutils;

public class DNDExpansion extends PlaceholderExpansion {
    private final Parallelutils plugin;

    public DNDExpansion(Parallelutils plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "ParallelUtils";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Parallel";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params.equals("dnd")) {
            Player p = player.getPlayer();
            if (p != null) {
                if (ParallelChat.dndPlayers.containsKey(p)) {
                    return "§c§lDND";
                }
                else return "";
            }
        }
        return null;
    }
}
