package parallelmc.parallelutils.modules.parallelquests.dialogue;

import java.util.ArrayList;
import java.util.List;

public class DialogueBuilder {
    private String text;
    private final List<DialogueOption> options = new ArrayList<>();
    private final List<DialogueAction> actions = new ArrayList<>();

    private DialogueBuilder() {}

    public static DialogueBuilder node() {
        return new DialogueBuilder();
    }

    public DialogueBuilder text(String text) {
        this.text = text;
        return this;
    }

    public DialogueBuilder action(DialogueAction action) {
        this.actions.add(action);
        return this;
    }

    public DialogueBuilder option(String optionText, DialogueBuilder next) {
        this.options.add(new DialogueOption(optionText, next.build()));
        return this;
    }

    public DialogueNode build() {
        return new DialogueNode(text, options, actions);
    }
}
