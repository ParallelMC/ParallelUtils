package parallelmc.parallelutils.modules.parallelcasino.games.deuceswild;

public enum DeucesWildResult {
    NATURAL_ROYAL_FLUSH("Royal Flush"),
    FOUR_DEUCES("Four Deuces"),
    WILD_ROYAL_FLUSH("Wild Royal Flush"),
    FIVE_OF_A_KIND("Five of a Kind"),
    STRAIGHT_FLUSH("Straight Flush"),
    FOUR_OF_A_KIND("Four of a Kind"),
    FULL_HOUSE("Full House"),
    FLUSH("Flush"),
    STRAIGHT("Straight"),
    THREE_OF_A_KIND("Three of a Kind"),
    NO_WIN("No Hand");

    private final String name;

    DeucesWildResult(String name) { this.name = name; }

    public String getName() { return name; }
}
