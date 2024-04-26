package parallelmc.parallelutils.modules.parallelcasino.cards;

public enum Suit {
    CLUB("Clubs"),
    DIAMOND("Diamonds"),
    HEART("Hearts"),
    SPADE("Spades");

    private final String name;

    Suit(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
