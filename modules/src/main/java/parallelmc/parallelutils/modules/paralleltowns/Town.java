package parallelmc.parallelutils.modules.paralleltowns;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.UUID;

public class Town {
    // the name of the town
    private final String Name;
    // unix timestamp of founding date
    private final long dateFounded;
    // the town's members
    // TODO: support "ranks" (leader, official, etc.), maybe via fancy enum flag system
    private final HashSet<UUID> members = new HashSet<>();

    public Town(String name, UUID founder) {
        Name = name;
        dateFounded = System.currentTimeMillis();
        members.add(founder);
    }

    public String getName() { return Name; }

    public String getFoundedDate() {
        OffsetDateTime time = OffsetDateTime.ofInstant(Instant.ofEpochMilli(dateFounded), ZoneId.of("America/New_York"));
        // returns as, for example, 3:00 PM, December 2, 2022
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a, MMM d, y");
        return time.format(formatter);
    }

    public HashSet<UUID> getMembers() { return members; }

    public void addMember(UUID player) {
        members.add(player);
    }

    public void removeMember(UUID player) {
        members.remove(player);
    }
}
