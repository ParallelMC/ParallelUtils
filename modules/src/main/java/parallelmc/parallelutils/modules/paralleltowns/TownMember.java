package parallelmc.parallelutils.modules.paralleltowns;

import java.util.UUID;

public class TownMember {
    private short townRank;
    // easier to do this than to try and finagle combining ranks
    private final boolean isFounder;

    public TownMember(short rank, boolean founder) {
        townRank = rank;
        isFounder = founder;
    }


    public short getTownRank() {
        return townRank;
    }

    public void setTownRank(short rank) {
        townRank = rank;
    }

    public boolean getIsFounder() {
        return isFounder;
    }

    public boolean promote() {
        if (townRank + 1 > TownRank.LEADER)
            return false;
        townRank++;
        return true;
    }

    public boolean demote() {
        if (townRank - 1 < TownRank.MEMBER)
            return false;
        townRank--;
        return true;
    }

}
