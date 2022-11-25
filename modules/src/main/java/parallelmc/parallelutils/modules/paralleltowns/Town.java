package parallelmc.parallelutils.modules.paralleltowns;

import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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

    public boolean demoteMember(UUID player) {
        return members.get(player).demote();
    }
}
