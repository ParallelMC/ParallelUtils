package parallelmc.parallelutils.modules.parallelcasino.cards;

public record Card(Suit suit, Rank rank) {

    public int getValue() {
        return rank.getValue();
    }

    public String toString() {
        return rank.getName() + " of " + suit.getName();
    }

}

