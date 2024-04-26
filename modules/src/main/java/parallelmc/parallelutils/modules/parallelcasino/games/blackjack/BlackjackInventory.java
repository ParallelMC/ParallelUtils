package parallelmc.parallelutils.modules.parallelcasino.games.blackjack;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.parallelcasino.ParallelCasino;
import parallelmc.parallelutils.modules.parallelcasino.cards.Card;
import parallelmc.parallelutils.modules.parallelcasino.cards.Rank;
import parallelmc.parallelutils.modules.parallelcasino.cards.Shoe;
import parallelmc.parallelutils.modules.parallelcasino.cards.Suit;
import parallelmc.parallelutils.util.GUIInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class BlackjackInventory extends GUIInventory {

    /** TODO:
     * - Splitting
     * - Surrender
     * - Reshuffle shoe
     */

    private final Shoe shoe;

    private final ArrayList<Card> playerHand = new ArrayList<>();
    private final ArrayList<Card> dealerHand = new ArrayList<>();

    private final static ItemStack HIT_BUTTON;
    private final static ItemStack STAND_BUTTON;
    private final static ItemStack DOUBLE_BUTTON;
    private final static ItemStack LEAVE_BUTTON;

    static {
        HIT_BUTTON = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta meta = HIT_BUTTON.getItemMeta();
        meta.displayName(Component.text("Hit", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
        meta.lore(List.of(Component.text("Draw another card", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)));
        HIT_BUTTON.setItemMeta(meta);

        STAND_BUTTON = new ItemStack(Material.RED_CONCRETE);
        meta = STAND_BUTTON.getItemMeta();
        meta.displayName(Component.text("Stand", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        meta.lore(List.of(Component.text("Take no more cards", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)));
        STAND_BUTTON.setItemMeta(meta);

        DOUBLE_BUTTON = new ItemStack(Material.LIGHT_BLUE_CONCRETE);
        meta = DOUBLE_BUTTON.getItemMeta();
        meta.displayName(Component.text("Double Down", NamedTextColor.BLUE).decoration(TextDecoration.ITALIC, false));
        meta.lore(List.of(Component.text("Double your bet and draw only one card").decoration(TextDecoration.ITALIC, false)));
        DOUBLE_BUTTON.setItemMeta(meta);

        LEAVE_BUTTON = new ItemStack(Material.BARRIER);
        meta = LEAVE_BUTTON.getItemMeta();
        meta.displayName(Component.text("Quit", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        LEAVE_BUTTON.setItemMeta(meta);
    }

    public BlackjackInventory() {
        super(54, Component.text("Blackjack"));

        for (int i = 0; i < 54; i++) {
            inventory.setItem(i, PLACEHOLDER_GREEN);
        }

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = head.getItemMeta();
        meta.displayName(Component.text("Dealer", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        head.setItemMeta(meta);
        inventory.setItem(13, head);

        inventory.setItem(29, DOUBLE_BUTTON);
        inventory.setItem(30, HIT_BUTTON);
        inventory.setItem(32, STAND_BUTTON);

        shoe = new Shoe(6);
        shoe.shuffle();
    }

    @Override
    public void onOpen(Player player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta)head.getItemMeta();
        meta.displayName(Component.text(player.getName(), NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        meta.setPlayerProfile(player.getPlayerProfile());
        head.setItemMeta(meta);
        inventory.setItem(31, head);

        init(player);
    }

    @Override
    public void onSlotClicked(Player player, int slotNum, ItemStack itemClicked) {
        Material clicked = itemClicked.getType();
        // double down
        if (slotNum == 29) {
            if (clicked == Material.LIGHT_BLUE_CONCRETE) {
                drawPlayerCard();
                updatePlayerSkull(player);
                checkShouldEndGame(player);
                inventory.setItem(30, PLACEHOLDER_GREEN);
            }
        }
        // hit or replay
        if (slotNum == 30) {
            if (clicked == Material.LIME_CONCRETE) {
                // if the player doesn't double down then prevent them from doing so after hitting
                if (playerHand.size() == 2) {
                    inventory.setItem(29, PLACEHOLDER_GREEN);
                }
                drawPlayerCard();
                updatePlayerSkull(player);
                checkShouldEndGame(player);
            }
            else if (clicked == Material.NAME_TAG) {
                inventory.setItem(29, DOUBLE_BUTTON);
                inventory.setItem(30, HIT_BUTTON);
                inventory.setItem(32, STAND_BUTTON);
                init(player);
            }
        }
        // stand or quit
        if (slotNum == 32) {
            if (clicked == Material.RED_CONCRETE) {
                while (currentDealerValue() < 17)
                    drawDealerCard();
                updateDealerSkull();
                int dealer = currentDealerValue();
                int play = currentPlayerValue();
                if (dealer > 21)
                    displayGameResult(BlackjackResult.DEALER_BUST, player);
                else if (dealer > play)
                    displayGameResult(BlackjackResult.DEALER_WIN, player);
                else if (play > dealer)
                    displayGameResult(BlackjackResult.PLAYER_WIN, player);
                else
                    displayGameResult(BlackjackResult.PUSH, player);
            }
            else if (clicked == Material.BARRIER) {
                ParallelCasino.get().removePlayerFromGame(player);
                player.closeInventory();
            }
        }
    }

    private void checkShouldEndGame(Player player) {
        int value = currentPlayerValue();
        if (value == 21) {
            // prevent player from hitting at 21
            inventory.setItem(30, PLACEHOLDER_GREEN);
        }
        else if (value > 21) {
            displayGameResult(BlackjackResult.PLAYER_BUST, player);
        }
        else if (playerHand.size() >= 5) {
            displayGameResult(BlackjackResult.FIVE_CARD_RULE, player);
        }
    }

    private void init(Player player) {
        resetCards();
        playerHand.clear();
        dealerHand.clear();

        drawPlayerCard();
        drawDealerCard();
        drawPlayerCard();
        updatePlayerSkull(player);
        updateDealerSkull();

        // the dealer's second card is initially hidden
        // so draw another card but hide it and don't update the dealer's score
        drawDealerCard();
        ItemStack hidden = new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
        ItemMeta hmeta = hidden.getItemMeta();
        hmeta.displayName(Component.text("Face Down Card", NamedTextColor.DARK_AQUA).decoration(TextDecoration.ITALIC, false));
        hidden.setItemMeta(hmeta);
        inventory.setItem(22, hidden);

        if (currentPlayerValue() == 21) {
            displayGameResult(BlackjackResult.PLAYER_BLACKJACK, player);
        }
        else if (currentDealerValue() == 21) {
            displayGameResult(BlackjackResult.DEALER_BLACKJACK, player);
        }
    }

    private void drawPlayerCard() {
        playerHand.add(shoe.drawCard());
        for (int i = 0; i < playerHand.size(); i++) {
            setSlotToCard(41 - i, playerHand.get(i));
        }

    }

    private void drawDealerCard() {
        dealerHand.add(shoe.drawCard());
        showDealerCards();
    }

    private void showDealerCards() {
        for (int i = 0; i < dealerHand.size(); i++) {
            setSlotToCard(23 - i, dealerHand.get(i));
        }
    }

    private void resetCards() {
        for (int i = 0; i < playerHand.size(); i++) {
            inventory.setItem(41 - i, PLACEHOLDER_GREEN);
        }
        for (int i = 0; i < dealerHand.size(); i++) {
            inventory.setItem(23 - i, PLACEHOLDER_GREEN);
        }
    }

    private int currentPlayerValue() {
        int value = 0;
        for (Card c : playerHand) {
            if (c.getRank() == Rank.ACE) {
                if (value + 11 > 21)
                    value += 1;
                else
                    value += 11;
            }
            else {
                value += c.getValue();
            }
        }
        return value;
    }

    private int currentDealerValue() {
        int value = 0;
        for (Card c : dealerHand) {
            if (c.getRank() == Rank.ACE) {
                if (value + 11 > 21)
                    value += 1;
                else
                    value += 11;
            }
            else {
                value += c.getValue();
            }
        }
        return value;
    }


    private void displayGameResult(BlackjackResult result, Player player) {
        showDealerCards();
        ItemStack item = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = item.getItemMeta();
        switch (result) {
            case PLAYER_WIN -> {
                meta.displayName(Component.text("Player Win", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
            }
            case DEALER_WIN -> {
                meta.displayName(Component.text("Dealer Win", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
                player.playSound(player.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1f, 1f);
            }
            case DEALER_BUST -> {
                meta.displayName(Component.text("Dealer Bust", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
            }
            case PLAYER_BUST -> {
                meta.displayName(Component.text("Player Bust", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
                player.playSound(player.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1f, 1f);
            }
            case PUSH -> {
                meta.displayName(Component.text("Push", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1f);
            }
            case PLAYER_BLACKJACK -> {
                meta.displayName(Component.text("Player Blackjack", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
            }
            case DEALER_BLACKJACK -> {
                meta.displayName(Component.text("Dealer Blackjack", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
                player.playSound(player.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1f, 1f);
            }
            case FIVE_CARD_RULE -> {
                meta.displayName(Component.text("Five Card Rule", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1f);
            }
        }
        meta.lore(List.of(Component.text("Click to play again!", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)));
        item.setItemMeta(meta);
        inventory.setItem(30, item);

        inventory.setItem(32, LEAVE_BUTTON);
    }

    private void updatePlayerSkull(Player player) {
        ItemStack head = inventory.getItem(31);
        if (head == null) {
            ParallelUtils.log(Level.SEVERE, "updatePlayerSkull failed...item is null!");
            return;
        }
        ItemMeta meta = head.getItemMeta();
        meta.displayName(Component.text(player.getName() + " | " + currentPlayerValue(), NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        head.setItemMeta(meta);
        inventory.setItem(31, head);
    }

    private void updateDealerSkull() {
        ItemStack head = inventory.getItem(13);
        if (head == null) {
            ParallelUtils.log(Level.SEVERE, "updateDealerSkull failed...item is null!");
            return;
        }
        ItemMeta meta = head.getItemMeta();
        meta.displayName(Component.text("Dealer | " + currentDealerValue(), NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        head.setItemMeta(meta);
        inventory.setItem(13, head);
    }

    private void setSlotToCard(int slot, Card card) {
        Suit suit = card.getSuit();
        ItemStack item;
        if (suit == Suit.HEART || suit == Suit.DIAMOND) {
            item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            ItemMeta meta = item.getItemMeta();
            meta.displayName(Component.text(card.toString(), NamedTextColor.RED).decoration(TextDecoration.ITALIC ,false));
            item.setItemMeta(meta);
        }
        else {
            item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta meta = item.getItemMeta();
            meta.displayName(Component.text(card.toString(), NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false));
            item.setItemMeta(meta);
        }
        inventory.setItem(slot, item);
    }
}
