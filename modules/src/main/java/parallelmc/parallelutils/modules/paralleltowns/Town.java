package parallelmc.parallelutils.modules.paralleltowns;

import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Town {
    // the name of the town
    private final String Name;
    // unix timestamp of founding date
    private final long dateFounded;
    // the town's members
    private HashMap<UUID, TownMember> members = new HashMap<>();
    // the town charter
    private Book charter;
    // the item that is displayed in the town list
    private DisplayItem displayItem;
    // if the town is open or invite only
    private boolean isOpen;

    public Town(String name, Player founder) {
        Name = name;
        dateFounded = System.currentTimeMillis();
        members.put(founder.getUniqueId(), new TownMember(TownRank.LEADER, true));
        charter = Book.book(Component.text("Town Charter"), Component.text("Parallel"), Component.empty());
        displayItem = new DisplayItem(Material.BOOK);
        isOpen = false;
    }

    /** Constructor used when loading town data from json, do not use! **/
    public Town(String name, long dateFounded, HashMap<UUID, TownMember> members, Book charter, DisplayItem displayItem, boolean open) {
        this.Name = name;
        this.dateFounded = dateFounded;
        this.members = members;
        this.charter = charter;
        this.displayItem = displayItem;
        this.isOpen = open;
    }

    public String getName() { return Name; }

    public long getUnformattedFoundedDate() { return dateFounded; }

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

    public Book getCharter() { return charter; }

    public DisplayItem getDisplayItem() { return displayItem; }

    public boolean isOpen() { return isOpen; }

    public void setCharter(List<Component> pages) {
        charter = Book.book(Component.text("Town Charter"), Component.text("Parallel"), pages);
    }

    public void setDisplayItem(Material material, int modelData) {
        displayItem = new DisplayItem(material, modelData);
    }

    public void setIsOpen(boolean value) {
        isOpen = value;
    }

    public void sendMessage(String message, NamedTextColor color) {
        Component msg = Component.text("[" + Name + "]: ", NamedTextColor.GOLD).append(Component.text(message, color));
        for (UUID uuid : members.keySet()) {
            Player player = ParallelTowns.get().getPlugin().getServer().getPlayer(uuid);
            if (player != null)
                player.sendMessage(msg);
        }
    }
}
