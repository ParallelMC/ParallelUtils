package parallelmc.parallelutils.modules.paralleltowns;

import com.mojang.brigadier.Command;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.UUID;

public class Town {
    // the name of the town
    private final String Name;
    // unix timestamp of founding date
    private final long dateFounded;
    // the town's members
    private final HashMap<UUID, TownMember> members = new HashMap<>();

    public Town(String name, Player founder) {
        Name = name;
        dateFounded = System.currentTimeMillis();
        members.put(founder.getUniqueId(), new TownMember(TownRank.LEADER, true));
    }

    public String getName() { return Name; }

    public String getFoundedDate() {
        OffsetDateTime time = OffsetDateTime.ofInstant(Instant.ofEpochMilli(dateFounded), ZoneId.of("America/New_York"));
        // returns as, for example, 3:00 PM, December 2, 2022
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a, MMM d, y");
        return time.format(formatter);
    }

    public HashMap<UUID, TownMember> getMembers() { return members; }

    public TownMember getMember(Player player) {
        return members.get(player.getUniqueId());
    }

    public TownMember getMember(UUID player) {
        return members.get(player);
    }

    public void addMember(UUID player) {
        members.put(player, new TownMember(TownRank.MEMBER, false));
    }

    public void removeMember(UUID player) {
        members.remove(player);
    }

    public boolean promoteMember(UUID player) {
        return members.get(player).promote();
    }

    // TODO: disallow demoting a leader if only one exists (i.e. there should be at least one leader at all times)
    public boolean demoteMember(UUID player) {
        return members.get(player).demote();
    }

    public void sendMessage(String message, NamedTextColor color) {
        Component msg = Component.text("[" + Name + "]: ", NamedTextColor.GOLD).append(Component.text(message, color));
        for (UUID uuid : members.keySet()) {
            Player player = ParallelChat.get().getPlugin().getServer().getPlayer(uuid);
            if (player != null)
                player.sendMessage(msg);
        }
    }
}
