package parallelmc.parallelutils.modules.paralleltowns;

public class TownMember {
    private final String townName;
    private TownRank townRank;
    // easier to do this than to try and finagle combining ranks
    private final boolean isFounder;

    public TownMember(String name, TownRank rank, boolean founder) {
        townName = name;
        townRank = rank;
        isFounder = founder;
    }

    public String getTownName() {
        return townName;
    }

    public TownRank getTownRank() {
        return townRank;
    }

    public void setTownRank(TownRank rank) {
        townRank = rank;
    }

    public boolean getIsFounder() {
        return isFounder;
    }
}
