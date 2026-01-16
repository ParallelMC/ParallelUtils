package parallelmc.parallelutils.modules.parallelquests.quests;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import parallelmc.parallelutils.modules.parallelquests.ParallelQuests;
import parallelmc.parallelutils.modules.parallelquests.dialogue.Dialogue;

public abstract class Quest {
    protected final String questId;
    protected final String questName;
    protected final String questDescription;

    public Quest(String questId, String questName, String questDescription) {
        this.questId = questId;
        this.questName = questName;
        this.questDescription = questDescription;
    }

    public String getQuestId() { return questId; }
    public String getQuestName() { return questName; }
    public String getQuestDescription() { return questDescription; }

    protected void registerDialogue(String dialogueId, Dialogue dialogue) {
        ParallelQuests.getConversationManager().registerDialogue(dialogueId, dialogue);
    }

    protected void showDialogue(Player player, String dialogueId) {
        ParallelQuests.getConversationManager().startConversation(player, dialogueId);
    }

    protected void setQuestStage(Player player, String stage) {
        ParallelQuests.get().setQuestStage(player.getUniqueId(), questId, stage);
    }

    protected void completeQuest(Player player) {
        setQuestStage(player, "completed");
        player.sendMessage(Component.text("Quest Completed: " + questName, NamedTextColor.GREEN));
        player.playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "entity.player.levelup"), Sound.Source.MASTER, 1f, 1f));
    }

    public abstract void init();

    public abstract void enter(Player player);
}
