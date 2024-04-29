package parallelmc.parallelutils.modules.parallelcasino.cards;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public class RankComparator implements Comparator<Card> {

    @Override
    public int compare(Card o1, Card o2) {
        return -o1.getRank().compareTo(o2.getRank());
    }
}
