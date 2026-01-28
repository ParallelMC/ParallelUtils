package parallelmc.parallelutils.modules.parallelquests.dialogue;

import net.kyori.adventure.text.minimessage.MiniMessage;
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
        StringBuilder sb = new StringBuilder();
        sb.append("<yellow>").append(centerText(dialogue.getSpeaker())).append("</yellow><br>")
                .append(current.getText()).append("<br>");

        for (int i = 0; i < current.getOptions().size(); i++) {
            DialogueOption option = current.getOptions().get(i);
            sb.append("<hover:show_text:'<yellow>Click to select option ").append(i + 1)
                    .append("</yellow>'><click:run_command:dialogueoption ").append(i).append("><aqua>")
                    .append(" ".repeat(5)).append(option.getText()).append("</aqua><br>");
        }
        player.sendMessage(MiniMessage.miniMessage().deserialize(sb.toString()));
    }

    public boolean isFinished() { return !current.hasNext(); }

    private String centerText(String input) {
        int messageSize = MinecraftFont.Font.getWidth(input);
        int padding = (CENTER - messageSize / 2) / SPACE_WIDTH;
        return " ".repeat(padding) + input;
    }
}
