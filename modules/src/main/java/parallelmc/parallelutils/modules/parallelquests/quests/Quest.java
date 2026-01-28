package parallelmc.parallelutils.modules.parallelquests.quests;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.parallelquests.ParallelQuests;
import parallelmc.parallelutils.modules.parallelquests.dialogue.Dialogue;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public abstract class Quest {
    protected final String questId;
    protected final String questName;
    protected final List<String> questDescription;
    protected final HashMap<String, List<String>> questStages;

    public Quest(String questId, String questName, List<String> questDescription) {
        this.questId = questId;
        this.questName = questName;
        this.questDescription = questDescription;
        this.questStages = new HashMap<>();
    }

    public String getQuestId() { return questId; }
    public String getQuestName() { return questName; }
    public List<String> getQuestDescription() { return questDescription; }
    public List<String> getStageDescription(String stageId) { return questStages.getOrDefault(stageId, List.of()); }

    protected void registerStageDescription(String stageId, List<String> description) {
        if (questStages.putIfAbsent(stageId, description) != null) {
            ParallelUtils.log(Level.WARNING, "Duplicate stage description for stage ID " + stageId + " in quest " + questId + ", ignoring!");
        }
    }

    protected void registerDialogue(String dialogueId, Dialogue dialogue) {
        ParallelQuests.getConversationManager().registerDialogue(dialogueId, dialogue);
    }

    protected void showDialogue(Player player, String dialogueId) {
        ParallelQuests.getConversationManager().startConversation(player, dialogueId);
    }

    protected void setQuestStage(Player player, String stage) {
        ParallelQuests.get().setQuestStage(player, questId, stage);
    }

    protected void completeQuest(Player player) {
        setQuestStage(player, "completed");
        player.sendMessage(Component.text("Quest Completed: " + questName, NamedTextColor.GREEN));
        player.playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "entity.player.levelup"), Sound.Source.MASTER, 1f, 1f));
    }

    public abstract void init();

    public abstract void enter(Player player, String npcName);

    public abstract List<String> prerequisites();
}
