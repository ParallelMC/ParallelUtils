package parallelmc.parallelutils.modules.parallelquests.dialogue;

public class Dialogue {
    private final String speaker;
    private final DialogueNode root;

    private Dialogue(String speaker, DialogueNode root) {
        this.speaker = speaker;
        this.root = root;
    }

    public static Dialogue withSpeaker(String speaker, DialogueNode root) {
       return new Dialogue(speaker, root);
    }

    public String getSpeaker() { return speaker; }
    public DialogueNode getRoot() { return root; }
}
