package parallelmc.parallelutils.modules.parallelquests.dialogue;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.map.MinecraftFont;

public class Conversation {
    private final Dialogue dialogue;
    private DialogueNode current;
    private static final int CENTER = 120;
    private static final int SPACE_WIDTH = 3;

    public Conversation(Dialogue dialogue) {
        this.dialogue = dialogue;
        this.current = dialogue.getRoot();
    }

    public void enter(Player player) {
        current.onEnter(player);
        display(player);
    }

    public void choose(Player player, int index) {
        current = current.getOptions().get(index).getNext();
        enter(player);
    }

    private void display(Player player) {
        Component message = Component
                .text(centerText(dialogue.getSpeaker()), NamedTextColor.YELLOW)
                .appendNewline()
                .append(Component.text(current.getText(), NamedTextColor.WHITE))
                .appendNewline();
        for (int i = 0; i < current.getOptions().size(); i++) {
            DialogueOption option = current.getOptions().get(i);
            message = message.append(Component.text(" ".repeat(5) + option.getText(), NamedTextColor.AQUA)
                    .clickEvent(ClickEvent.runCommand("/dialogueoption " + i))
                    .hoverEvent(HoverEvent.showText(Component.text("Click to Select Option " + (i + 1), NamedTextColor.YELLOW)))
                    ).appendNewline();
        }
        player.sendMessage(message);
    }

    public boolean isFinished() { return !current.hasNext(); }

    private String centerText(String input) {
        int messageSize = MinecraftFont.Font.getWidth(input);
        int padding = (CENTER - messageSize / 2) / SPACE_WIDTH;
        return " ".repeat(padding) + input;
    }
}
