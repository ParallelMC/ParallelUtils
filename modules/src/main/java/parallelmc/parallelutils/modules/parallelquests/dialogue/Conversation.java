package parallelmc.parallelutils.modules.parallelquests.dialogue;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public class Conversation {
    private final Dialogue dialogue;
    private DialogueNode current;

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
        player.sendMessage(Component.text(dialogue.getSpeaker() + ": " + current.getText()));
        for (int i = 0; i < current.getOptions().size(); i++) {
            player.sendMessage(Component.text("[" + current.getOptions().get(i).getText() + "]", NamedTextColor.AQUA));
        }
    }

    public boolean isFinished() { return !current.hasNext(); }
}
