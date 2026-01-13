package parallelmc.parallelutils.modules.parallelquests.dialogue;

import org.bukkit.entity.Player;

import java.util.List;

public class DialogueNode {
    private final String text;
    private final List<DialogueOption> options;
    private final List<DialogueAction> actions;

    DialogueNode(String text, List<DialogueOption> options, List<DialogueAction> actions) {
        this.text = text;
        this.options = List.copyOf(options);
        this.actions = List.copyOf(actions);
    }

    public String getText() { return text; }
    public List<DialogueOption> getOptions() { return options; }

    public void onEnter(Player player) {
        for (DialogueAction action : actions)
            action.execute(player);
    }

    public boolean hasNext() {
        return !options.isEmpty();
    }
}
