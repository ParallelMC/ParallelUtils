package parallelmc.parallelutils.modules.parallelquests.dialogue;

public class DialogueOption {
    private final String text;
    private final DialogueNode next;

    public DialogueOption(String text, DialogueNode next) {
        this.text = text;
        this.next = next;
    }

    public String getText() { return text; }
    public DialogueNode getNext() { return next; }
}
