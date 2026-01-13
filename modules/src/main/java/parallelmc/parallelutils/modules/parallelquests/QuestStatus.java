package parallelmc.parallelutils.modules.parallelquests;

public class QuestStatus {
    private final String questId;
    private boolean completed;

    public QuestStatus(String questId, boolean completed) {
        this.questId = questId;
        this.completed = completed;
    }

    public String getQuestId() {
        return questId;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void markCompleted() {
        completed = true;
    }
}