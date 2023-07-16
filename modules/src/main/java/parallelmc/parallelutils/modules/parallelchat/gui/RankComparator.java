package parallelmc.parallelutils.modules.parallelchat.gui;

import parallelmc.parallelutils.modules.parallelchat.messages.JoinLeaveMessage;

import java.util.Comparator;
import java.util.Map;

/***
 * Helper class for sorting the items by rank in the JoinLeaveSelectInventory
 */
public class RankComparator implements Comparator<Map.Entry<String, JoinLeaveMessage>> {
    @Override
    public int compare(Map.Entry<String, JoinLeaveMessage> entry1, Map.Entry<String, JoinLeaveMessage> entry2) {
        return Integer.compare(rankValue(entry1), rankValue(entry2));
    }

    private int rankValue(Map.Entry<String, JoinLeaveMessage> entry) {
        return switch (entry.getValue().requiredRank().toLowerCase()) {
            case "recruit" -> 1;
            case "cadet" -> 2;
            case "researcher" -> 3;
            case "adventurer" -> 4;
            case "voyager" -> 5;
            case "rift_master" -> 6;
            case "bronze" -> 7;
            case "silver" -> 8;
            case "gold" -> 9;
            case "diamond" -> 10;
            case "team" -> 11;
            case "mod" -> 12;
            case "admin" -> 13;
            case "owner" -> 14;
            default -> Integer.MAX_VALUE;
        };
    }
}
