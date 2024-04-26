package parallelmc.parallelutils.modules.parallelcasino.cards;

import parallelmc.parallelutils.ParallelUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;

public class Shoe {
    private final ArrayList<Card> cards = new ArrayList<>();

    private final int shoeSize;

    public Shoe(int shoeSize) {
        this.shoeSize = shoeSize;
        if (this.shoeSize < 1) {
            ParallelUtils.log(Level.SEVERE, "Attempted to create a shoe with less than 1 decks");
        }
        buildShoe();
    }

    private void buildShoe() {
        for (int i = 0; i < shoeSize; i++) {
            for (Suit suit : Suit.values()) {
                for (Rank rank : Rank.values()) {
                    cards.add(new Card(suit, rank));
                }
            }
        }
    }

    public void shuffle() {
        int n = cards.size();
        for (int i = n - 1; i > 0; i--) {
            int j = (int)(Math.random() * (i + 1));
            Collections.swap(cards, i, j);
        }
    }

    @Nullable
    public Card drawCard() {
        if (isOutOfCards())
            return null;
        return cards.remove(0);
    }

    public boolean isOutOfCards() {
        return cards.isEmpty();
    }
}
