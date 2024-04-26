package parallelmc.parallelutils.modules.parallelcasino.games.blackjack;

public enum BlackjackResult {
    PLAYER_WIN("Player Win"),
    DEALER_WIN("Dealer Win"),
    PLAYER_BUST("Player Bust"),
    DEALER_BUST("Dealer Bust"),
    PUSH("Push"),
    PLAYER_BLACKJACK("Player Blackjack"),
    DEALER_BLACKJACK("Dealer Blackjack"),
    FIVE_CARD_RULE("Five Card Rule");

    private final String name;

    BlackjackResult(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
