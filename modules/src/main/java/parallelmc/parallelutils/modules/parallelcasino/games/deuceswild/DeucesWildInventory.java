package parallelmc.parallelutils.modules.parallelcasino.games.deuceswild;

import com.google.common.collect.Lists;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.parallelcasino.cards.*;
import parallelmc.parallelutils.util.GUIInventory;

import java.util.*;
import java.util.logging.Level;

public class DeucesWildInventory extends GUIInventory {

    private Shoe shoe;

    private final ArrayList<Card> currentHand = new ArrayList<>();

    private final HashMap<Rank, Integer> handHelper = new HashMap<>();

    private final static ItemStack DRAW_BUTTON;
    private final static ItemStack HOLD_BUTTON;
    private final static ItemStack HOLD_PLACEHOLDER;

    static {
        DRAW_BUTTON = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta meta = DRAW_BUTTON.getItemMeta();
        meta.displayName(Component.text("Draw", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
        meta.lore(List.of(Component.text("Confirm your selections and draw", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)));
        DRAW_BUTTON.setItemMeta(meta);

        HOLD_BUTTON = new ItemStack(Material.LIME_DYE);
        meta = HOLD_BUTTON.getItemMeta();
        meta.displayName(Component.text("Held", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
        meta.lore(List.of(Component.text("This card is held. Click again to un-hold", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)));
        HOLD_BUTTON.setItemMeta(meta);

        HOLD_PLACEHOLDER = new ItemStack(Material.GRAY_DYE);
        meta = HOLD_PLACEHOLDER.getItemMeta();
        meta.displayName(Component.text("Not Held", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
        meta.lore(List.of(Component.text("Click to hold the card above", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)));
        HOLD_PLACEHOLDER.setItemMeta(meta);
    }

    public DeucesWildInventory() {
        super(54, Component.text("Deuces Wild"));

        for (int i = 0; i < 54; i++) {
            inventory.setItem(i, PLACEHOLDER_GREEN);
        }

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = head.getItemMeta();
        meta.displayName(Component.text("Dealer", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        head.setItemMeta(meta);
        inventory.setItem(13, head);

        init();
    }

    @Override
    public void onOpen(Player player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta)head.getItemMeta();
        meta.displayName(Component.text(player.getName(), NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        meta.setPlayerProfile(player.getPlayerProfile());
        head.setItemMeta(meta);
        inventory.setItem(40, head);
    }

    @Override
    public void onSlotClicked(Player player, int slotNum, ItemStack itemClicked) {
        if (slotNum > 28 && slotNum < 34) {
            if (itemClicked.getType() == Material.GRAY_DYE) {
                inventory.setItem(slotNum, HOLD_BUTTON);
            }
            else {
                inventory.setItem(slotNum, HOLD_PLACEHOLDER);
            }
        }
        if (slotNum == 25) {
            for (int i = 20; i < 25; i++) {
                ItemStack s = inventory.getItem(i + 9);
                if (s != null) {
                    if (s.getType() == Material.LIME_DYE) {
                        inventory.setItem(i + 9, HOLD_PLACEHOLDER);
                    }
                    else {
                        Card card = shoe.drawCard();
                        Card old = currentHand.get(i - 20);
                        handHelper.put(old.getRank(), handHelper.get(old.getRank()) - 1);
                        handHelper.put(card.getRank(), handHelper.get(card.getRank()) + 1);
                        currentHand.set(i - 20, card);
                        setSlotToCard(i, card);
                    }
                }
            }
            long start = System.currentTimeMillis();
            DeucesWildResult result = checkWin();
            ParallelUtils.log(Level.WARNING, "Got a " + result);
            long end = System.currentTimeMillis();
            ParallelUtils.log(Level.WARNING, "Calculated in " + (end - start) + "ms");
        }
    }

    private void init() {
        shoe = new Shoe(1);
        shoe.shuffle();

        currentHand.clear();
        for (Rank r : Rank.values()) {
            handHelper.put(r, 0);
        }

        for (int i = 20; i < 25; i++) {
            Card card = shoe.drawCard();
            currentHand.add(card);
            handHelper.put(card.getRank(), handHelper.get(card.getRank()) + 1);
            setSlotToCard(i, card);
        }

        for (int i = 29; i < 34; i++) {
            inventory.setItem(i, HOLD_PLACEHOLDER);
        }

        inventory.setItem(25, DRAW_BUTTON);
        long start = System.currentTimeMillis();
        DeucesWildResult result = checkWin();
        ParallelUtils.log(Level.WARNING, "Got a " + result);
        long end = System.currentTimeMillis();
        ParallelUtils.log(Level.WARNING, "Calculated in " + (end - start) + "ms");
    }
    private void setSlotToCard(int slot, Card card) {
        Suit suit = card.getSuit();
        ItemStack item;
        if (suit == Suit.HEART || suit == Suit.DIAMOND) {
            item = new ItemStack(Material.RED_STAINED_GLASS_PANE, card.getValue());
            ItemMeta meta = item.getItemMeta();
            meta.displayName(Component.text(card.toString(), NamedTextColor.RED).decoration(TextDecoration.ITALIC ,false));
            if (card.getRank() == Rank.TWO)
                meta.lore(List.of(Component.text("This card is WILD and counts as any card!", NamedTextColor.DARK_GRAY)));
            item.setItemMeta(meta);
        }
        else {
            item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, card.getValue());
            ItemMeta meta = item.getItemMeta();
            meta.displayName(Component.text(card.toString(), NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false));
            if (card.getRank() == Rank.TWO)
                meta.lore(List.of(Component.text("This card is WILD and counts as any card!", NamedTextColor.DARK_GRAY)));
            item.setItemMeta(meta);
        }
        inventory.setItem(slot, item);
    }

    private DeucesWildResult checkWin() {
        Card[] copy = new Card[5];
        currentHand.toArray(copy);
        Arrays.sort(copy, new RankComparator());

        ParallelUtils.log(Level.WARNING, "handHelper contains:");
        for (var e : handHelper.entrySet()) {
            ParallelUtils.log(Level.WARNING, e.getKey() + " | " + e.getValue());
        }

        // while this looks really ugly and slow
        // it apparently finishes in under 1ms worst case
        // so I'll leave it for now

        boolean hasDeuces = handHelper.get(Rank.TWO) > 0;
        if (isRoyalFlush(copy, hasDeuces))
            return DeucesWildResult.NATURAL_ROYAL_FLUSH;
        if (isFourDeuces())
            return DeucesWildResult.FOUR_DEUCES;
        if (isWildRoyalFlush(copy, hasDeuces))
            return DeucesWildResult.WILD_ROYAL_FLUSH;
        if (isFiveOfAKind(hasDeuces))
            return DeucesWildResult.FIVE_OF_A_KIND;
        if (isStraightFlush(copy, hasDeuces))
            return DeucesWildResult.STRAIGHT_FLUSH;
        if (isFourOfAKind(hasDeuces))
            return DeucesWildResult.FOUR_OF_A_KIND;
        if (isFullHouse(hasDeuces))
            return DeucesWildResult.FULL_HOUSE;
        if (isFlush(copy, hasDeuces))
            return DeucesWildResult.FLUSH;
        if (isStraight(copy))
            return DeucesWildResult.STRAIGHT;
        if (isThreeOfAKind(hasDeuces))
            return DeucesWildResult.THREE_OF_A_KIND;
        return DeucesWildResult.NO_WIN;
    }

    private boolean isRoyalFlush(Card[] hand, boolean hasDeuces) {
        return isFlush(hand, hasDeuces) &&
                handHelper.get(Rank.TEN) == 1 &&
                handHelper.get(Rank.JACK) == 1 &&
                handHelper.get(Rank.QUEEN) == 1 &&
                handHelper.get(Rank.KING) == 1 &&
                handHelper.get(Rank.ACE) == 1;
    }

    private boolean isFourDeuces() {
        return handHelper.get(Rank.TWO) == 4;
    }

    private boolean isWildRoyalFlush(Card[] hand, boolean hasDeuces) {
        return hasDeuces && isFlush(hand, true) && handHelper.entrySet().stream().filter(x -> x.getKey() != Rank.TWO).allMatch(x -> x.getValue() == 1 && x.getKey().ordinal() < 5);
    }

    private boolean isFiveOfAKind(boolean hasDeuces) {
        if (!hasDeuces) return false;
        var first = handHelper.entrySet().stream().filter(x -> x.getKey() != Rank.TWO).findFirst();
        return first.filter(x -> x.getValue() + handHelper.get(Rank.TWO) == 5).isPresent();
    }

    private boolean isStraightFlush(Card[] hand, boolean hasDeuces) {
        return isFlush(hand, hasDeuces) && isStraight(hand);
    }

    private boolean isFourOfAKind(boolean hasDeuces) {
        if (hasDeuces) {
            int deuces = handHelper.get(Rank.TWO);
            return handHelper.entrySet().stream().filter(x -> x.getKey() != Rank.TWO).anyMatch(x -> x.getValue() + deuces == 4);
        }
        else {
            return handHelper.values().stream().anyMatch(x -> x == 4);
        }
    }

    private boolean isFullHouse(boolean hasDeuces) {
        // TODO: fix, currently includes previously used cards in calculation leading to false results
       return isThreeOfAKind(hasDeuces) && handHelper.entrySet().stream().filter(x -> x.getKey() != Rank.TWO).anyMatch(x -> x.getValue() == 2);
    }

    private boolean isFlush(Card[] hand, boolean hasDeuces) {
        if (hasDeuces) {
            Suit lastSeen = null;
            for (Card c : hand) {
                if (c.getRank() == Rank.TWO) continue;
                if (lastSeen == null) {
                    lastSeen = c.getSuit();
                    continue;
                }
                if (c.getSuit() != lastSeen)
                    return false;
            }
            return true;
        }
        else {
            Suit suit = hand[0].getSuit();
            return Arrays.stream(hand).allMatch(x -> x.getSuit() == suit);
        }
    }

    private boolean isStraight(Card[] hand) {
        // since the hand is sorted in ascending order
        // we can just check if the next card is greater or a deuce
        for (int i = 0; i < 4; i++) {
            if (hand[i].getRank().ordinal() - 1 == hand[i + 1].getRank().ordinal() || hand[i].getRank() == Rank.TWO)
                continue;
            return false;
        }
        return true;
    }

    private boolean isThreeOfAKind(boolean hasDeuces) {
        if (hasDeuces) {
            int deuces = handHelper.get(Rank.TWO);
            return handHelper.entrySet().stream().filter(x -> x.getKey() != Rank.TWO).anyMatch(x -> x.getValue() + deuces == 3);
        }
        else {
            return handHelper.values().stream().anyMatch(x -> x == 3);
        }
    }

}
